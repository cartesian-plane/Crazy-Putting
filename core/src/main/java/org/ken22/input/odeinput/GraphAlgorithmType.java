package org.ken22.input.odeinput;

public enum GraphAlgorithmType {
    NONE("None"),
    A_STAR("A*"),
    BFS("BFS");

    private String name;

    private GraphAlgorithmType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
