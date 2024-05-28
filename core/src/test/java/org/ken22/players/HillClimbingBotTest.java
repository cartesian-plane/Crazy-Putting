package org.ken22.players;

import net.objecthunter.exp4j.Expression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physicsx.TestUtils;
import org.ken22.physicsx.differentiators.FivePointCenteredDifference;
import org.ken22.physicsx.differentiators.ThreePointCenteredDifference;
import org.ken22.physicsx.odesolvers.RK4;
import org.ken22.physicsx.vectors.StateVector4;

import static org.junit.jupiter.api.Assertions.*;

class HillClimbingBotTest {

    static GolfCourse course;
    static Expression expr;

    @BeforeAll
    static void setUp() {
        course = TestUtils.course("true-golf-course.json");
        expr = TestUtils.expr(course);
    }

    @Test
    void play() {
        //System.out.println(expr.setVariable("x", 5).setVariable("y", 5).evaluate());
        HillClimbingBot bot = new HillClimbingBot(new StateVector4(0, 0, 1, 0), null, course, new ThreePointCenteredDifference(),
            new RK4(), 0.016);
        var result = bot.play(new StateVector4(0, 0, 1, 0), course);
        System.out.println(result);
    }
}
