package com.logic.feedback.nd.algorithm.tests;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.*;

public class BinaryProofGraphv3 {

    private static final int TOTAL_CLOSED = 10_000;
    public static final int BIG_INT = 10000;

    private final BinaryMap map;
    private final BinaryTransitionGraph transitionGraph;
    private final Map<BinaryGoalObj, BinaryTransitionEdge> graph;

    public BinaryProofGraphv3(BinaryMap map, BinaryTransitionGraph transitionGraph) {
        this.map = map;
        this.transitionGraph = transitionGraph;
        this.graph = new HashMap<>();
    }

    public void build(BinaryGoalObj initialGoal) {
        IntSet explored = new IntOpenHashSet();
        Int2ObjectMap<Set<BinaryGoalObj>> goals = new Int2ObjectOpenHashMap<>();
        Object2IntOpenHashMap<BinaryGoalObj> sizes = new Object2IntOpenHashMap<>();

        PriorityQueue<BinaryGoalObj> queue = new PriorityQueue<>(
                Comparator.comparingInt(BinaryGoalObj::getHeight).thenComparingInt(BinaryGoalObj::getIdentifier));


        Queue<BinaryGoalObj> explore = new LinkedList<>();
        goals.put(initialGoal.getIdentifier(), new HashSet<>());
        explore.add(initialGoal);

        int closedGoal = 0;

        while (!explore.isEmpty()) {
            BinaryGoalObj goal = explore.poll();

            if (goal.isClosed()) {
                if (!sizes.containsKey(goal)) //Count only different goals
                    closedGoal++;
                trimGoal(queue, initialGoal, goal, sizes, goals);
                if (closedGoal == TOTAL_CLOSED)
                    break;
                continue;
            }

            for (BinaryTransitionEdge edge : transitionGraph.getEdges(goal.getExp())) {
                for (BinaryTransitionNode transition : edge.getTransitions()) {
                    BinaryGoalObj newGoal = goal.transit(transition.getTo(), transition.getProduces(), map);

                    int identifier = newGoal.getIdentifier();
                    //if (newGoal.numberOfAssumptions(map) > 2) break;
                    Set<BinaryGoalObj> edges = goals.get(identifier);
                    if (edges == null) {
                        edges = new HashSet<>();
                        goals.put(identifier, edges);
                    }
                    edges.add(goal);

                    if (newGoal.isClosed() || explored.add(identifier))
                        explore.add(newGoal);
                }
            }
        }

        /*System.out.println("Size: " + explored.size());
        System.out.println("Formulas: " + map.formulas.size());
        System.out.println("BitArray: " + map.assumptions.size());
        System.out.println("Closed: " + closedGoal);*/

        //System.out.println(Utils.getToken(map.formulas.toString()));
    }

    private void trimGoal(PriorityQueue<BinaryGoalObj> queue, BinaryGoalObj initialGoal, BinaryGoalObj current, Object2IntOpenHashMap<BinaryGoalObj> sizes,
                          Int2ObjectMap<Set<BinaryGoalObj>> goals) {

        current.setHeight(1);
        sizes.put(current, 1);
        queue.add(current);
        graph.put(current, null);

        while (!queue.isEmpty()) {
            BinaryGoalObj goal = queue.poll();

            Set<BinaryGoalObj> subGoals = goals.get(goal.getIdentifier());
            if (subGoals == null) continue;

            for (BinaryGoalObj prev : subGoals) {
                for (BinaryTransitionEdge edge : transitionGraph.getEdges(prev.getExp())) {
                    if(!edge.containsFormula(goal.getExp())) continue;

                    boolean isClosed = true;
                    int size = 1;
                    for (BinaryTransitionNode transition : edge.getTransitions()) {
                        BinaryGoalObj next = prev.transit(transition.getTo(), transition.getProduces(), map);

                        if (!sizes.containsKey(next)) {
                            isClosed = false;
                            break;
                        }

                        size += sizes.getOrDefault(next, BIG_INT);
                    }

                    if (isClosed && (sizes.getOrDefault(prev, BIG_INT) > size)) {
                        if (prev.getIdentifier() == initialGoal.getIdentifier())
                            System.out.println(size);

                        graph.put(prev, edge);
                        prev.setHeight(size);
                        sizes.put(prev, size);
                        queue.remove(prev);
                        queue.add(prev);
                    }
                }
            }

        }
    }

    public Map<BinaryGoalObj, BinaryTransitionEdge> getGraph() {
        return graph;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Total nodes: ").append(graph.size()).append("\n");
        str.append("Total edges: ").append(graph.values().stream().filter(Objects::nonNull).count()).append("\n");

        for (Map.Entry<BinaryGoalObj, BinaryTransitionEdge> entry : graph.entrySet()) {
            str.append(entry.getKey().toString(map)).append("\n");

            if (entry.getValue() != null) {
                BinaryTransitionEdge edge = entry.getValue();
                str.append("\t").append(edge.getRule());
                for (BinaryTransitionNode transition : edge.getTransitions()) {
                    str.append("[to:").append(map.getFormulas(transition.getTo()));
                    if (transition.getProduces() != null) {
                        str.append(", produces:").append(map.getFormulas(transition.getProduces()));
                    }
                    str.append("]");
                }
                str.append("]\n");
            }

        }

        return str.toString();
    }
}
