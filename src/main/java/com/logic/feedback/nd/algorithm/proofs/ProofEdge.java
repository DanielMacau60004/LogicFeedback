package com.logic.feedback.nd.algorithm.proofs;

import com.logic.api.IFormula;
import com.logic.nd.ERule;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ProofEdge {

    private final ERule rule;
    private final List<GoalNode> transitions;

    public ProofEdge(ERule rule, GoalNode transition) {
        this.rule = rule;
        this.transitions = new LinkedList<>();

        transitions.add(transition);
    }

    public ProofEdge(ERule rule) {
        this.rule = rule;
        this.transitions = new LinkedList<>();
    }

    public void addTransition(GoalNode to) {
        transitions.add(to);
    }

    public int height() {
        int height = 0;
        for(GoalNode g : transitions)
            height += g.getHeight();
        return height;
    }

    public List<GoalNode> getTransitions() {
        return transitions;
    }

    public boolean isClosed() {
        for(GoalNode g : transitions)
            if(!g.isClosed()) return  false;
        return true;
    }

    public ERule getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return transitions.toString() + " closed: " + isClosed() + " rule: " + rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProofEdge edge = (ProofEdge) o;
        return rule == edge.rule && Objects.equals(transitions, edge.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, transitions);
    }


}
