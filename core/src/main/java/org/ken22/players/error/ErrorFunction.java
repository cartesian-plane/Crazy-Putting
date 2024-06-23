package org.ken22.players.error;

import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.vectors.StateVector4;

public interface ErrorFunction {
    public double calculateError(StateVector4 finalState);
    public void init(GolfCourse course, PhysicsFactory physicsFactory);
}
