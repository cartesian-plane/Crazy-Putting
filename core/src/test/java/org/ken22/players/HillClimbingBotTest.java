package org.ken22.players;

import net.objecthunter.exp4j.Expression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.TestUtils;
import org.ken22.physicsx.differentiators.ThreePointCenteredDifference;
import org.ken22.physicsx.odesolvers.RK4;
import org.ken22.physicsx.vectors.StateVector4;

class HillClimbingBotTest {

    static GolfCourse course;
    static Expression expr;

    @BeforeAll
    static void setUp() {
        course = TestUtils.course("test-golf-course.json");
        expr = TestUtils.expr(course);
    }

    @Test
    void play() {
        //System.out.println(expr.setVariable("x", 5).setVariable("y", 5).evaluate());
        HillClimbingBot bot = new HillClimbingBot( null, new ThreePointCenteredDifference(),
            new RK4(), 0.016);
        var result = bot.play(new StateVector4(2, 2, 3, 3), course);
        System.out.println(result);
    }
}
