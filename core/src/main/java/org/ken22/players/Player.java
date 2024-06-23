package org.ken22.players;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.StateVector4;

public interface Player {
    public StateVector4 play(StateVector4 state);
}
