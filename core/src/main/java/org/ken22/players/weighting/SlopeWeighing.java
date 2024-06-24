package org.ken22.players.weighting;

import org.ken22.players.pathfinding.Node;

public class SlopeWeighing implements Weighting {
    @Override
    public double calcWeight(Node node1, Node node2) {
        if(node1.z == Double.MAX_VALUE || node2.z == Double.MAX_VALUE)
            return Double.MAX_VALUE;
        // since the distance between neighbors is the same, we can just return the difference in z values
        // always positive, so that it works with A*
        return Math.max(node2.z - node1.z, 0);
    }
}
