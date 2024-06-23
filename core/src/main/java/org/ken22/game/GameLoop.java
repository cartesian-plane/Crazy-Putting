package org.ken22.game;

import com.badlogic.gdx.graphics.Camera;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.players.HumanPlayer;

public class GameLoop {
    //init
    PhysicsFactory physicsFactory;
    PhysicsEngine physicsEngine;
    private GolfCourse course;

    private int shotCount;
    private boolean ballInMotion;
    private StateVector4 lastValidState;


    public GameLoop(GolfCourse course, PhysicsFactory physicsFactory) {
        this.physicsFactory = physicsFactory;
        this.course = course;
        this.shotCount = 0;
        this.ballInMotion = false;
    }


    //find out what went wrong and revert to last position to shoot
    private void handleRestState() {
        ballInMotion = false;
        if (physicsEngine.reachedTarget()) {
            System.out.println("Ball reached the target mi lord!");
        } else if (physicsEngine.underwater()) {
            System.out.println("Ball went into the water.");
            revertToLastValidState();
        } else if (physicsEngine.outOfBounds()) {
            System.out.println("Ball went out of bounds.");
            revertToLastValidState();
        }
    }

    //shoot the ball
    public void shootBall(StateVector4 shot) {
        if (!ballInMotion) {
            lastValidState = physicsEngine.getState();
            physicsEngine.setState(shot);
            shotCount++;
            ballInMotion = true;
        }
    }



    //return to last game state if something bad
    private void revertToLastValidState() {
        physicsEngine = new PhysicsEngine(course, lastValidState);
    }
}
