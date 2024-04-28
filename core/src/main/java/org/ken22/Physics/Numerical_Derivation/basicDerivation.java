package org.ken22.Physics.Numerical_Derivation;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.Physics.Vectors.GVec4;
import org.ken22.interfaces.IFunc;

import java.util.ArrayList;

public class basicDerivation {

    public basicDerivation(ArrayList<Double> stateVector, IFunc<Double, Double> terrain, double timeStep) {
        this.gradients(stateVector, terrain, timeStep);
    }

    public GVec4 gradients(GVec4 stateVector, Expression terrain, double timeStep) {

        terrain
                .setVariable("x", 1)
                .setVariable("y", 2);
        double result = terrain.evaluate();

        ArrayList<Double> gradients = new ArrayList<Double>();
        ArrayList<Double> coords = new ArrayList<Double>(); // {x,y}
        coords.add(stateVector.get(1));
        coords.add(stateVector.get(2));

        double height = terrain
            .setVariable()

        //With respect to x
        double delX = stateVector.get(3) * timeStep;
        ArrayList<Double> coordsX = new ArrayList<Double>();
        coordsX.add(coords.get(0)+delX);
        coordsX.add(coords.get(1));

        terrain
            .setVariable("x", 1)
            .setVariable("y", 2);
        double height_x = terrain.evaluate();

        double dhdx = (terrain.apply(coordsX) - terrain.apply(coords))/delX;

        //With respect to y
        double delY = stateVector.get(4) * timeStep;
        ArrayList<Double> coordsY = new ArrayList<Double>();
        coordsY.add(coords.get(0));
        coordsY.add(coords.get(1)+delY);
        double dhdy = (terrain.apply(coordsY) - terrain.apply(coords))/delY;

        stateVector.add(dhdx);
        stateVector.add(dhdy;

        return stateVector;
    }
}
