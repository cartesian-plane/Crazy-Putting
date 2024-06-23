package org.ken22.players.bots;

import org.ken22.input.settings.BotSettings;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.players.bots.hillclimbing.HillClimber;
import org.ken22.players.error.ErrorFunction;

public class BotFactory {
    private BotSettings settings;
    private PhysicsFactory physicsFactory;

    public BotFactory(BotSettings settings, PhysicsFactory physicsFactory) {
        this.settings = settings;
        this.physicsFactory = physicsFactory;
    }

    public HillClimbingBot hillClimbingBot(GolfCourse course) {
        return new HillClimbingBot(course, errorFunction(course),
            settings.differentiatorType.getDifferentiator(), settings.odesolverType.getSolver(), settings.stepSize);
    }

    public HillClimber hillClimber(GolfCourse course) {
        return null;
    }

    public NewtonRaphsonBot newtonRaphsonBot(GolfCourse course) {
        return new NewtonRaphsonBot(errorFunction(course), settings.stepSize);
    }

    public SimplePlanarApproximationBot planarApproximationBot(GolfCourse course) {
        return new SimplePlanarApproximationBot(course);
    }

    private ErrorFunction errorFunction(GolfCourse course) {
        ErrorFunction errorFunction = settings.errorFunctionType
            .getErrorFunction(course, physicsFactory, settings.gridPathfindingType.getPathfinding(), settings.weightingType.getWeighting());
        errorFunction.init(course, physicsFactory);
        return errorFunction;
    }
}
