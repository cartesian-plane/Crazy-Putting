package org.ken22.physics.numerical_derivation;

import net.objecthunter.exp4j.Expression;
import org.ken22.physics.vectors.GVec4;
import java.util.ArrayList;

public class fivePointDifference implements NumDerivationMethod {

    public void gradients(GVec4 stateVector, Expression terrain, double timeStep) {

        // Extract x and y coordinates from the state vector
        double x = stateVector.get(1);
        double y = stateVector.get(2);

        // Calculate gradients using the centred stencil method
        double dhdx = centredStencilX(terrain, timeStep, x, y);
        double dhdy = centredStencilY(terrain, timeStep, x, y);

        // Update the state vector with the calculated gradients
        stateVector.set(5, dhdx);
        stateVector.set(6, dhdy);
    }

    public void gradients(ArrayList<Double> stateVector, Expression terrain, double timeStep) {

        // Extract x and y coordinates from the state vector
        double x = stateVector.get(1);
        double y = stateVector.get(2);

        // Calculate gradients using the centred stencil method
        double dhdx = centredStencilX(terrain, timeStep, x, y);
        double dhdy = centredStencilY(terrain, timeStep, x, y);

        // Update the state vector with the calculated gradients
        stateVector.set(5, dhdx);
        stateVector.set(6, dhdy);
    }

    private double centredStencilX(Expression terrain, double timeStep, double x, double y) {

        // Set variables and evaluate the terrain function at the required points for x gradient
        terrain.setVariable("x", x - 2 * timeStep).setVariable("y", y);
        double backward2 = terrain.evaluate();

        terrain.setVariable("x", x - timeStep).setVariable("y", y);
        double backward1 = terrain.evaluate();

        terrain.setVariable("x", x + timeStep).setVariable("y", y);
        double forward1 = terrain.evaluate();

        terrain.setVariable("x", x + 2 * timeStep).setVariable("y", y);
        double forward2 = terrain.evaluate();

        // Calculate the centred finite difference for x gradient
        return (backward2 - 8 * backward1 + 8 * forward1 - forward2) / (12.0 * timeStep);
    }

    private double centredStencilY(Expression terrain, double timeStep, double x, double y) {

        // Set variables and evaluate the terrain function at the required points for y gradient
        terrain.setVariable("x", x).setVariable("y", y - 2 * timeStep);
        double backward2 = terrain.evaluate();

        terrain.setVariable("x", x).setVariable("y", y - timeStep);
        double backward1 = terrain.evaluate();

        terrain.setVariable("x", x).setVariable("y", y + timeStep);
        double forward1 = terrain.evaluate();

        terrain.setVariable("x", x).setVariable("y", y + 2 * timeStep);
        double forward2 = terrain.evaluate();

        // Calculate the centred finite difference for y gradient
        return (backward2 - 8 * backward1 + 8 * forward1 - forward2) / (12.0 * timeStep);
    }
}

