package com.logic.feedback.nd.algorithm.tests;

import java.util.*;

public class BinarySizeTrim {

    private final BinaryTransitionGraph transitionGraph;
    private final BinaryProofGraph proofGraph;
    private final BinaryMap map;
    private final Map<BinaryGoalObj, BinaryTransitionEdge> graph;

    public BinarySizeTrim(BinaryTransitionGraph transitionGraph, BinaryProofGraph proofGraph, BinaryMap map) {
        this.transitionGraph = transitionGraph;
        this.proofGraph = proofGraph;
        this.map = map;

        this.graph = new HashMap<>();
    }

    public void build() {
        Map<BinaryGoalObj, Integer> sizes = new HashMap<>();
        PriorityQueue<BinaryGoalObj> queue = new PriorityQueue<>(
                (a, b) -> {
                    int result = Integer.compare(a.getHeight(), b.getHeight());
                    if (result != 0) return result;

                    return Integer.compare(a.getIdentifier(), b.getIdentifier());
                });

        for (BinaryGoalObj goal : proofGraph.getClosedGoals()) {
            goal.setHeight(1);
            sizes.put(goal, 1);
            queue.add(goal);
            graph.put(goal, null);
        }

        while (!queue.isEmpty()) {
            BinaryGoalObj goal = queue.poll();

            //if (sizes.get(goal) != null && sizes.get(goal) >= goal.getHeight()) continue;
            //sizes.put(goal, goal.getHeight());

            Set<BinaryTransitionEdge> adjacent = transitionGraph.getInvertedEdges(goal.getExp());
            if (adjacent == null) continue;

            for (BinaryTransitionEdge edge : adjacent) {
                List<BinaryTransitionNode> transitions = edge.getTransitions();
                BinaryGoalObj prev = goal.invert(edge.getFrom(), transitions.get(0).getProduces(), map);
                if (!proofGraph.getExplored().contains(prev.getIdentifier()))
                    continue;

                int size = 1;
                boolean isClosed = true;

                for (BinaryTransitionNode transition : edge.getTransitions()) {
                    BinaryGoalObj next = prev.transit(transition.getTo(), transition.getProduces(), map);
                    if (!sizes.containsKey(next)) {
                        isClosed = false;
                        break;
                    }

                    size += sizes.get(next);
                }

                if (isClosed && (sizes.get(prev) == null || sizes.get(prev) > size)) {
                    graph.put(prev, edge);
                    prev.setHeight(size);
                    sizes.put(prev, size);
                    queue.remove(prev);
                    queue.add(prev);
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
