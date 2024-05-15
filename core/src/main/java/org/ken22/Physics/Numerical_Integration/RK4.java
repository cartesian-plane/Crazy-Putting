package org.ken22.Physics.Numerical_Integration;

import net.objecthunter.exp4j.Expression;
import org.ken22.Physics.Numerical_Derivation.NumDerivationMethod;
import org.ken22.Physics.Vectors.GVec4;
import org.ken22.interfaces.IFunc;
import java.util.ArrayList;

import static org.ken22.Physics.Vectors.GVec4.copy;

public class RK4 implements NumIntegrationMethod {

    public GVec4 execute(ArrayList<GVec4> stateVectors, double timeStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, Expression terrain, NumDerivationMethod differentiator) {

        GVec4 stateVector = copy(stateVectors.getLast());
        stateVector.skim();
        ArrayList<Double> initial = stateVector.getVector();
//      System.out.println("Initial state vector: " + initial.toString());

        ArrayList<Double> k1 = new ArrayList<Double>();
        k1.add(funcx.apply(initial));
        k1.add(funcy.apply(initial));
//      System.out.println("k1: " + k1.toString());

        ArrayList<Double> k2 = compute_k_i(initial, timeStep, 0.5, funcx, funcy, k1, terrain, differentiator);
//      System.out.println("k2: " + k2.toString());

        ArrayList<Double> k3 = compute_k_i(initial, timeStep, 0.5, funcx, funcy, k2, terrain, differentiator);
//      System.out.println("k3: " + k3.toString());

        ArrayList<Double> k4 = compute_k_i(initial, timeStep, 1.0, funcx, funcy, k3, terrain, differentiator);
//      System.out.println("k4: " + k4.toString());

        double _vx = (timeStep/6)*(k1.get(0) + 2*k2.get(0) + 2*k3.get(0) + k4.get(0));
        double _vy = (timeStep/6)*(k1.get(1) + 2*k2.get(1) + 2*k3.get(1) + k4.get(1));
//      System.out.println("ax: " + ax + ", ay: " + ay);

        stateVector.set(0, initial.get(0) + timeStep);
        stateVector.set(1, initial.get(1) + timeStep*initial.get(3));
        stateVector.set(2, initial.get(2) + timeStep*initial.get(4));
        stateVector.set(3, initial.get(3) + _vx);
        stateVector.set(4, initial.get(4) + _vy);
        differentiator.gradients(stateVector, terrain, timeStep);
//      System.out.println("Final state vector: " + stateVector.getVector().toString());

        return stateVector;
    }

    private ArrayList<Double> compute_k_i(ArrayList<Double> initial, double timeStep, double subStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, ArrayList<Double> k_previous, Expression terrain, NumDerivationMethod differentiator) {

        ArrayList<Double> k_i = new ArrayList<Double>();
        ArrayList<Double> interim = interim_vector(initial, timeStep, subStep, k_previous, terrain, differentiator);
        k_i.add(funcx.apply(interim));
        k_i.add(funcy.apply(interim));
//      System.out.println("k_i: " + k_i.toString());

        return k_i;
    }

    private ArrayList<Double> interim_vector(ArrayList<Double> initial, double timeStep, double subStep, ArrayList<Double> k_i, Expression terrain, NumDerivationMethod differentiator) {

        ArrayList<Double> interim = new ArrayList<Double>();

        double t = initial.get(0);
        double x = initial.get(1);
        double y = initial.get(2);
        double vx = initial.get(3);
        double vy = initial.get(4);

        interim.add(t + timeStep * subStep);
        interim.add(x + vx * timeStep * subStep);
        interim.add(y + vy * timeStep * subStep);
        interim.add(vx + k_i.get(0) * timeStep * subStep);
        interim.add(vy + k_i.get(1) * timeStep * subStep);
        interim.add(0.0);
        interim.add(0.0);
        //Re-evaluate gradients at current point
        differentiator.gradients(interim, terrain, timeStep);

        return interim;
    }
}

