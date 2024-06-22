package org.ken22.physics;

import org.ken22.input.settings.GeneralSettings;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.engine.PhysicsEngine;

public class PhysicsFactory {
    private GeneralSettings settings;

    public PhysicsFactory(GeneralSettings settings) {
        this.settings = settings;
    }

    public PhysicsEngine physicsEngine(GolfCourse course) {
        return new PhysicsEngine(course, null, settings.stepSize, settings.differentiator, settings.solver, !settings.useSimplifiedPhysics);
    }
}
