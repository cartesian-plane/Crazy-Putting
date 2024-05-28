package org.ken22.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import org.ken22.physicsx.engine.PhysicsEngine;
import org.ken22.physicsx.vectors.StateVector4;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.players.HumanPlayer;

import java.util.ArrayList;

public class GameLoop {
    //init
    private PhysicsEngine physicsEngine;
    private HumanPlayer humanPlayer;
    private GolfCourse course;
    private Camera camera;
    private int shotCount;
    private boolean ballInMotion;
    private StateVector4 lastValidState;



    //recieve stuff
    public GameLoop(PhysicsEngine physicsEngine, GolfCourse course, Camera camera) {
        this.physicsEngine = physicsEngine;
        this.course = course;
        this.camera = camera;
        this.humanPlayer = new HumanPlayer();
        this.shotCount = 0;
        this.ballInMotion = false;
        this.lastValidState = physicsEngine.getTrajectory().get(0); // initial state
    }


    //update and check for flags in new step
    public void update(float deltaTime) {
        if (ballInMotion) {
            StateVector4 newState = physicsEngine.nextStep();
            if (physicsEngine.isAtRest()) {
                handleRestState();
            } else {
                lastValidState = newState;
            }
        }
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
        promptHumanPlayer();
    }





    //get the player input for a shot with Y and X velocities
    private void promptHumanPlayer() {
        StateVector4 currentState = physicsEngine.getTrajectory().get(physicsEngine.getTrajectory().size() - 1);
        StateVector4 velocities = humanPlayer.play(currentState, course);

        // setting the new velocities
        StateVector4 newState = new StateVector4(
            currentState.x(),
            currentState.y(),
            velocities.vx(),
            velocities.vy()
        );
        physicsEngine = new PhysicsEngine(course, newState);
        //one more shot
        ballInMotion = true;
        shotCount++;
    }



    //return to last game state if something bad
    private void revertToLastValidState() {
        physicsEngine = new PhysicsEngine(course, lastValidState);
    }
}
