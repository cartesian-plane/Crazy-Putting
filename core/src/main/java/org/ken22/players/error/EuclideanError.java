package org.ken22.players.error;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.StateVector4;

import static org.ken22.physics.utils.PhysicsUtils.magnitude;

public class EuclideanError implements ErrorFunction {
    private GolfCourse course;

    @Override
    public void init(GolfCourse course) {
        this.course = course;
    }

    public double calculateError(StateVector4 state) {
        return magnitude(state.x() - course.targetXcoord, state.y() - course.targetYcoord);
    }
}
