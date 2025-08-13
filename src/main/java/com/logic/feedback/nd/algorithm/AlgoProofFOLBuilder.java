package com.logic.feedback.nd.algorithm;

import com.logic.api.IFormula;
import com.logic.api.INDProof;
import com.logic.exps.asts.others.AASTTerm;
import com.logic.feedback.nd.algorithm.proofs.IProofGraph;
import com.logic.feedback.nd.algorithm.proofs.ProofGraphFOL;
import com.logic.feedback.nd.algorithm.proofs.ProofGraphSettings;
import com.logic.feedback.nd.algorithm.proofs.Solution;
import com.logic.feedback.nd.algorithm.transition.ITransitionGraph;
import com.logic.feedback.nd.algorithm.transition.TransitionGraphFOL;
import com.logic.nd.ERule;
import com.logic.others.Utils;

import java.util.HashSet;
import java.util.Set;

public class AlgoProofFOLBuilder {

    private final AlgoProofFOLMainGoalBuilder problem;
    private AlgoProofFOLGoalBuilder goal;

    private final Set<ERule> forbiddenRules;
    private AlgoSettingsBuilder algoSettingsBuilder = new AlgoSettingsBuilder();

    public AlgoProofFOLBuilder(AlgoProofFOLMainGoalBuilder problem) {
        this.problem = problem;
        this.forbiddenRules = new HashSet<>();
    }

    public AlgoProofFOLBuilder addForbiddenRule(ERule forbiddenRule) {
        this.forbiddenRules.add(forbiddenRule);
        return this;
    }

    public AlgoProofFOLBuilder addForbiddenRules(Set<ERule> forbiddenRules) {
        this.forbiddenRules.addAll(forbiddenRules);
        return this;
    }

    public AlgoProofFOLBuilder setGoal(AlgoProofFOLGoalBuilder goal) {
        this.goal = goal;
        return this;
    }

    public AlgoProofFOLBuilder setAlgoSettingsBuilder(AlgoSettingsBuilder algoSettingsBuilder) {
        this.algoSettingsBuilder = algoSettingsBuilder;
        return this;
    }

    public IProofGraph buildGraph() {
        if (goal == null)
            goal = problem;

        Set<AASTTerm> terms = new HashSet<>(problem.terms);
        terms.addAll(goal.terms);

        ProofGraphSettings s = algoSettingsBuilder.build();
        Set<IFormula> expressions = new HashSet<>(problem.premises);
        expressions.add(problem.goal);
        expressions.addAll(problem.hypotheses);
        expressions.addAll(goal.hypotheses);
        expressions.add(goal.goal);

        ITransitionGraph tg = new TransitionGraphFOL(expressions, forbiddenRules, terms);
        tg.build();

        //System.out.println(Utils.getToken(tg.toString()));

        IProofGraph pg = new ProofGraphFOL(problem, goal, tg, s);
        pg.build();

        //System.out.println(Utils.getToken(pg.toString()));

        return pg;
    }

    public INDProof build() {
        return new Solution(buildGraph(), true).findSolution();
    }

}
