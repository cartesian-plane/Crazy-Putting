package org.ken22.players.pathfinding;

import java.util.Comparator;
import java.util.Objects;

public class Node {
    private Node connection;
    public final int x;
    public final int y;
    public final double z;
    private double g;
    private double h;
    public final boolean walkable;

    public Node(int x, int y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        if (z == Double.POSITIVE_INFINITY)
            this.walkable = false;
        else
            this.walkable = true;

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
    public String toString() {
        return "Node{" +
            "x=" + x +
            ", y=" + y +
            ", z=" + z +
            '}';
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

    public static Comparator<Node> getComparator() {
        return new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                if (node1.getF() < node2.getF() || (node1.getF() == node2.getF() && node1.getH() < node2.getH())) {
                    return -1;
                } else if (node1.getF() > node2.getF() || (node1.getF() == node2.getF() && node1.getH() > node2.getH())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

}
