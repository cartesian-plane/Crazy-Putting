package org.ken22.physicsx.differentiation;

public enum Lambdas {
    X(0), Y(1), VX(2), VY(3);

    private final int index;

    Lambdas(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
