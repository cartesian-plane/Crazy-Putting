package org.ken22.players.pathfinding;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.players.weighting.Weighting;
import org.ken22.screens.GolfScreen;

import java.util.*;
import java.util.stream.Collectors;

public class AStar implements GridPathfinding {

    private GolfCourse course;

    private Weighting weighting;

    private double[][] terrainGrid;
    private Node[][] nodes;
    private Node finish;

    double xMin, xMax, yMin, yMax;

    public void init(Node finish, double[][] terrainGrid, GolfCourse course, Weighting weighting) {
        this.finish = finish;
        this.course = course;
        this.terrainGrid = terrainGrid;
        this.nodes = new Node[terrainGrid.length][terrainGrid[0].length];
        this.weighting = weighting;
    }

    @Override
    public double calcPathDist(double ballX, double ballY) {
        xMin = course.ballX() < course.targetXcoord() ? course.ballX() - GolfScreen.PADDING_SIZE : course.targetXcoord();
        xMax = course.ballX() > course.targetXcoord() ? course.ballX() + GolfScreen.PADDING_SIZE : course.targetXcoord();
        yMin = course.ballY() < course.targetYcoord() ? course.ballY() - GolfScreen.PADDING_SIZE : course.targetYcoord();
        yMax = course.ballY() > course.targetYcoord() ? course.ballY() + GolfScreen.PADDING_SIZE : course.targetYcoord();

        var expression = course.expression;

        expression
            .setVariable("x", ballX)
            .setVariable("y", ballY);

        double z = expression.evaluate();

        this.terrainGrid = new double[(int) ((xMax - xMin) / GridPathfinding.GRID_RESOLUTION)][(int) ((yMax - yMin) / GridPathfinding.GRID_RESOLUTION)];

        Node ballNode = project(ballX, ballY, z);

        //a star pathfinding
        Queue<Node> path = new LinkedList<>();

        return 0;
    }

    private List<Node> findPath(Node startNode, Node targetNode) {
        PriorityQueue<Node> toSearch = new PriorityQueue<>();
        toSearch.add(startNode);

        HashSet<Node> processed = new HashSet<>();

        while (!toSearch.isEmpty()) {
            var current = toSearch.poll();

            for (var node : toSearch) {
                if (node.getF() < current.getF() || node.getF() == current.getF() && node.getH() < current.getH())
                    current = node;
            }

            processed.add(current);
            toSearch.remove(current);

            if (current == targetNode) {
                return reconstructPath(startNode, targetNode);
            }

            var unprocessedNeighbours = getNeighbours(current).stream()
                .filter(neighbour -> !processed.contains(neighbour))
                .toList();

            for (Node neighbour : unprocessedNeighbours) {
                var inSearch = toSearch.contains(neighbour);

                double costToNeighbour = current.getG() + weighting.calcWeight(current, neighbour);

                if (!inSearch || costToNeighbour < neighbour.getG()) {
                    neighbour.setG(costToNeighbour);
                    neighbour.setConnection(current);

                    if (!inSearch) {
                        neighbour.setH(weighting.calcWeight(neighbour, targetNode));
                        toSearch.add(neighbour);
                    }
                }
            }

        }

        return Collections.emptyList(); // No path found
    }

    private List<Node> reconstructPath(Node startNode, Node targetNode) {
        var currentPathTile = targetNode;
        var path = new ArrayList<Node>();

        while (currentPathTile != startNode) {
            path.add(currentPathTile);
            currentPathTile = currentPathTile.getConnection();
        }

        return path;
    }

    private List<Node> getNeighbours(Node node) {
        var x = node.x;
        var y = node.y;
        ArrayList<Node> neighbours = new ArrayList<>();

        Node neighbour1, neighbour2, neighbour3, neighbour4;

        if (x + 1 < terrainGrid.length) {
            var newZ = terrainGrid[x + 1][y];
            if (nodes[x + 1][y] == null)
                nodes[x + 1][y] = new Node(x + 1, y, newZ);
            neighbour1 = nodes[x + 1][y];
            neighbours.add(neighbour1);
        }
        if (x - 1 >= 0) {
            var newZ = terrainGrid[x - 1][y];
            if (nodes[x - 1][y] == null)
                nodes[x - 1][y] = new Node(x - 1, y, newZ);
            neighbour2 = nodes[x - 1][y];
            neighbours.add(neighbour2);
        }
        if (y + 1 < terrainGrid[0].length) {
            var newZ = terrainGrid[x][y + 1];
            if (nodes[x][y + 1] == null)
                nodes[x][y + 1] = new Node(x, y + 1, newZ);
            neighbour3 = nodes[x][y + 1];
            neighbours.add(neighbour3);
        }
        if (y - 1 >= 0) {
            var newZ = terrainGrid[x][y - 1];
            if (nodes[x][y - 1] == null)
                nodes[x][y - 1] = new Node(x, y - 1, newZ);
            neighbour4 = nodes[x][y - 1];
            neighbours.add(neighbour4);
        }

        return neighbours;
    }

    private Node project(double x, double y, double z) {
        return new Node((int) ((x - xMin) / GridPathfinding.GRID_RESOLUTION),
            (int) ((y - yMin) / GridPathfinding.GRID_RESOLUTION), z);
    }

}
