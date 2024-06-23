package org.ken22.players.bots;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.Player;
import org.ken22.utils.MathUtils;

public class InitialGuessBot implements Player {
    private GolfCourse course;

    public InitialGuessBot(GolfCourse course) {
        this.course = course;
    }

    @Override
    public StateVector4 play(StateVector4 state) {

        var distance = MathUtils.magnitude(state.x() - course.targetXcoord(), state.y() - course.targetYcoord());
        var acceleration = -course.kineticFrictionGrass() * course.gravitationalConstant(); // assuming flat plane
        var initVelocity = Math.sqrt(-2 * acceleration * distance);
        var vx = initVelocity * (course.targetXcoord() - state.x()) / distance;
        var vy = initVelocity * (course.targetYcoord() - state.y()) / distance;

        return new StateVector4(state.x(), state.y(), vx, vy);
    }
}
