package com.logic.feedback.nd.algorithm.tests;

import com.logic.nd.ERule;
import com.logic.others.Utils;

import java.util.*;

public class BinaryHeightTrim {

    private final BinaryTransitionGraph transitionGraph;
    private final BinaryProofGraph proofGraph;
    private final BinaryMap map;
    private final Map<BinaryGoalObj, BinaryTransitionEdge> graph;

    public BinaryHeightTrim(BinaryTransitionGraph transitionGraph, BinaryProofGraph proofGraph, BinaryMap map) {
        this.transitionGraph = transitionGraph;
        this.proofGraph = proofGraph;
        this.map = map;

        this.graph = new HashMap<>();
    }

    public void build() {
        Queue<BinaryGoalObj> explore = proofGraph.getClosedGoals();
        for(BinaryGoalObj goal : explore) graph.put(goal, null);

        while (!explore.isEmpty()) {
            BinaryGoalObj goal = explore.poll();

            Set<BinaryTransitionEdge> inverted = transitionGraph.getInvertedEdges(goal.getExp());
            if (inverted != null) {
                for (BinaryTransitionEdge edge : inverted) {

                    boolean shouldAdd = true;
                    BinaryGoalObj prev = null;
                    for (BinaryTransitionNode transition : edge.getTransitions()) {
                        if (prev == null) {
                            prev = goal.invert(edge.getFrom(), transition.getProduces(), map);
                            if (graph.containsKey(prev) || !proofGraph.getExplored().contains(prev.getIdentifier())) {
                                shouldAdd = false;
                                break;
                            }
                        }

                        BinaryGoalObj next = prev.transit(transition.getTo(), transition.getProduces(), map);
                        if (!graph.containsKey(next)) {
                            shouldAdd = false;
                            break;
                        }
                    }

                    if (shouldAdd) {
                        graph.put(prev, edge);
                        explore.add(prev);
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
