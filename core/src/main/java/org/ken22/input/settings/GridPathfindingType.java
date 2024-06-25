package org.ken22.input.settings;

import org.ken22.players.pathfinding.AStar;
import org.ken22.players.pathfinding.GridPathfinding;

public enum GridPathfindingType {
    NONE("None"),
    A_STAR("A*"),
    BFS("BFS");

    private String name;

    private GridPathfindingType(String name) {
        this.name = name;
    }

    public GridPathfinding getPathfinding() {
        return new AStar();
    }


    @Override
    public String toString() {
        return name;
    }
}
