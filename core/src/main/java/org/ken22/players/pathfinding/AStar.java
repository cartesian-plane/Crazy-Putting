//package org.ken22.players.pathfinding;
//
//import org.ken22.input.courseinput.GolfCourse;
//import org.ken22.players.weighting.Weighting;
//import org.ken22.screens.GolfScreen;
//
//import java.util.*;
//
//public class AStar implements GridPathfinding {
//
//    private GolfCourse course;
//
//    private Weighting weighting;
//
//    private double[][] terrainGrid;
//    private Node finish;
//
//    double xMin, xMax, yMin, yMax;
//
//    public void init(Node finish, double[][] terrainGrid, GolfCourse course, Weighting weighting) {
//        this.finish = finish;
//        this.course = course;
//        this.terrainGrid = terrainGrid;
//        this.weighting = weighting;
//    }
//
//    @Override
//    public double calcPathDist(double ballX, double ballY) {
//        xMin = course.ballX() < course.targetXcoord() ? course.ballX() - GolfScreen.PADDING_SIZE : course.targetXcoord();
//        xMax = course.ballX() > course.targetXcoord() ? course.ballX() + GolfScreen.PADDING_SIZE : course.targetXcoord();
//        yMin = course.ballY() < course.targetYcoord() ? course.ballY() - GolfScreen.PADDING_SIZE : course.targetYcoord();
//        yMax = course.ballY() > course.targetYcoord() ? course.ballY() + GolfScreen.PADDING_SIZE : course.targetYcoord();
//
//        var expression = course.expression;
//
//        expression
//            .setVariable("x", ballX)
//            .setVariable("y", ballY);
//
//        double z =  expression.evaluate();
//
//        this.terrainGrid = new double[(int) ((xMax - xMin) / GridPathfinding.GRID_RESOLUTION)][(int) ((yMax - yMin) / GridPathfinding.GRID_RESOLUTION)];
//
//        Node ballNode = project(ballX, ballY, z);
//
//        //a star pathfinding
//        Queue<Node> path = new Queue<Node>();
//
//        return 0;
//    }
//
//    private List<Node> findPath(Node start, Node targetNode) {
//        PriorityQueue<Node> toSearch = new PriorityQueue<>();
//        toSearch.add(start);
//
//        HashSet<Node> processed = new HashSet<>();
//
//        start.h = heuristic(start, targetNode);
//        start.f = start.h;
//
//
//        while (!toSearch.isEmpty()) {
//            var current = toSearch.poll();
//
//            for (var node : toSearch) {
//                if (node.getF() < current.getF() || node.getF() == current.getF() && node.getH() < current.getH())
//                    current = node;
//            }
//
//            processed.add(current);
//            toSearch.remove(current);
//
//            if (current == targetNode) {
//                // implement this later
//            }
//
//
//            processed.add(current);
//
//            for (Node neighbor : getNeighbors(current)) {
//                if (processed.contains(neighbor)) continue;
//
//                double tentativeG = current.g + 1; // Assuming uniform cost for moving to a neighbor
//
//                if (!toSearch.contains(neighbor) || tentativeG < neighbor.g) {
//                    neighbor.parent = current;
//                    neighbor.g = tentativeG;
//                    neighbor.h = heuristic(neighbor, targetNode);
//                    neighbor.f = neighbor.g + neighbor.h;
//
//                    if (!toSearch.contains(neighbor)) {
//                        toSearch.add(neighbor);
//                    }
//                }
//            }
//        }
//
//        return Collections.emptyList(); // No path found
//    }
//
//    private List<Node> reconstructPath(Node current) {
//        List<Node> path = new ArrayList<>();
//        while (current != null) {
//            path.add(current);
//            current = current.parent;
//        }
//        Collections.reverse(path);
//        return path;
//    }
//
//    private List<Node> getNeighbors(Node node) {
//
//    }
//
//    private Node project(double x, double y, double z) {
//        return new Node((int) ((x - xMin) / GridPathfinding.GRID_RESOLUTION),
//            (int) ((y - yMin) / GridPathfinding.GRID_RESOLUTION), z);
//    }
//
//}
