package org.ken22.players.pathfinding;

import org.junit.jupiter.api.Test;
import org.ken22.players.weighting.EquivalentWeighting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AStarTest {

    @Test
    public void test() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
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
            {1, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1},
            {1, 1, 1, 1, 1},
            {2, 1, 1, 1, 1}
        };
        terrainGrid.set(aStar, testGrid);
        var ballNode = new Node(4, 0, 1);
        var targetNode = new Node(0, 0, 1);
        Method findPath = AStar.class.getDeclaredMethod("findPath", Node.class, Node.class);
        findPath.setAccessible(true);

        aStar.init(null, testGrid, null, new EquivalentWeighting());

        var returnedPath = aStar.findPath(ballNode, targetNode);
        List<Node> expectedPath;

        System.out.println(returnedPath.reversed());


    }


}
