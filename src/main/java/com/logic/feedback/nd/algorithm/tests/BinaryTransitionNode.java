package com.logic.feedback.nd.algorithm.tests;

import java.util.Objects;

public class BinaryTransitionNode {

    private final Short to;
    private final Short produces;

    BinaryTransitionNode(Short to, Short produces) {
        this.to = to;
        this.produces = produces;
    }

    public short getTo() {
        return to;
    }

    public Short getProduces() {
        return produces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryTransitionNode that = (BinaryTransitionNode) o;
        return Objects.equals(to, that.to) && Objects.equals(produces, that.produces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, produces);
    }

}
