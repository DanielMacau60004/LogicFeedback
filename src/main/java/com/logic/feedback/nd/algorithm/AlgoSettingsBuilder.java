package com.logic.feedback.nd.algorithm;

import com.logic.feedback.nd.algorithm.proofs.ProofGraphSettings;
import com.logic.feedback.nd.algorithm.proofs.strategies.IBuildStrategy;
import com.logic.feedback.nd.algorithm.proofs.strategies.ITrimStrategy;
import com.logic.feedback.nd.algorithm.proofs.strategies.LinearBuildStrategy;
import com.logic.feedback.nd.algorithm.proofs.strategies.HeightTrimStrategy;

public class AlgoSettingsBuilder {

    private int heightLimit = 20;
    private int totalClosedNodes = 2000;
    private int hypothesesPerGoal = 5;
    private int totalNodes = Integer.MAX_VALUE;

    private long timeout = 5000; //In milliseconds

    private IBuildStrategy buildStrategy = new LinearBuildStrategy();
    private ITrimStrategy trimStrategy = new HeightTrimStrategy();

    public int getHeightLimit() {
        return heightLimit;
    }

    public AlgoSettingsBuilder setHeightLimit(int heightLimit) {
        this.heightLimit = heightLimit;
        return this;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public AlgoSettingsBuilder setTotalClosedNodes(int totalClosedNodes) {
        this.totalClosedNodes = totalClosedNodes;
        return this;
    }

    public int getHypothesesPerGoal() {
        return hypothesesPerGoal;
    }

    public AlgoSettingsBuilder setHypothesesPerGoal(int hypothesesPerGoal) {
        this.hypothesesPerGoal = hypothesesPerGoal;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public AlgoSettingsBuilder setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getTotalClosedNodes() {
        return totalClosedNodes;
    }

    public AlgoSettingsBuilder setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
        return this;
    }

    public IBuildStrategy getBuildStrategy() {
        return buildStrategy;
    }

    public AlgoSettingsBuilder setBuildStrategy(IBuildStrategy buildStrategy) {
        this.buildStrategy = buildStrategy;
        return this;
    }

    public ITrimStrategy getTrimStrategy() {
        return trimStrategy;
    }

    public AlgoSettingsBuilder setTrimStrategy(ITrimStrategy trimStrategy) {
        this.trimStrategy = trimStrategy;
        return this;
    }

    public ProofGraphSettings build() {
        return new ProofGraphSettings(heightLimit, totalClosedNodes, totalNodes,
                hypothesesPerGoal, timeout, buildStrategy, trimStrategy);
    }

}
