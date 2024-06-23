package org.ken22.input.settings;

import org.ken22.input.courseinput.GolfCourse;
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
        public ErrorFunction getErrorFunction(GolfCourse course, GridPathfinding pathfinding, Weighting weighting) {
            var euclideanError = new EuclideanError();
            euclideanError.init(course);
            return euclideanError;
        }
    },

    EUCLIDEAN_AND_VELOCITY("Euclidean and velocity") {
        @Override
        public String toString() {
            return "Euclidean and velocity";
        }
        public ErrorFunction getErrorFunction(GolfCourse course, GridPathfinding pathfinding, Weighting weighting) {
            var euclAndVelError = new EuclAndVelError();
            euclAndVelError.init(course);
            return euclAndVelError;
        }
    },

    PATHFINDING("Pathfinding") {
        @Override
        public String toString() {
            return "Pathfinding";
        }
        public ErrorFunction getErrorFunction(GolfCourse course, GridPathfinding pathfinding, Weighting weighting) {
            var pathfindingError = new PathfindingError(pathfinding, weighting);
            pathfindingError.init(course);
            return pathfindingError;
        }
    };

    private final String name;

    ErrorFunctionType(String name) {
        this.name = name;
    }

    public abstract ErrorFunction getErrorFunction(GolfCourse course, GridPathfinding pathfinding, Weighting weighting);
    @Override
    public String toString() {
        return name;
    }
}
