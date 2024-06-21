package org.ken22.players.pathfinding;

import java.util.Objects;

public class Node {
    private Node connection;
    public final int x;
    public final int y;
    public final double z;
    private double g;
    private double h;

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

    public Node getConnection() {
        return connection;
    }

    public void setG(double g) {
        this.g = g;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setConnection(Node connection) {
        this.connection = connection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
