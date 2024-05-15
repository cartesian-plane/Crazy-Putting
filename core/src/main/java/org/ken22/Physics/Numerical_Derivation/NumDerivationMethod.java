package org.ken22.Physics.Numerical_Derivation;

import net.objecthunter.exp4j.Expression;
import org.ken22.Physics.Vectors.GVec4;

import java.util.ArrayList;

public interface NumDerivationMethod {

    public void gradients(GVec4 stateVector, Expression terrain, double timeStep);

    public void gradients(ArrayList<Double> stateVector, Expression terrain, double timeStep);

}
