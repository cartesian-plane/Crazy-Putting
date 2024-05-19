package org.ken22.physics.vectors;

import java.util.ArrayList;

public class GVec4 implements Vec {

    ArrayList<Double> vector = new ArrayList<Double>();
    double timeStep;

    public GVec4(double timeStep) {
        this.timeStep = this.timeStep;
    }

    public GVec4() {

    }
    public GVec4(String t0, String x0, String y0, String vx0, String vy0, String timeStep) {
        vector.add(Double.parseDouble(t0));
        vector.add(Double.parseDouble(x0));
        vector.add(Double.parseDouble(y0));
        vector.add(Double.parseDouble(vx0));
        vector.add(Double.parseDouble(vy0));
        this.timeStep = Double.parseDouble(timeStep);
    }

    public GVec4(double t0, double x0, double y0, double vx0, double vy0, double timeStep) {
        vector.add(t0);
        vector.add(x0);
        vector.add(y0);
        vector.add(vx0);
        vector.add(vy0);
        this.timeStep = timeStep;
    }

    public GVec4(ArrayList<Double> vector, double timeStep) {
        this.vector.add(vector.get(0));
        this.vector.add(vector.get(1));
        this.vector.add(vector.get(2));
        this.vector.add(vector.get(3));
        this.vector.add(vector.get(4));
        this.timeStep = timeStep;
    }


    public int size() {
        return this.vector.size();
    }

    public ArrayList<Double> getVector() {
        return this.vector;
    }

    public double get(int index) {
        return this.vector.get(index);
    }

    public double set(int index, double value) {
        return this.vector.set(index, value);
    }

    public void add(double value) {
        this.vector.add(value);
    }

    public double get_t() {
        return vector.get(0);
    }

    public double get_x() {
        return vector.get(1);
    }

    public double get_y() {
        return vector.get(2);
    }

    public double get_vx() {
        return vector.get(3);
    }

    public double get_vy() {
        return vector.get(4);
    }

    public double get_dhdx() {
        return vector.get(5);
    }

    public double get_dhdy() {
        return vector.get(6);
    }

    public double get_h() {
        return vector.get(7);
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void set_t(double t0) {
        this.vector.set(0, t0);
    }

    public void set_x(double x0) {
        this.vector.set(1, x0);
    }

    public void set_y(double y0) {
        this.vector.set(2, y0);
    }

    public void set_vx(double vx0) {
        this.vector.set(3, vx0);
    }

    public void set_vy(double vy0) {
        this.vector.set(4, vy0);
    }


    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public static GVec4 copy(GVec4 vector) {
        GVec4 copy = new GVec4();
        for(Double val : vector.getVector()) {
            copy.add(val);
        }
        return copy;
    }

    public void skim() {
        this.vector.removeLast();
    }

    public double getLast() {
        return this.vector.getLast();
    }

}
