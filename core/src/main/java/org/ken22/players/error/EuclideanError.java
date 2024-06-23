package org.ken22.players.error;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.vectors.StateVector4;

import static org.ken22.physics.utils.PhysicsUtils.magnitude;

public class EuclideanError implements ErrorFunction {
    private GolfCourse course;
    private PhysicsFactory physicsFactory;

    @Override
    public void init(GolfCourse course, PhysicsFactory physicsFactory) {
        this.course = course;
        this.physicsFactory = physicsFactory;
    }

    public double calculateError(StateVector4 state) {
        state = physicsFactory.runSimulation(state, course);
        return magnitude(state.x() - course.targetXcoord, state.y() - course.targetYcoord);
    }
}
