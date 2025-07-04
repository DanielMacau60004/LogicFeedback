package com.logic.feedback.nd.algorithm.proofs.strategies;

import com.logic.feedback.nd.algorithm.proofs.ProofEdge;
import com.logic.feedback.nd.algorithm.proofs.GoalNode;

import java.util.Map;

public interface ITrimStrategy {

    Map<GoalNode, ProofEdge> trim(IBuildStrategy buildStrategy);
}
