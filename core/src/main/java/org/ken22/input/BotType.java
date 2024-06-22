package org.ken22.input;

public enum BotType {
    PLANAR("Planar"),
    HOLE_IN_ONE("Hole-in-one"),
    MAZE_EXPLORER("Maze explorer");


    private String name;

    private BotType(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
