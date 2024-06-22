package org.ken22.input.settings;

import org.ken22.physics.differentiators.Differentiator;
import org.ken22.physics.differentiators.FivePointCenteredDifference;
import org.ken22.physics.differentiators.ThreePointCenteredDifference;

public enum DifferentiatorType {
    FIVE_POINT_CENTERED("Five-point centered") {
        @Override
        public String toString() {
            return "Five-point centered difference";
        }
        public Differentiator getDifferentiator() {
            return new FivePointCenteredDifference();
        }
    },

    THREE_POINT_CENTERED("Three-point centered") {
        @Override
        public String toString() {
            return "Three-point centered difference";
        }
        public Differentiator getDifferentiator() {
            return new ThreePointCenteredDifference();
        }
    };

    private final String name;
    DifferentiatorType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
