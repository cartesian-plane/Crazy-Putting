package org.ken22.Physics.Numerical_Derivation;

import net.objecthunter.exp4j.Expression;
import org.ken22.Physics.Vectors.GVec4;

import java.util.ArrayList;

public class fivePointDifference implements NumDerivationMethod {

    public void gradients(GVec4 stateVector, Expression terrain, double timeStep) {

        //Using timeStep for both ∆x and ∆y for no reason other than it's convenient :D
        ArrayList<Double> coords = new ArrayList<Double>(); // {x,y}
        coords.add(stateVector.get(1));
        coords.add(stateVector.get(2));

        // Five-point difference is used separately for each gradient
        // For alternative methods see the following:
        // Handbook of formulas, 25.3.21 - 33 https://personal.math.ubc.ca/~cbm/aands/abramowitz_and_stegun.pdf
        // Taylor for 2 variable functions: https://math.libretexts.org/Bookshelves/Calculus/Supplemental_Modules_(Calculus)/Multivariable_Calculus/3%3A_Topics_in_Partial_Derivatives/Taylor__Polynomials_of_Functions_of_Two_Variables
        // Method for stencils https://en.wikipedia.org/wiki/Five-point_stencil#cite_ref-3

        double dhdx = centredStencilX(terrain, timeStep, coords);
        double dhdy = centredStencilY(terrain, timeStep, coords);

        stateVector.set(5, dhdx);
        stateVector.set(6, dhdy);
    }

    public void gradients(ArrayList<Double> stateVector, Expression terrain, double timeStep) {

        //Using timeStep for both ∆x and ∆y for no reason other than it's convenient :D
        ArrayList<Double> coords = new ArrayList<Double>(); // {x,y}
        coords.add(stateVector.get(1));
        coords.add(stateVector.get(2));

        // Five-point difference is used separately for each gradient
        // For alternative methods see the following:
        // Handbook of formulas, 25.3.21 - 33 https://personal.math.ubc.ca/~cbm/aands/abramowitz_and_stegun.pdf
        // Taylor for 2 variable functions: https://math.libretexts.org/Bookshelves/Calculus/Supplemental_Modules_(Calculus)/Multivariable_Calculus/3%3A_Topics_in_Partial_Derivatives/Taylor__Polynomials_of_Functions_of_Two_Variables
        // Method for stencils https://en.wikipedia.org/wiki/Five-point_stencil#cite_ref-3

        double dhdx = centredStencilX(terrain, timeStep, coords);
        double dhdy = centredStencilY(terrain, timeStep, coords);

        stateVector.set(5, dhdx);
        stateVector.add(6, dhdy);
    }

    public double centredStencilX(Expression terrain, double timeStep, ArrayList<Double> coords) {
        double grad = 0;
        terrain
            .setVariable("x", coords.get(0)-timeStep)
            .setVariable("y", coords.get(1));
        double backward1 = terrain.evaluate();
        terrain
            .setVariable("x", coords.get(0)-2*timeStep)
            .setVariable("y", coords.get(1));
        double backward2 = terrain.evaluate();
        terrain
            .setVariable("x", coords.get(0)+timeStep)
            .setVariable("y", coords.get(1));
        double forward1 = terrain.evaluate();
        terrain
            .setVariable("x", coords.get(0)+2*timeStep)
            .setVariable("y", coords.get(1));
        double forward2 = terrain.evaluate();

        grad = (forward2 - 8*forward1 + 8*backward1 - backward2)/(12*timeStep);

        return grad;
    }

    public double centredStencilY(Expression terrain, double timeStep, ArrayList<Double> coords) {
        double grad = 0;
        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1)-timeStep);
        double backward1 = terrain.evaluate();
        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1)-2*timeStep);
        double backward2 = terrain.evaluate();
        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1)+timeStep);
        double forward1 = terrain.evaluate();
        terrain
            .setVariable("x", coords.get(0))
            .setVariable("y", coords.get(1)+2*timeStep);
        double forward2 = terrain.evaluate();

        grad = (forward2 - 8*forward1 + 8*backward1 - backward2)/(12*timeStep);

        return grad;
    }

}
