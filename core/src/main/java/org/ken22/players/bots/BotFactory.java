package org.ken22.players.bots;

import org.ken22.input.settings.BotSettings;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.bots.hillclimbing.GradientDescent;
import org.ken22.players.bots.hillclimbing.SimulatedAnnealing;
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

    public GradientDescent gradientDescent(GolfCourse course) {
        return new GradientDescent(0.01, course.targetRadius, settings.sidewaysMoves, settings.randomRestarts,
            course, settings.odesolverType.getSolver(), settings.differentiatorType.getDifferentiator(),
            settings.stepSize, errorFunction(course));
    }

    public SimulatedAnnealing simulatedAnnealing(GolfCourse course)  {
        return new SimulatedAnnealing(course, settings.odesolverType.getSolver(),
            settings.differentiatorType.getDifferentiator(), settings.stepSize, 100,1000, errorFunction(course));
    }

    public NewtonRaphsonBot newtonRaphsonBot(GolfCourse course) {
        return new NewtonRaphsonBot(new InitialGuessBot(course), errorFunction(course), settings.stepSize);
    }

    public SimplePlanarApproximationBot planarApproximationBot(GolfCourse course) {
        return new SimplePlanarApproximationBot(course);
    }

    public InitialGuessBot initialGuessBot(GolfCourse course) {
        return new InitialGuessBot(course);
    }

    private ErrorFunction errorFunction(GolfCourse course) {
        ErrorFunction errorFunction = settings.errorFunctionType
            .getErrorFunction(course, physicsFactory, settings.gridPathfindingType.getPathfinding(), settings.weightingType.getWeighting());
        errorFunction.init(course, physicsFactory);
        return errorFunction;
    }
}
