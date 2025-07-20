package com.logic.feedback.nd.algorithm.proofs;

import com.logic.feedback.nd.algorithm.proofs.strategies.IBuildStrategy;
import com.logic.feedback.nd.algorithm.proofs.strategies.ITrimStrategy;


public class ProofGraphSettings {

    private int heightLimit;
    private int totalClosedNodesLimit;
    private int totalNodesLimit;
    private int hypothesesPerGoalLimit;

    private long timeout;

    private final IBuildStrategy buildStrategy;
    private final ITrimStrategy trimStrategy;

    public ProofGraphSettings(int heightLimit, int totalClosedNodesLimit, int totalNodesLimit,
                              int hypothesesPerGoalLimit, long timeout, IBuildStrategy buildStrategy,
                              ITrimStrategy trimStrategy) {

        this.heightLimit = heightLimit;
        this.totalNodesLimit = totalNodesLimit;
        this.totalClosedNodesLimit = totalClosedNodesLimit;
        this.hypothesesPerGoalLimit = hypothesesPerGoalLimit;
        this.timeout = timeout;
        this.buildStrategy = buildStrategy;
        this.trimStrategy = trimStrategy;
    }

    public int getHeightLimit() {
        return heightLimit;
    }

    public void setHeightLimit(int heightLimit) {
        this.heightLimit = heightLimit;
    }

    public int getTotalNodesLimit() {
        return totalNodesLimit;
    }

    public void setTotalClosedNodesLimit(int totalClosedNodesLimit) {
        this.totalClosedNodesLimit = totalClosedNodesLimit;
    }

    public int getTotalClosedNodesLimit() {
        return totalClosedNodesLimit;
    }

    public void setTotalNodesLimit(int totalNodesLimit) {
        this.totalNodesLimit = totalNodesLimit;
    }

    public int getHypothesesPerGoalLimit() {
        return hypothesesPerGoalLimit;
    }

    public void setHypothesesPerGoalLimit(int hypothesesPerGoalLimit) {
        this.hypothesesPerGoalLimit = hypothesesPerGoalLimit;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public IBuildStrategy getBuildStrategy() {
        return buildStrategy;
    }

    public ITrimStrategy getTrimStrategy() {
        return trimStrategy;
    }
}


