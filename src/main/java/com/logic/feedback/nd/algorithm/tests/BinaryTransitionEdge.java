package com.logic.feedback.nd.algorithm.tests;

import com.logic.nd.ERule;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BinaryTransitionEdge implements Comparable<BinaryTransitionEdge> {

    private final ERule rule;
    private final short from;
    private final List<BinaryTransitionNode> transitions;
    private final ShortSet formulas = new ShortOpenHashSet();

    BinaryTransitionEdge(ERule rule, Short from, Short to, Short produces) {
        this(rule, from);
        addTransition(to, produces);
    }

    BinaryTransitionEdge(ERule rule, Short from, Short to) {
        this(rule, from);
        addTransition(to, null);
    }

    BinaryTransitionEdge(ERule rule, Short from) {
        this.rule = rule;
        this.from = from;
        this.transitions = new LinkedList<>();
    }

    public BinaryTransitionEdge addTransition(Short to, Short produces) {
        transitions.add(new BinaryTransitionNode(to, produces));
        formulas.add((short)to);
        return this;
    }

    public BinaryTransitionEdge addTransition(Short to) {
        transitions.add(new BinaryTransitionNode(to, null));
        formulas.add((short)to);
        return this;
    }

    public boolean containsFormula(short formula) {
        return formulas.contains(formula);
    }

    public List<BinaryTransitionNode> getTransitions() {
        return transitions;
    }

    public short getFrom() {
        return from;
    }

    public ERule getRule() {
        return rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryTransitionEdge that = (BinaryTransitionEdge) o;
        return rule == that.rule && Objects.equals(transitions, that.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, transitions);
    }

    @Override
    public int compareTo(BinaryTransitionEdge other) {
        if (this.equals(other)) return 0;

        //int cmp = Integer.compare(this.transitions.size(), other.transitions.size());
        //if (cmp != 0) return cmp;
        int cmp;
        long countThis = this.transitions.stream().filter(t -> t.getProduces() != null).count();
        long countOther = other.transitions.stream().filter(t -> t.getProduces() != null).count();
        cmp = Long.compare(countThis, countOther);
        if (cmp != 0) return cmp;

        return -1;
    }

    public String toString(BinaryMap map) {
        StringBuilder str = new StringBuilder("[");
        str.append("\tfrom: ").append(map.getFormulas(this.getFrom()))
                .append(" [").append(this.getRule()).append(": ");
        for (BinaryTransitionNode transition : this.getTransitions()) {
            str.append("[to:").append(map.getFormulas(transition.getTo()))
                    .append(" ").append(transition.getTo());
            if (transition.getProduces() != null) {
                str.append(", produces:").append(map.getFormulas(transition.getProduces()));
            }
            str.append("]");
        }
        return str.toString();
    }

}
