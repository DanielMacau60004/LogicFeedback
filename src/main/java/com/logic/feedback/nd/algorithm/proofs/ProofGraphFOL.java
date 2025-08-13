package com.logic.feedback.nd.algorithm.proofs;

import com.logic.feedback.nd.algorithm.AlgoProofFOLMainGoalBuilder;
import com.logic.feedback.nd.algorithm.AlgoProofFOLGoalBuilder;
import com.logic.feedback.nd.algorithm.transition.ITransitionGraph;

import java.util.*;

public class ProofGraphFOL extends ProofGraph {

    public ProofGraphFOL(AlgoProofFOLMainGoalBuilder problem, AlgoProofFOLGoalBuilder state,
                         ITransitionGraph transitionGraph, ProofGraphSettings settings) {
        super(new HashSet<>(problem.premises), transitionGraph, settings);

        this.mainGoal = problem.build(problem.premises, handler);
        targetGoal = state.build(problem.premises, handler);
    }

    @Override
    public String toString() {
        String str = "";
        str += "Total nodes: " + tree.size() + "\n";
        str += "Total edges: " + tree.values().stream().filter(Objects::nonNull).mapToLong(t -> t.getTransitions().size()).sum() + "\n";
        for (Map.Entry<GoalNode, ProofEdge> entry : tree.entrySet())
            str += "{" + entry.getKey().getExp() + " h:" + entry.getKey().getAssumptions() + " " +
                    entry.getKey().getNotFree() + "} edges:" + entry.getValue() + ": \n";
        return str;
    }

}
