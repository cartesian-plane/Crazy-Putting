package org.ken22.input.settings;

import org.ken22.players.pathfinding.AStar;
import org.ken22.players.pathfinding.GridPathfinding;

public enum GridPathfindingType {
    A_STAR("A*") {
        @Override
        public String toString() {
            return "A*";
        }
        public GridPathfinding getPathfinding() {
            return new AStar();
        }
    };

    private final String name;

    GridPathfindingType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
    public abstract GridPathfinding getPathfinding();
}
