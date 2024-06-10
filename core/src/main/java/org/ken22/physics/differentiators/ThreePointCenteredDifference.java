package org.ken22.physics.differentiators;

import java.util.function.Function;

public class ThreePointCenteredDifference implements Differentiator {
    public double differentiate(double h, double x, Function<Double, Double> f) {
        double xx = f.apply(x + h);
        double yy = f.apply(x - h);
        return (xx -  yy) / (2 * h);
    }
}
