//package org.ken22.game;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.Camera;
//import com.badlogic.gdx.math.Vector3;
//
//import java.util.ArrayList;
//
//
//
//
//
//public class GameLoop {
//    private PhysicsEngine physicsEngine;
//    private Vector3 ballPosition;
//    private double ballVelocityX;
//    private double ballVelocityY;
//    private Vector3 targetPosition;
//    private double targetRadius;
//    private int shotCount;
//    private int maxStrokes;
//    private boolean ballInMotion;
//    private boolean outOfBounds;
//    private Camera camera;
//    private float boundaryMinX, boundaryMinY, boundaryMaxX, boundaryMaxY;
//
//
//
//    public GameLoop(PhysicsEngine physicsEngine, Vector3 initialBallPosition, Vector3 targetPosition,
//                    double targetRadius, Camera camera, float boundaryMinX, float boundaryMinY, float boundaryMaxX, float boundaryMaxY, int maxStrokes) {
//        this.physicsEngine = physicsEngine;
//        this.ballPosition = initialBallPosition;
//        this.targetPosition = targetPosition;
//        this.targetRadius = targetRadius;
//        this.camera = camera;
//        this.boundaryMinX = boundaryMinX;
//        this.boundaryMinY = boundaryMinY;
//        this.boundaryMaxX = boundaryMaxX;
//        this.boundaryMaxY = boundaryMaxY;
//        this.shotCount = 0;
//        this.maxStrokes = maxStrokes;
//        this.ballInMotion = false;
//        this.outOfBounds = false;
//    }
//
//
//
//
//
//
//
//
//
//
//
//
////This updates each frame and checks flags
//    public void update(float deltaTime) {
//        if (ballInMotion) {
//            physicsEngine.solve();
//            updateBallPosition();
//            checkBallInTarget();
//            checkBallStopped();
//            checkOutOfBounds();
//        } else {
//            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && shotCount < maxStrokes) {
//                shootBall();
//            }
//        }
//    }
//
//
//
////New ball position
//    private void updateBallPosition() {
//        ArrayList<Double> lastState = physicsEngine.getStateVectors().get(physicsEngine.getStateVectors().size() - 1);
//        ballPosition.set(lastState.get(1).floatValue(), lastState.get(2).floatValue(), ballPosition.z);
//        ballVelocityX = lastState.get(3);
//        ballVelocityY = lastState.get(4);
//    }
//
//
//
//
////ADAPT ALL TO FIT
//
//
//
//
////is it in the target
//    private void checkBallInTarget() {
//        float distance = ballPosition.dst(targetPosition);
//        if (distance <= targetRadius) {
//            System.out.println("Ball in target my lord");
//            ballInMotion = false;
//        }
//    }
//
////if it stopps
//    private void checkBallStopped() {
//        if (Math.sqrt(ballVelocityX * ballVelocityX + ballVelocityY * ballVelocityY) < 0.01) {
//            System.out.println("Ball has stopped");
//            ballInMotion = false;
//        }
//    }
//
//    //is it outside bounds
//    private void checkOutOfBounds() {
//        if (ballPosition.x < boundaryMinX || ballPosition.x > boundaryMaxX || ballPosition.y < boundaryMinY || ballPosition.y > boundaryMaxY) {
//            System.out.println("Ball is out");
//            outOfBounds = true;
//            ballInMotion = false;
//        }
//    }
//
//
//    //to shoot the ball NEEDS CHANGE
//    private void shootBall() {
//        // CHANGE THIS
//        ballVelocityX = 5.0;
//        ballVelocityY = 5.0;
//
//        //reset physics engine
//        physicsEngine = new PhysicsEngine(
//            ballPosition.x, ballPosition.y,
//            ballVelocityX, ballVelocityY,
//            physicsEngine.getTimeStep(), physicsEngine.getStartTime(), physicsEngine.getEndTime(),
//            physicsEngine.getkFrictionCoef(), physicsEngine.getsFrictionCoef(), physicsEngine.getgCoef(),
//            physicsEngine.getHeight()
//        );
//
//        ballInMotion = true;
//        shotCount++;
//        System.out.println("Shot count: " + shotCount);
//
//        if (shotCount >= maxStrokes) {
//            System.out.println("Maximum strokes reached. Try again");
//            resetLevel();
//        }
//    }
//
//
//
//
//    //this will restart with initial conditions change as well
//    private void resetLevel() {
//        shotCount = 0;
//        ballPosition.set(0, 0, 0);
//        ballVelocityX = 0;
//        ballVelocityY = 0;
//        ballInMotion = false;
//        outOfBounds = false;
//    }
//}
//
//
//
