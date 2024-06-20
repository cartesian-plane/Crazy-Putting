package org.ken22.players;

import net.objecthunter.exp4j.Expression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.TestUtils;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.players.bots.SimplePlanarApproximationBot;

class SimplePlanarApproximationBotTest {

    static GolfCourse course;
    static Expression expr;

    @BeforeAll
    static void setUp() {
        course = TestUtils.course("sin(x)sin(y).json");
        expr = TestUtils.expr(course);
    }

    @Test
    void play() {
        SimplePlanarApproximationBot bot = new SimplePlanarApproximationBot();
        var result = bot.play(new StateVector4(0, 0, 1, 0), course);
        var expected = TestUtils.prediction.apply(result, course);
        System.out.println(expected);
    }
}
