package org.ken22.players.pathfinding;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.players.weighting.Weighting;
import org.ken22.screens.GolfScreen;

import java.util.*;

@Deprecated //unimplemented
public class BFS {
    private GolfCourse course;

    private Weighting weighting;

    private double[][] terrainGrid;
    private Node start, finish;

    double xMin, xMax, yMin, yMax;

    public void init(Node finish, double[][] terrainGrid, GolfCourse course, Weighting weighting) {
        this.finish = finish;
        this.course = course;
        this.terrainGrid = terrainGrid;
        this.weighting = weighting;
    }

    public double calcPathDist(double ballX, double ballY) {
        // init bounds
        xMin = course.ballX() < course.targetXcoord() ? course.ballX() - GolfScreen.PADDING_SIZE : course.targetXcoord();
        xMax = course.ballX() > course.targetXcoord() ? course.ballX() + GolfScreen.PADDING_SIZE : course.targetXcoord();
        yMin = course.ballY() < course.targetYcoord() ? course.ballY() - GolfScreen.PADDING_SIZE : course.targetYcoord();
        yMax = course.ballY() > course.targetYcoord() ? course.ballY() + GolfScreen.PADDING_SIZE : course.targetYcoord();

        // init start
        var startCoord = project(ballX, ballY);
        start = new Node(startCoord[0], startCoord[1], terrainGrid[startCoord[0]][startCoord[1]]);

        // bfs pathfinding
        HashMap<Node, Node> parent = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(start);

//        while (!queue.isEmpty()) {
//            Node current = queue.poll();
//            if (current.equals(finish)) {
//                return
//            }
//
//            for (Node neighbor : current.neighbors) {
//                if (neighbor.g == Double.MAX_VALUE) {
//                    neighbor.g = current.g + weighting.weight(current, neighbor);
//                    queue.add(neighbor);
//                }
//            }
//        }

        return 0;
    }

    private int[] project(double x, double y) {
        return new int[] {(int) ((x - xMin) / GridPathfinding.GRID_RESOLUTION),
            (int) ((y - yMin) / GridPathfinding.GRID_RESOLUTION)};
    }
}
