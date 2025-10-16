package com.logic.feedback.nd.algorithm.tests;

import com.logic.nd.ERule;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BinaryProofEdge {

    private final BinaryGoalObj from;
    private final BinaryTransitionEdge edge;
    private final List<BinaryGoalObj> goals;

    BinaryProofEdge(BinaryGoalObj from, BinaryTransitionEdge edge) {
        this.from = from;
        this.edge = edge;
        this.goals = new ArrayList<>(edge.getTransitions().size());
    }

    public void addGoal(BinaryGoalObj goal) {
        this.goals.add(goal);
    }

    public BinaryTransitionEdge getEdge() {
        return edge;
    }

    public List<BinaryGoalObj> getGoals() {
        return goals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryProofEdge that = (BinaryProofEdge) o;
        return Objects.equals(from, that.from) && Objects.equals(edge, that.edge) && Objects.equals(goals, that.goals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, edge, goals);
    }
}
