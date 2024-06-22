package org.ken22.players.error;

import org.ken22.input.courseinput.GolfCourse;
import static org.ken22.physics.utils.PhysicsUtils.magnitude;

public class EuclideanError implements ErrorFunction {
    private GolfCourse course;

    public void init(GolfCourse course) {
        this.course = course;
    }

    public double calculateError(double ballX, double ballY) {
        return magnitude(ballX - course.targetXcoord, ballY - course.targetYcoord);
    }
}
