package com.logic.feedback.nd.algorithm;

import com.logic.feedback.nd.algorithm.proofs.GoalNode;
import com.logic.feedback.nd.algorithm.proofs.IProofGraph;

public interface IGoalBuilder {

    GoalNode build(IProofGraph proofGraph);
}
