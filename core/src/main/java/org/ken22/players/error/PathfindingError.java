package org.ken22.players.error;

import net.objecthunter.exp4j.Expression;
import org.ken22.input.InjectedClass;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.obstacles.Tree;
import org.ken22.obstacles.Wall;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.pathfinding.GridPathfinding;
import org.ken22.players.pathfinding.Node;
import org.ken22.players.weighting.Weighting;
import org.ken22.screens.GolfScreen;

import javax.swing.plaf.nimbus.State;

public class PathfindingError implements ErrorFunction {

    private GolfCourse course;
    private InjectedClass expr;

    private PhysicsFactory physicsEngine;

    private GridPathfinding pathfinding;
    private Weighting weighting;
    private double xMin, xMax, yMin, yMax;
    private double[][] terrainGrid;

    public PathfindingError(GridPathfinding pathfinding, Weighting weighting) {
        this.pathfinding = pathfinding;
        this.weighting = weighting;
    }

    public void init(GolfCourse course, PhysicsFactory physicsEngine) {
        this.course = course;
        this.expr = course.getInjectedExpression();
        this.physicsEngine = physicsEngine;


        // generate terrain grid // we use Double.MAX_VALUE to enconde where the ball can't go
        xMin = course.ballX() < course.targetXcoord() ? course.ballX() - GolfScreen.PADDING_SIZE : course.targetXcoord() - GolfScreen.PADDING_SIZE;
        xMax = course.ballX() > course.targetXcoord() ? course.ballX() + GolfScreen.PADDING_SIZE : course.targetXcoord() + GolfScreen.PADDING_SIZE;
        yMin = course.ballY() < course.targetYcoord() ? course.ballY() - GolfScreen.PADDING_SIZE : course.targetYcoord() - GolfScreen.PADDING_SIZE;
        yMax = course.ballY() > course.targetYcoord() ? course.ballY() + GolfScreen.PADDING_SIZE : course.targetYcoord() + GolfScreen.PADDING_SIZE;
        this.terrainGrid = new double[(int) ((xMax - xMin) / GridPathfinding.GRID_RESOLUTION)][(int) ((yMax - yMin) / GridPathfinding.GRID_RESOLUTION)];
        for (int i = 0; i < terrainGrid.length; i++) {
            for (int j = 0; j < terrainGrid[0].length; j++) {
                terrainGrid[i][j] = expr.evaluate(xMin + i * GridPathfinding.GRID_RESOLUTION, yMin + j * GridPathfinding.GRID_RESOLUTION);
                if (terrainGrid[i][j] < 0) { // if water, then set to max value
                    terrainGrid[i][j] = Double.MAX_VALUE;
                }
                for(Tree t : course.trees) { //if tree, then set to max value
                    if (Math.sqrt(Math.pow(t.coordinates()[0] - (xMin + i * GridPathfinding.GRID_RESOLUTION), 2)
                        + Math.pow(t.coordinates()[1] - (yMin + j * GridPathfinding.GRID_RESOLUTION), 2)) < t.radius()) {
                        terrainGrid[i][j] = Double.MAX_VALUE;
                    }
                }
                for(Wall w : course.walls) {
                    if (w.isPointInWall(xMin + i * GridPathfinding.GRID_RESOLUTION, yMin + j * GridPathfinding.GRID_RESOLUTION)) {
                        terrainGrid[i][j] = Double.MAX_VALUE;
                    }
                }
            }
        }

        // initialize pathfinding
        int finishX, finishY;
        finishX = (int) (1+(course.targetXcoord() - xMin) / GridPathfinding.GRID_RESOLUTION);
        finishY = (int) (1+(course.targetYcoord() - yMin) / GridPathfinding.GRID_RESOLUTION);
        Node finish = new Node(finishX, finishY, expr.evaluate(course.targetXcoord(),  course.targetYcoord()));
        this.pathfinding.init(finish, terrainGrid, course, weighting);
    }

    @Override
    public double calculateError(StateVector4 state) {
        state = physicsEngine.runSimulation(state, course);
        return pathfinding.calcPathDist(state.x(), state.y());
    }
}
