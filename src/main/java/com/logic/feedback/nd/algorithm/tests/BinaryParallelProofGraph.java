package com.logic.feedback.nd.algorithm.tests;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class BinaryParallelProofGraph {

    private static final int TOTAL_CLOSED = 50_000;

    private final BinaryMap map;
    private final BinaryTransitionGraph transitionGraph;

    private final IntSet explored;
    private final Queue<BinaryGoalObj> closedGoals;

    public BinaryParallelProofGraph(BinaryMap map, BinaryTransitionGraph transitionGraph) {
        this.map = map;
        this.transitionGraph = transitionGraph;
        this.explored = new IntOpenHashSet();
        this.closedGoals = new LinkedList<>();
    }

    public void build(BinaryGoalObj initialGoal) {
        Queue<BinaryGoalObj> explore = new LinkedList<>();
        explored.add((int)initialGoal.getIdentifier());
        explore.add(initialGoal);
        explore.add(null);

        int maxThreads = Runtime.getRuntime().availableProcessors();
        int frontierSize = explored.size();
        int numThreads = Math.min(maxThreads, Math.max(1, frontierSize / 10_000));
        ForkJoinPool levelPool = new ForkJoinPool(numThreads);

        //explore(explore, level);

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

    private void explore(Queue<BinaryGoalObj> explore, int level) {

        while (!explore.isEmpty()) {
            BinaryGoalObj goal = explore.poll();
            goal.setHeight(level);

            if (closedGoals.size() == TOTAL_CLOSED) break; //TODO tem de ser feito antes de enviar para o thread

            if (goal.isClosed()) {
                closedGoals.add(goal);
                continue;
            }

            for (BinaryTransitionEdge edge : transitionGraph.getEdges(goal.getExp())) {
                for (BinaryTransitionNode transition : edge.getTransitions()) {
                    BinaryGoalObj newGoal = goal.transit(transition.getTo(), transition.getProduces(), map);

                    int identifier = (int)newGoal.getIdentifier();
                    if (newGoal.numberOfAssumptions(map) > 4) break;
                    if (explored.add(identifier))
                        explore.add(newGoal);
                }
            }
        }
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
