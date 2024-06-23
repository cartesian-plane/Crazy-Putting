package org.ken22.players.weighting;

import org.ken22.players.pathfinding.Node;

public interface Weighting {
    public double calcWeight(Node node1, Node node2);
}
