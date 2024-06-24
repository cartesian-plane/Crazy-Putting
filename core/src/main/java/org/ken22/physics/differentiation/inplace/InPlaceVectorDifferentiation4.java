package org.ken22.physics.differentiation.inplace;

import org.ken22.physics.vectors.StateVector4;

import java.util.function.BiFunction;

/**
 * This class is used to store the differentiation functions for a 4D vector representing the state of a solid in 2D space.
 * The functions are used to calculate the rate of change of the x, y, vx and vy components of the vector.
 */
public interface InPlaceVectorDifferentiation4 extends BiFunction<Double, StateVector4, StateVector4> {

}
