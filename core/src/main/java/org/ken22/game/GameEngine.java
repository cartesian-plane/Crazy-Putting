package org.ken22.game;

import net.objecthunter.exp4j.Expression;
import org.ken22.graphics.GolfScreen;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.engine.PhysicsEngine;
import org.ken22.physicsx.vectors.StateVector4;
import org.ken22.players.HumanPlayer;
import org.ken22.players.Player;

public class GameEngine {
    GameLoop gameLoop;
    PhysicsEngine physicsEngine;
    GolfScreen screen;
    Player player = new HumanPlayer();

    public GameEngine(GolfCourse course, Expression expression, StateVector4 initSV, GolfScreen screen) {
        this.physicsEngine = new PhysicsEngine(course, initSV, 0.0001);
        //gameLoop = new GameLoop(course, physicsEngine, screen, player);
    }
}
