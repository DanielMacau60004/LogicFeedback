package com.logic.feedback.nd.algorithm.tests;

import com.logic.api.IFormula;
import com.logic.others.Utils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.*;

public class BinaryProofGraph {

    private static final int TOTAL_CLOSED = 20_000;

    private final BinaryMap map;
    private final BinaryTransitionGraph transitionGraph;

    private final IntSet explored;
    private final Queue<BinaryGoalObj> closedGoals;

    public BinaryProofGraph(BinaryMap map, BinaryTransitionGraph transitionGraph) {
        this.map = map;
        this.transitionGraph = transitionGraph;
        this.explored = new IntOpenHashSet();
        this.closedGoals = new LinkedList<>();
    }

    public void build(BinaryGoalObj initialGoal) {
        Queue<BinaryGoalObj> explore = new LinkedList<>();
        explored.add(initialGoal.getIdentifier());
        explore.add(initialGoal);
        explore.add(null);

        int level = 0;
        int min = Integer.MAX_VALUE;
        int totalExplored = 0;

        while (!explore.isEmpty()) {
            BinaryGoalObj goal = explore.poll();

            //Track the current level
            if (goal == null) {
                level++;
                if (!explore.isEmpty()) explore.add(null);
                System.out.println("Level: " + level+", min: " + min+", totalExplored: " + totalExplored);
                min = Integer.MAX_VALUE;
                totalExplored = 0;
                continue;
            }

            goal.setHeight(level);

            if (closedGoals.size() == TOTAL_CLOSED) break;

            if (goal.isClosed()) {
                closedGoals.add(goal);
                continue;
            }

            for (BinaryTransitionEdge edge : transitionGraph.getEdges(goal.getExp())) {
                for (BinaryTransitionNode transition : edge.getTransitions()) {
                    BinaryGoalObj newGoal = goal.transit(transition.getTo(), transition.getProduces(), map);

                    int identifier = newGoal.getIdentifier();
                    //if (newGoal.numberOfAssumptions(map) > 4) break;
                    if (explored.add(identifier)) {
                        totalExplored++;
                        min = Math.min(min, newGoal.numberOfAssumptions(map));
                        explore.add(newGoal);
                    }
                }
            }
        }

        System.out.println("Size: " + explored.size());
        System.out.println("Formulas: " + map.formulas.size());
        System.out.println("BitArray: " + map.assumptions.size());
        System.out.println("Closed: " + closedGoals.size());
/*
        map.assumptions.forEach((i,it)->{
            boolean first = true;
            StringBuilder str = new StringBuilder();
            for (short assumption : it.getData()) {
                if (!first) str.append(", ");
                str.append(map.getFormulas(assumption));
                first = false;
            }
            System.out.println(str);
        });*/
    }

    public Queue<BinaryGoalObj> getClosedGoals() {
        return closedGoals;
    }

    public Set<Integer> getExplored() {
        return explored;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Closed Nodes: ").append(closedGoals.size()).append("\n");
        for (BinaryGoalObj closed : closedGoals) {
            str.append(map.getFormulas(closed.getExp())).append(" [");
            BinarySet assumptions = closed.getAssumptions(map);
            boolean first = true;
            for (short assumption : assumptions.getData()) {
                if (!first) str.append(", ");
                str.append(map.getFormulas(assumption));
                first = false;
            }

            str.append("]\n");
        }

        return str.toString();
    }

}
