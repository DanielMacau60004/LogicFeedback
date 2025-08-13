package com.logic.feedback.nd.algorithm.proofs;

import com.logic.feedback.nd.algorithm.AlgoProofPLMainGoalBuilder;
import com.logic.feedback.nd.algorithm.AlgoProofPLGoalBuilder;
import com.logic.feedback.nd.algorithm.transition.ITransitionGraph;

import java.util.*;

public class ProofGraphPL extends ProofGraph {

    public ProofGraphPL(AlgoProofPLMainGoalBuilder problem, AlgoProofPLGoalBuilder state,
                        ITransitionGraph transitionGraph, ProofGraphSettings settings) {
        super(new HashSet<>(problem.premises), transitionGraph, settings);

        this.mainGoal = problem.build(handler);
        targetGoal = state.build(handler);
    }

    @Override
    public String toString() {
        String str = "";
        str += "Total nodes: " + tree.size() + "\n";
        str += "Total edges: " + tree.values().stream().filter(Objects::nonNull).mapToLong(t -> t.getTransitions().size()).sum() + "\n";
        for (Map.Entry<GoalNode, ProofEdge> entry : tree.entrySet())
            str += "{" + entry.getKey().getExp() + " h:" + entry.getKey().getAssumptions()+ "} edges:" + entry.getValue() + ": \n";
        return str;
    }
}
