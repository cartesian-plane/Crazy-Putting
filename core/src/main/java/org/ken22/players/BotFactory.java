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
        return new HillClimbingBot(course, settings.errorFunction, settings.differentiator, settings.odesolver, settings.stepSize);
    }

    public SimplePlanarApproximationBot planarApproximationBot() {
        return new SimplePlanarApproximationBot();
    }
}
