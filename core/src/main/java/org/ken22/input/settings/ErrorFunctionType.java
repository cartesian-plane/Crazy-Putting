package org.ken22.input.settings;

import org.ken22.players.error.ErrorFunction;
import org.ken22.players.error.EuclAndVelError;
import org.ken22.players.error.EuclideanError;
import org.ken22.players.error.PathfindingError;
import org.ken22.players.pathfinding.GridPathfinding;
import org.ken22.players.weighting.Weighting;

public enum ErrorFunctionType {
    EUCLIDEAN("Euclidean") {
        @Override
        public String toString() {
            return "Euclidean";
        }
        public ErrorFunction getErrorFunction() {
            return new EuclideanError();
        }
    },

    EUCLIDEAN_AND_VELOCITY("Euclidean and velocity") {
        @Override
        public String toString() {
            return "Euclidean and velocity";
        }
        public ErrorFunction getErrorFunction() {
            return new EuclAndVelError();
        }
    },

    PATHFINDING("Pathfinding") {
        @Override
        public String toString() {
            return "Pathfinding";
        }
        public ErrorFunction getErrorFunction(GridPathfinding pathfinding, Weighting weighting) {
            return new PathfindingError(pathfinding, weighting);
        }
    };

    private final String name;

    ErrorFunctionType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
