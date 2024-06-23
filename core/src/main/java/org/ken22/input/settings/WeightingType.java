package org.ken22.input.settings;

import org.ken22.players.weighting.EquivalentWeighting;
import org.ken22.players.weighting.SlopeWeighing;
import org.ken22.players.weighting.Weighting;

public enum WeightingType {
    EQUIVALENT("Equivalent") {
        @Override
        public String toString() {
            return "Equivalent";
        }
        public Weighting getWeighting() {
            return new EquivalentWeighting();
        }
    },

    SLOPE("Slope") {
        @Override
        public String toString() {
            return "Slope";
        }
        public Weighting getWeighting() {
            return new SlopeWeighing();
        }
    };

    private String name;

    private WeightingType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
    public abstract Weighting getWeighting();
}
