package org.ken22.Physics.Numerical_Integration;

import org.ken22.Physics.System.PhysicsSystem;
import org.ken22.Physics.Vectors.GVec4;
import org.ken22.interfaces.ODESolution;

public class RK4 implements NumIntegrationMethod {

    public RK4(GVec4 state) {
        this.solve(state);
    }

    public GVec4 solve(GVec4 state) {
        // stops when velocity is very close to zero and acceleration is 0.
        return null;
    }



}

