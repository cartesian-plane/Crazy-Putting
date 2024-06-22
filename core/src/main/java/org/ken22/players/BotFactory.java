package org.ken22.players;

import org.ken22.input.settings.BotSettings;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.players.bots.HillClimbingBot;
import org.ken22.players.bots.SimplePlanarApproximationBot;

public class BotFactory {
    private BotSettings settings;

    public BotFactory(BotSettings settings) {
        this.settings = settings;
    }

    public HillClimbingBot hillClimbingBot(GolfCourse course) {
        return new HillClimbingBot(course,
            settings.errorFunctionType.getErrorFunction(settings.gridPathfindingType.getPathfinding(), settings.weightingType.getWeighting()),
            settings.differentiatorType.getDifferentiator(), settings.odesolverType.getSolver(), settings.stepSize);
    }

    public SimplePlanarApproximationBot planarApproximationBot(GolfCourse course) {
        return new SimplePlanarApproximationBot(course);
    }
}
