package org.ken22.input;

public enum LocalSearchType {
    NONE("None"),
    HILLCLIMB("Hill climb"),
    PARTICLE_SWARM("Particle Swarm Optimisation");

    private String name;

    private LocalSearchType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
