package org.ken22.players.error;

import org.ken22.input.courseinput.GolfCourse;

public interface ErrorFunction {
    public double calculateError(double ballX, double ballY);
    public void init(GolfCourse course);
}
