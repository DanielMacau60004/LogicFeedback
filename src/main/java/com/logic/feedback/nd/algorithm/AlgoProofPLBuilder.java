package com.logic.feedback.nd.algorithm;

import com.logic.api.IFormula;
import com.logic.api.INDProof;
import com.logic.feedback.nd.algorithm.proofs.IProofGraph;
import com.logic.feedback.nd.algorithm.proofs.ProofGraphPL;
import com.logic.feedback.nd.algorithm.proofs.ProofGraphSettings;
import com.logic.feedback.nd.algorithm.proofs.Solution;
import com.logic.feedback.nd.algorithm.transition.ITransitionGraph;
import com.logic.feedback.nd.algorithm.transition.TransitionGraphPL;
import com.logic.nd.ERule;

import java.util.HashSet;
import java.util.Set;

public class AlgoProofPLBuilder {

    private final AlgoProofPLMainGoalBuilder problem;
    private AlgoProofPLGoalBuilder goal;

    private final Set<ERule> forbiddenRules;
    private AlgoSettingsBuilder algoSettingsBuilder = new AlgoSettingsBuilder();

    public AlgoProofPLBuilder(AlgoProofPLMainGoalBuilder problem) {
        this.problem = problem;
        this.forbiddenRules = new HashSet<>();
    }

    public AlgoProofPLBuilder addForbiddenRule(ERule forbiddenRule) {
        this.forbiddenRules.add(forbiddenRule);
        return this;
    }

    public AlgoProofPLBuilder addForbiddenRules(Set<ERule> forbiddenRules) {
        this.forbiddenRules.addAll(forbiddenRules);
        return this;
    }

    public AlgoProofPLBuilder setGoal(AlgoProofPLGoalBuilder goal) {
        this.goal = goal;
        return this;
    }

    public AlgoProofPLBuilder setAlgoSettingsBuilder(AlgoSettingsBuilder algoSettingsBuilder) {
        this.algoSettingsBuilder = algoSettingsBuilder;
        return this;
    }

    public IProofGraph buildGraph() {
        if (goal == null)
            goal = problem;

        ProofGraphSettings s = algoSettingsBuilder.build();
        Set<IFormula> expressions = new HashSet<>(problem.premises);
        expressions.add(problem.goal);
        expressions.addAll(problem.hypotheses);
        expressions.addAll(goal.hypotheses);
        expressions.add(goal.goal);

        ITransitionGraph tg = new TransitionGraphPL(expressions, forbiddenRules);
        tg.build();

        //System.out.println(Utils.getToken(tg.toString()));

        IProofGraph sg = new ProofGraphPL(problem, goal, tg, s);
        sg.build();

        return sg;
    }

    public INDProof build() {
        return new Solution(buildGraph(), false).findSolution();
    }

}
