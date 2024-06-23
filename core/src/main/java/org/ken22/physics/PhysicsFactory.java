package org.ken22.physics;

import org.ken22.input.settings.GeneralSettings;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.engine.PhysicsEngine;
import org.ken22.physics.vectors.StateVector4;

public class PhysicsFactory {
    private GeneralSettings settings;

    public PhysicsFactory(GeneralSettings settings) {
        this.settings = settings;
    }

    public PhysicsEngine physicsEngine(GolfCourse course) {
        return new PhysicsEngine(course, null, settings.stepSize,
            settings.differentiatorType.getDifferentiator(), settings.solverType.getSolver(),
            !settings.useSimplifiedPhysics);
    }

   public StateVector4 runSimulation(StateVector4 initialState, GolfCourse course) {
        PhysicsEngine engine = physicsEngine(course);
        return engine.solve();
   }
}
