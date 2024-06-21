package org.ken22.players.pathfinding;

public class Node {
    public final int x;
    public final int y;
    public final double z;
    private double g;
    private double h;
    private double f;

    public Node(int x, int y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public double getF() {
        return g + h;
    }
}
