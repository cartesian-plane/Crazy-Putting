package org.ken22.input.odeinput;

public enum GridPathfindingType {
    NONE("None"),
    A_STAR("A*"),
    BFS("BFS");

    private String name;

    private GridPathfindingType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
