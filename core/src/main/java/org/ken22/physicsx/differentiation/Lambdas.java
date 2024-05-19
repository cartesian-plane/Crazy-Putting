package org.ken22.physicsx.differentiation;

import org.ken22.physicsx.vectors.StateVector4;

import java.util.function.Function;

public class Lambdas {

    Function<StateVector4, Double> x = (stateVector4) -> stateVector4.vx();
    Function<StateVector4, Double> y = (stateVector4) -> stateVector4.vy();

    Function<StateVector4, Double> vx = (stateVector4) -> stateVector4.;

}
