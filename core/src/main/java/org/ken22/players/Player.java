package org.ken22.players;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.vectors.GVec4;

import java.util.ArrayList;

public interface Player {
    public ArrayList<Double> play(GVec4 state, GolfCourse course);
}
