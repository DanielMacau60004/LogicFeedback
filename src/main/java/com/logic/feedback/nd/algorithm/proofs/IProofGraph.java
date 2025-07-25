package com.logic.feedback.nd.algorithm.proofs;

import com.logic.api.IFormula;
import com.logic.feedback.nd.algorithm.transition.ITransitionGraph;

import java.util.Set;

public interface IProofGraph {
    GoalNode getMainGoal();

    GoalNode getTargetGoal();

    Set<IFormula> getPremises();

    ITransitionGraph getTransitionGraph();

    ProofGraphSettings getSettings();

    ProofEdge getEdge(GoalNode node);

    void build();

    boolean isSolvable();
}
