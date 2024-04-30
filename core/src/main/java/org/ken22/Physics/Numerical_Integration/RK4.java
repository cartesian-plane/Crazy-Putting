package org.ken22.Physics.Numerical_Integration;

import net.objecthunter.exp4j.Expression;
import org.ken22.Physics.Numerical_Derivation.NumDerivationMethod;
import org.ken22.Physics.System.PhysicsSystem;
import org.ken22.Physics.Vectors.GVec4;
import org.ken22.interfaces.IFunc;
import org.ken22.interfaces.ODESolution;

import java.util.ArrayList;

public class RK4 implements NumIntegrationMethod {

    public GVec4 execute(GVec4 stateVector, double timeStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, Expression terrain, NumDerivationMethod differentiator) {

        ArrayList<Double> initial = stateVector.getVector();
//        System.out.println("Initial state vector: " + initial.toString());

        ArrayList<Double> k1 = new ArrayList<Double>();
        k1.add(funcx.apply(initial));
        k1.add(funcy.apply(initial));
//        System.out.println("k1: " + k1.toString());

        ArrayList<Double> k2 = compute_k_i(initial, timeStep, 0.5, funcx, funcy, k1, terrain, differentiator);
//        System.out.println("k2: " + k2.toString());

        ArrayList<Double> k3 = compute_k_i(initial, timeStep, 0.5, funcx, funcy, k2, terrain, differentiator);
//        System.out.println("k3: " + k3.toString());

        ArrayList<Double> k4 = compute_k_i(initial, timeStep, 1.0, funcx, funcy, k3, terrain, differentiator);
//        System.out.println("k4: " + k4.toString());

        double ax = (timeStep/6)*(k1.get(0) + 2*k2.get(0) + 2*k3.get(0) + k4.get(0));
        double ay = (timeStep/6)*(k1.get(1) + 2*k2.get(1) + 2*k3.get(1) + k4.get(1));
//        System.out.println("ax: " + ax + ", ay: " + ay);

        stateVector.set(0, initial.get(0) + timeStep);
        stateVector.set(1, initial.get(1) + timeStep*initial.get(3));
        stateVector.set(2, initial.get(2) + timeStep*initial.get(4));
        stateVector.set(3, initial.get(3) + ax);
        stateVector.set(4, initial.get(4) + ay);
        stateVector.skim(); //remove gradients
        stateVector.skim(); //remove gradients

//        System.out.println("Final state vector: " + stateVector.getVector().toString());

        return stateVector;
    }

    public ArrayList<Double> compute_k_i(ArrayList<Double> vector, double timeStep, double subStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, ArrayList<Double> k_previous, Expression terrain, NumDerivationMethod differentiator) {

        ArrayList<Double> k_i = new ArrayList<Double>();
        ArrayList<Double> interim = interim_vector(vector, timeStep, subStep, funcx, funcy, k_previous, terrain, differentiator);
        k_i.add(funcx.apply(interim));
        k_i.add(funcy.apply(interim));

//        System.out.println("k_i: " + k_i.toString());

        return k_i;
    }

    public ArrayList<Double> interim_vector(ArrayList<Double> vector, double timeStep, double subStep, IFunc<Double, Double> funcx, IFunc<Double, Double> funcy, ArrayList<Double> k_i, Expression terrain, NumDerivationMethod differentiator) {

        ArrayList<Double> interim = new ArrayList<Double>();

        double t = vector.get(0);
        double x = vector.get(1);
        double y = vector.get(2);
        double vx = vector.get(3);
        double vy = vector.get(4);

        interim.add(t + timeStep * subStep);
        interim.add(x + vx * timeStep * subStep);
        interim.add(y + vy * timeStep * subStep);
        interim.add(vx + k_i.get(0) * timeStep * subStep);
        interim.add(vy + k_i.get(1) * timeStep * subStep);

//        System.out.println("Interim vector before gradients: " + interim.toString());

        differentiator.gradients(interim, terrain, timeStep);

//        System.out.println("Interim vector after gradients: " + interim.toString());

        return interim;
    }
}

