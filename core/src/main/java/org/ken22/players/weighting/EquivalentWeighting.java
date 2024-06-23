package org.ken22.players.weighting;

import org.ken22.players.pathfinding.Node;

public class EquivalentWeighting implements Weighting {
    @Override
    public double calcWeight(Node node1, Node node2) {
        if(node1.z == Double.MAX_VALUE || node2.z == Double.MAX_VALUE)
            return Double.MAX_VALUE;
        return 1;
    }
}
