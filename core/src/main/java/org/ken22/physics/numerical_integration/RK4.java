package org.ken22.physics.numerical_integration;

import net.objecthunter.exp4j.Expression;
import org.ken22.physics.numerical_derivation.NumDerivationMethod;
import org.ken22.physics.vectors.GVec4;
import org.ken22.interfaces.IFunc;

import java.util.ArrayList;

import static org.ken22.physics.vectors.GVec4.copy;

public class RK4 implements NumIntegrationMethod {

    public GVec4 execute(ArrayList<GVec4> stateVectors, double timeStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, Expression terrain, NumDerivationMethod differentiator) {
        GVec4 stateVector = copy(stateVectors.get(stateVectors.size() - 1));
        ArrayList<Double> initial = stateVector.getVector();

        ArrayList<Double> k1 = computeK(initial, timeStep, 0.0, funcx, funcy, null, terrain, differentiator);
        ArrayList<Double> k2 = computeK(initial, timeStep, 0.5, funcx, funcy, k1, terrain, differentiator);
        ArrayList<Double> k3 = computeK(initial, timeStep, 0.5, funcx, funcy, k2, terrain, differentiator);
        ArrayList<Double> k4 = computeK(initial, timeStep, 1.0, funcx, funcy, k3, terrain, differentiator);

        double _vx = (timeStep / 6.0) * (k1.get(0) + 2 * k2.get(0) + 2 * k3.get(0) + k4.get(0));
        double _vy = (timeStep / 6.0) * (k1.get(1) + 2 * k2.get(1) + 2 * k3.get(1) + k4.get(1));

        stateVector.set(0, initial.get(0) + timeStep);
        stateVector.set(1, initial.get(1) + timeStep * initial.get(3));
        stateVector.set(2, initial.get(2) + timeStep * initial.get(4));
        stateVector.set(3, initial.get(3) + _vx);
        stateVector.set(4, initial.get(4) + _vy);

        // Recalculate gradients and terrain height at the new state
        differentiator.gradients(stateVector, terrain, timeStep / 10.0);
        double height = terrain.setVariable("x", stateVector.get(1)).setVariable("y", stateVector.get(2)).evaluate();
        stateVector.set(7, height);

        return stateVector;
    }

    private ArrayList<Double> computeK(ArrayList<Double> initial, double timeStep, double subStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, ArrayList<Double> previousK, Expression terrain, NumDerivationMethod differentiator) {
        ArrayList<Double> interim = (previousK == null) ? initial : computeInterim(initial, timeStep, subStep, previousK, terrain, differentiator);
        ArrayList<Double> k = new ArrayList<>();

        k.add(funcx.apply(interim));
        k.add(funcy.apply(interim));

        return k;
    }

    private ArrayList<Double> computeInterim(ArrayList<Double> initial, double timeStep, double subStep, ArrayList<Double> k, Expression terrain, NumDerivationMethod differentiator) {
        ArrayList<Double> interim = new ArrayList<>();

        double t = initial.get(0);
        double x = initial.get(1);
        double y = initial.get(2);
        double vx = initial.get(3);
        double vy = initial.get(4);

        interim.add(t + timeStep * subStep);
        interim.add(x + vx * timeStep * subStep);
        interim.add(y + vy * timeStep * subStep);
        interim.add(vx + k.get(0) * timeStep * subStep);
        interim.add(vy + k.get(1) * timeStep * subStep);
        interim.add(0.0); // Placeholder for gradX
        interim.add(0.0); // Placeholder for gradY

        // Re-evaluate gradients at the interim point
        differentiator.gradients(interim, terrain, timeStep / 10.0);

        return interim;
    }
}
