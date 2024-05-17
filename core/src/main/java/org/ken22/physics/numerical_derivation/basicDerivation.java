package org.ken22.physics.numerical_derivation;

import net.objecthunter.exp4j.Expression;
import org.ken22.physics.vectors.GVec4;

import java.util.ArrayList;

public class basicDerivation implements NumDerivationMethod {
    public basicDerivation () {
    }

    public void gradientsAdaptive(GVec4 stateVector, Expression terrain, double timeStep) {
        ArrayList<Double> coords = new ArrayList<Double>(); // {x,y}
        coords.add(stateVector.get(1));
        coords.add(stateVector.get(2));

        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1));
        double height = terrain.evaluate();

        //With respect to x
        double delX = stateVector.get(3) * timeStep;

        terrain
            .setVariable("x", coords.get(0)+delX)
            .setVariable("y", coords.get(1));
        double height_x = terrain.evaluate();

        double dhdx = (height_x - height)/delX;

        //With respect to y
        double delY = stateVector.get(4) * timeStep;
        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1)+delY);
        double height_y = terrain.evaluate();

        double dhdy = (height_y - height)/delY;

        stateVector.add(dhdx);
        stateVector.add(dhdy);
    }

    public void gradients(GVec4 stateVector, Expression terrain, double timeStep) {

        //Using timeStep for both ∆x and ∆y for no reason other than it's convenient :D
        ArrayList<Double> coords = new ArrayList<Double>(); // {x,y}
        coords.add(stateVector.get(1));
        coords.add(stateVector.get(2));

        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1));
        double height = terrain.evaluate();

        //With respect to x
        terrain
            .setVariable("x", coords.get(0)+timeStep)
            .setVariable("y", coords.get(1));
        double height_x = terrain.evaluate();

        double dhdx = (height_x - height)/timeStep;

        //With respect to y
        double delY = stateVector.get(4) * timeStep;
        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1)+timeStep);
        double height_y = terrain.evaluate();

        double dhdy = (height_y - height)/timeStep;

        stateVector.add(dhdx);
        stateVector.add(dhdy);
    }

    public void gradients(ArrayList<Double> stateVector, Expression terrain, double timeStep) {

        //Using timeStep for both ∆x and ∆y for no reason other than it's convenient :D
        ArrayList<Double> coords = new ArrayList<Double>(); // {x,y}
        coords.add(stateVector.get(1));
        coords.add(stateVector.get(2));

        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1));
        double height = terrain.evaluate();

        //With respect to x
        terrain
            .setVariable("x", coords.get(0)+timeStep)
            .setVariable("y", coords.get(1));
        double height_x = terrain.evaluate();

        double dhdx = (height_x - height)/timeStep;

        //With respect to y
        double delY = stateVector.get(4) * timeStep;
        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1)+timeStep);
        double height_y = terrain.evaluate();

        double dhdy = (height_y - height)/timeStep;

        stateVector.add(dhdx);
        stateVector.add(dhdy);
    }
}
