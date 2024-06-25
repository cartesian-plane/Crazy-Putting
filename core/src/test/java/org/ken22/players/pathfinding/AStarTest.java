package org.ken22.players.pathfinding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ken22.players.weighting.EquivalentWeighting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AStarTest {

    @Test
    @DisplayName("Test wall avoidance on 5x5 grid")
    public void testWallAvoidance() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        AStar aStar = new AStar();
        Field finish;
        Field terrainGrid;

        finish = AStar.class.getDeclaredField("finish");
        terrainGrid = AStar.class.getDeclaredField("terrainGrid");


        finish.setAccessible(true);
        terrainGrid.setAccessible(true);

        double[][] testGrid = {
            {2, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1},
            {1, 1, 1, 1, 1},
            {2, 1, 1, 1, 1}
        };
        terrainGrid.set(aStar, testGrid);
        var ballNode = new Node(4, 0, 2);
        var targetNode = new Node(0, 0, 1);
        Method findPath = AStar.class.getDeclaredMethod("findPath", Node.class, Node.class);
        findPath.setAccessible(true);

        aStar.init(null, testGrid, null, new EquivalentWeighting());

        var returnedPath = aStar.findPath(ballNode, targetNode);

        var node1 = new Node(4, 0 ,2);
        var node2 = new Node(3, 0, 1);
        var node3 = new Node(3, 1, 1);
        var node4 = new Node(3, 2, 1);
        var node5 = new Node(3, 3, 1);
        var node6 = new Node(3, 4, 1);
        var node7 = new Node(2, 4, 1);
        var node8 = new Node(1, 4, 1);
        var node9 = new Node(0, 4, 1);
        var node10 = new Node(0, 3, 1);
        var node11 = new Node(0, 2, 1);
        var node12 = new Node(0, 1, 1);
        var node13 = new Node(0, 0, 2);

        List<Node> expectedPath = new ArrayList<>();
        expectedPath.add(node1);
        expectedPath.add(node2);
        expectedPath.add(node3);
        expectedPath.add(node4);
        expectedPath.add(node5);
        expectedPath.add(node6);
        expectedPath.add(node7);
        expectedPath.add(node8);
        expectedPath.add(node9);
        expectedPath.add(node10);
        expectedPath.add(node11);
        expectedPath.add(node12);
        expectedPath.add(node13);
        Collections.reverse(expectedPath);

        assertEquals(expectedPath, returnedPath);

        System.out.println(returnedPath.reversed());


    }

    @Test
    @DisplayName("Test tree avoidance on 10x0 grid")
    public void testTreeAvoidance() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        AStar aStar = new AStar();
        Field finish;
        Field terrainGrid;

        finish = AStar.class.getDeclaredField("finish");
        terrainGrid = AStar.class.getDeclaredField("terrainGrid");


        finish.setAccessible(true);
        terrainGrid.setAccessible(true);

        double f = Double.POSITIVE_INFINITY;
        double b = 2;
        double t = 2;
        double[][] testGrid = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, t, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, f, 1, 1, 1, f, 1, 1},
            {1, 1, 1, f, f, f, f, f, 1, 1},
            {1, 1, 1, 1, f, f, f, 1, 1, 1},
            {1, 1, 1, 1, f, f, f, 1, 1, 1},
            {1, 1, 1, f, 1, 1, 1, f, 1, 1},
            {1, 1, 1, f, 1, 1, 1, f, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, b, 1, 1, 1, 1},
        };
        terrainGrid.set(aStar, testGrid);
        var ballNode = new Node(9, 5, 2);
        var targetNode = new Node(1, 5, 2);
        Method findPath = AStar.class.getDeclaredMethod("findPath", Node.class, Node.class);
        findPath.setAccessible(true);

        aStar.init(null, testGrid, null, new EquivalentWeighting());

        System.out.println("Avoiding tree...");
         var returnedPath = aStar.findPath(ballNode, targetNode);
        Collections.reverse(returnedPath);
        System.out.println("returnedPathReversed = " + returnedPath);
    }
}
