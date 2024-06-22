package org.ken22.input.settings;

import org.ken22.physics.odesolvers.Euler;
import org.ken22.physics.odesolvers.ODESolver;
import org.ken22.physics.odesolvers.RK2;
import org.ken22.physics.odesolvers.RK4;

public enum ODESolverType {
    RK4("Runge-Kutta 4") {
        @Override
        public String toString() {
            return "Runge-Kutta 4";
        }
        public ODESolver getSolver() {
            return new RK4();
        }
    },

    RK2("Runge-Kutta 2") {
        @Override
        public String toString() {
            return "Runge-Kutta 2";
        }
        public ODESolver getSolver() {
            return new RK2();
        }
    },

    EULER("Euler") {
        @Override
        public String toString() {
            return "Euler";
        }
        public ODESolver getSolver() {
            return new Euler();
        }
    };


    private final String name;
    ODESolverType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
