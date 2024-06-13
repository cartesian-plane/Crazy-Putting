package org.ken22.input.courseinput;

public class Settings {
    private static Settings instance;

    private String odeSolver;
    private double stepSize;
    private double differentiation;
    private boolean simplifiedPhysics;
    private boolean allowPlaying;
    private GolfCourse selectedCourse;

    private Settings() {
        // default settings
        odeSolver = "Runge Kutta 4";
        stepSize = 0.01;
        differentiation = 0.01;
        simplifiedPhysics = true;
        allowPlaying = true;
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getOdeSolver() {
        return odeSolver;
    }

    public void setOdeSolver(String odeSolver) {
        this.odeSolver = odeSolver;
    }

    public double getStepSize() {
        return stepSize;
    }

    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }

    public double getDifferentiation() {
        return differentiation;
    }

    public void setDifferentiation(double differentiation) {
        this.differentiation = differentiation;
    }

    public boolean isSimplifiedPhysics() {
        return simplifiedPhysics;
    }

    public void setSimplifiedPhysics(boolean simplifiedPhysics) {
        this.simplifiedPhysics = simplifiedPhysics;
    }

    public boolean isAllowPlaying() {
        return allowPlaying;
    }

    public void setAllowPlaying(boolean allowPlaying) {
        this.allowPlaying = allowPlaying;
    }

    public GolfCourse getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(GolfCourse selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public String getPhysicsType() {
        return simplifiedPhysics ? "Simplified Physics" : "Complete Physics";
    }
}
