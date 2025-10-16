package com.logic.feedback.nd.algorithm.tests;

import com.logic.api.IFormula;
import com.logic.api.INDProof;
import com.logic.exps.asts.IASTExp;
import com.logic.exps.exceptions.ExpException;
import com.logic.feedback.nd.algorithm.proofs.GoalNode;
import com.logic.feedback.nd.algorithm.proofs.IProofGraph;
import com.logic.feedback.nd.algorithm.proofs.ProofEdge;
import com.logic.nd.NDProofs;
import com.logic.nd.asts.IASTND;
import com.logic.nd.asts.binary.ASTEExist;
import com.logic.nd.asts.binary.ASTEImp;
import com.logic.nd.asts.binary.ASTENeg;
import com.logic.nd.asts.binary.ASTIConj;
import com.logic.nd.asts.others.ASTEDis;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.asts.unary.*;
import com.logic.nd.checkers.NDMarksChecker;
import com.logic.nd.checkers.NDWWFChecker;
import com.logic.nd.checkers.NDWWFExpsChecker;
import com.logic.nd.exceptions.NDRuleException;
import com.logic.nd.interpreters.NDInterpreter;
import com.logic.others.Env;
import com.logic.others.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BinarySolution {


    private final BinaryMap map;
    private final Map<BinaryGoalObj, BinaryTransitionEdge> graph;
    private int mark;

    public BinarySolution(Map<BinaryGoalObj, BinaryTransitionEdge> graph, BinaryMap map) {
        this.map = map;
        this.graph = graph;
    }

    public INDProof findSolution(BinaryGoalObj initialGoal) {
        mark = 1;
        Env<IASTExp, String> marks = new Env<>();

        if (!graph.containsKey(initialGoal))
            throw new RuntimeException("Solution not found!");

        IASTND proof = rule(initialGoal, marks);
        if (proof == null)
            throw new RuntimeException("Solution not found!");

        return NDProofs.verifyNDPLProof(proof);
    }

    private IASTND rule(BinaryGoalObj goal, Env<IASTExp, String> marks) {
        BinaryTransitionEdge edge = graph.get(goal);
        IASTExp exp = map.getFormulas(goal.getExp()).getAST();

        if (edge == null)
            return new ASTHypothesis(exp, marks.findParent(exp));

        List<BinaryTransitionNode> transitions = edge.getTransitions();
        return switch (edge.getRule()) {
            case INTRO_CONJUNCTION -> introConjunction(transitions, marks, exp, goal);
            case ELIM_CONJUNCTION_LEFT -> elimConjunctionLeft(transitions, marks, exp, goal);
            case ELIM_CONJUNCTION_RIGHT -> elimConjunctionRight(transitions, marks, exp, goal);
            case INTRO_DISJUNCTION_LEFT -> introDisjunctionLeft(transitions, marks, exp, goal);
            case INTRO_DISJUNCTION_RIGHT -> introDisjunctionRight(transitions, marks, exp, goal);
            case ELIM_DISJUNCTION -> elimDisjunction(transitions, marks, exp, goal);
            case INTRO_IMPLICATION -> introImplication(transitions, marks, exp, goal);
            case INTRO_NEGATION -> introNegation(transitions, marks, exp, goal);
            case ELIM_IMPLICATION -> elimImplication(transitions, marks, exp, goal);
            case ABSURDITY -> absurdity(transitions, marks, exp, goal);
            case ELIM_NEGATION -> elimNegation(transitions, marks, exp, goal);
            default -> null;
        };
    }

    private BinaryGoalObj transit(BinaryGoalObj goal, BinaryTransitionNode transition) {
        return goal.transit(transition.getTo(), transition.getProduces(), map);
    }

    private IASTND introConjunction(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                    BinaryGoalObj goal) {
        IASTND first = rule(transit(goal, transitions.get(0)), marks);
        IASTND second = rule(transit(goal, transitions.get(1)), marks);
        return new ASTIConj(first, second, exp);
    }

    private IASTND elimConjunctionLeft(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                       BinaryGoalObj goal) {
        IASTND first = rule(transit(goal, transitions.get(0)), marks);
        return new ASTELConj(first, exp);
    }

    private IASTND elimConjunctionRight(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                        BinaryGoalObj goal) {
        IASTND first = rule(transit(goal, transitions.get(0)), marks);
        return new ASTERConj(first, exp);
    }

    private IASTND introDisjunctionLeft(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                        BinaryGoalObj goal) {
        IASTND first = rule(transit(goal, transitions.get(0)), marks);
        return new ASTILDis(first, exp);
    }

    private IASTND introDisjunctionRight(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                         BinaryGoalObj goal) {
        IASTND first = rule(transit(goal, transitions.get(0)), marks);
        return new ASTIRDis(first, exp);
    }

    private IASTND elimDisjunction(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                   BinaryGoalObj goal) {
        Env<IASTExp, String> envM = marks.beginScope();
        Env<IASTExp, String> envN = marks.beginScope();

        IASTExp m = map.getFormulas(transitions.get(1).getProduces()).getAST();
        IASTExp n = map.getFormulas(transitions.get(2).getProduces()).getAST();
        if(m != null) envM.bind(m, String.valueOf(mark++));
        if(n != null) envN.bind(n, String.valueOf(mark++));

        IASTND first = rule(transit(goal, transitions.get(0)), marks);
        IASTND second = rule(transit(goal, transitions.get(1)), envM);
        IASTND third = rule(transit(goal, transitions.get(2)), envN);
        return new ASTEDis(first, second, third, exp, envM.findParent(m), envN.findParent(n));
    }

    private IASTND introImplication(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                    BinaryGoalObj goal) {
        Env<IASTExp, String> envM = marks.beginScope();

        IASTExp m = map.getFormulas(transitions.get(0).getProduces()).getAST();
        if(m != null) envM.bind(m, String.valueOf(mark++));

        IASTND first = rule(transit(goal, transitions.get(0)), envM);
        return new ASTIImp(first, exp, envM.findParent(m));
    }

    private IASTND introNegation(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                 BinaryGoalObj goal) {
        Env<IASTExp, String> envM = marks.beginScope();

        IASTExp m = map.getFormulas(transitions.get(0).getProduces()).getAST();
        if(m != null) envM.bind(m, String.valueOf(mark++));

        IASTND first = rule(transit(goal, transitions.get(0)), envM);
        return new ASTINeg(first, exp, envM.findParent(m));
    }

    private IASTND elimImplication(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                   BinaryGoalObj goal) {
        IASTND first = rule(transit(goal, transitions.get(0)), marks);
        IASTND second = rule(transit(goal, transitions.get(1)), marks);
        return new ASTEImp(first, second, exp);
    }

    private IASTND absurdity(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                             BinaryGoalObj goal) {
        Env<IASTExp, String> envM = marks.beginScope();

        IASTExp m = map.getFormulas(transitions.get(0).getProduces()).getAST();
        if(m != null) envM.bind(m, String.valueOf(mark++));

        IASTND first = rule(transit(goal, transitions.get(0)), envM);
        return new ASTAbsurdity(first, exp, envM.findParent(m));
    }

    private IASTND elimNegation(List<BinaryTransitionNode> transitions, Env<IASTExp, String> marks, IASTExp exp,
                                BinaryGoalObj goal) {
        IASTND first = rule(transit(goal, transitions.get(0)), marks);
        IASTND second = rule(transit(goal, transitions.get(1)), marks);
        return new ASTENeg(first, second, exp);
    }

}
