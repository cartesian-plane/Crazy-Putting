package org.ken22.utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.input.courseinput.GolfCourse;

@Deprecated
public abstract class GolfExpression {
    public static Expression expr(GolfCourse course) {
        return new ExpressionBuilder(course.courseProfile())
            .variables("x", "y")
            .build();
    }
}
