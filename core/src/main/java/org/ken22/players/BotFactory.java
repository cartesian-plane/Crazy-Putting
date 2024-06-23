package org.ken22.players;

import org.ken22.input.settings.BotSettings;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.players.bots.HillClimbingBot;
import org.ken22.players.bots.NewtonRaphsonBot;
import org.ken22.players.bots.SimplePlanarApproximationBot;
import org.ken22.players.bots.hillclimbing.HillClimber;

public class BotFactory {
    private BotSettings settings;

    public BotFactory(BotSettings settings, PhysicsFactory physicsFactory) {
        this.settings = settings;
    }

    public HillClimbingBot hillClimbingBot(GolfCourse course) {
        return new HillClimbingBot(course,
            settings.errorFunctionType.getErrorFunction(course, settings.gridPathfindingType.getPathfinding(), settings.weightingType.getWeighting()),
            settings.differentiatorType.getDifferentiator(), settings.odesolverType.getSolver(), settings.stepSize);
    }

    public HillClimber hillClimber(GolfCourse course) {
        return new HillClimber(course, settings.randomRestarts, 10);
    }

    public NewtonRaphsonBot newtonRaphsonBot(GolfCourse course) {
        return new NewtonRaphsonBot(
            settings.errorFunctionType.getErrorFunction(course, settings.gridPathfindingType.getPathfinding(), settings.weightingType.getWeighting()),
            settings.stepSize);
    }

    public SimplePlanarApproximationBot planarApproximationBot(GolfCourse course) {
        return new SimplePlanarApproximationBot(course);
    }
}
