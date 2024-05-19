package org.ken22.physics.numerical_derivation;

import net.objecthunter.exp4j.Expression;
import org.ken22.physics.vectors.GVec4;
import java.util.ArrayList;

public class fivePointDifference implements NumDerivationMethod {

    @Override
    public void gradients(GVec4 stateVector, Expression terrain, double timeStep) {
        // Extract x and y coordinates from the state vector
        double x = stateVector.get(1);
        double y = stateVector.get(2);
        System.out.println("Calculating gradients for GVec4:");
        System.out.println("x = " + x + ", y = " + y + ", timeStep = " + timeStep);

        // Calculate gradients using the centred stencil method
        double dhdx = centredStencilX(terrain, timeStep, x, y);
        double dhdy = centredStencilY(terrain, timeStep, x, y);

        // Update the state vector with the calculated gradients
        stateVector.set(5, dhdx);
        stateVector.set(6, dhdy);
        System.out.println("Gradient dhdx = " + dhdx + ", dhdy = " + dhdy);
    }

    @Override
    public void gradients(ArrayList<Double> stateVector, Expression terrain, double timeStep) {
        // Extract x and y coordinates from the state vector
        double x = stateVector.get(1);
        double y = stateVector.get(2);
        System.out.println("Calculating gradients for ArrayList:");
        System.out.println("x = " + x + ", y = " + y + ", timeStep = " + timeStep);

        // Calculate gradients using the centred stencil method
        double dhdx = centredStencilX(terrain, timeStep, x, y);
        double dhdy = centredStencilY(terrain, timeStep, x, y);

        // Update the state vector with the calculated gradients
        stateVector.set(5, dhdx);
        stateVector.set(6, dhdy);
        System.out.println("Gradient dhdx = " + dhdx + ", dhdy = " + dhdy);
    }

    private double centredStencilX(Expression terrain, double timeStep, double x, double y) {
        System.out.println("Calculating centredStencilX for x = " + x + ", y = " + y);

        terrain.setVariable("x", x - 2 * timeStep).setVariable("y", y);
        double backward2 = terrain.evaluate();
        System.out.println("backward2 = " + backward2);

        terrain.setVariable("x", x - timeStep).setVariable("y", y);
        double backward1 = terrain.evaluate();
        System.out.println("backward1 = " + backward1);

        terrain.setVariable("x", x + timeStep).setVariable("y", y);
        double forward1 = terrain.evaluate();
        System.out.println("forward1 = " + forward1);

        terrain.setVariable("x", x + 2 * timeStep).setVariable("y", y);
        double forward2 = terrain.evaluate();
        System.out.println("forward2 = " + forward2);

        // Calculate the centred finite difference for x gradient
        double dhdx = (backward2 - 8 * backward1 + 8 * forward1 - forward2) / (12.0 * timeStep);
        System.out.println("centredStencilX dhdx = " + dhdx);
        return dhdx;
    }

    private double centredStencilY(Expression terrain, double timeStep, double x, double y) {
        System.out.println("Calculating centredStencilY for x = " + x + ", y = " + y);

        terrain.setVariable("x", x).setVariable("y", y - 2 * timeStep);
        double backward2 = terrain.evaluate();
        System.out.println("backward2 = " + backward2);

        terrain.setVariable("x", x).setVariable("y", y - timeStep);
        double backward1 = terrain.evaluate();
        System.out.println("backward1 = " + backward1);

        terrain.setVariable("x", x).setVariable("y", y + timeStep);
        double forward1 = terrain.evaluate();
        System.out.println("forward1 = " + forward1);

        terrain.setVariable("x", x).setVariable("y", y + 2 * timeStep);
        double forward2 = terrain.evaluate();
        System.out.println("forward2 = " + forward2);

        // Calculate the centred finite difference for y gradient
        double dhdy = (backward2 - 8 * backward1 + 8 * forward1 - forward2) / (12.0 * timeStep);
        System.out.println("centredStencilY dhdy = " + dhdy);
        return dhdy;
    }
}
