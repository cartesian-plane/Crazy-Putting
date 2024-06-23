package org.ken22.players.pathfinding;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.players.weighting.Weighting;

public interface GridPathfinding extends Pathfinding {
    public static final double GRID_RESOLUTION = 0.1;
    public void init(Node finish, double[][] terrainGrid, GolfCourse course, Weighting weighting);
}
