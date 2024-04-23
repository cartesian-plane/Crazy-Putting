package org.ken22.interfaces;




//////////////////// CLASS //////////////////////////////////////////////

public class Level {
    //variables
    private String name;
    private String heightProfile;
    private double frictionCoefficient;
    private double startPositionX;
    private double startPositionY;
    private double targetLocationX;
    private double targetLocationY;
    private double targetRadius;


    //constructO!
    public Level(String name, String heightProfile, double frictionCoefficient, double startPositionX, 
                 double startPositionY, double targetLocationX, double targetLocationY, double targetRadius) {
        this.name = name;
        this.heightProfile = heightProfile;
        this.frictionCoefficient = frictionCoefficient;
        this.startPositionX = startPositionX;
        this.startPositionY = startPositionY;
        this.targetLocationX = targetLocationX;
        this.targetLocationY = targetLocationY;
        this.targetRadius = targetRadius;
    }



    // Getters
    public String getName() { return name; }
    public String getHeightProfile() { return heightProfile; }
    public double getFrictionCoefficient() { return frictionCoefficient; }
    public double getStartPositionX() { return startPositionX; }
    public double getStartPositionY() { return startPositionY; }
    public double getTargetLocationX() { return targetLocationX; }
    public double getTargetLocationY() { return targetLocationY; }
    public double getTargetRadius() { return targetRadius; }


    // Setters
    public void setName(String name) { this.name = name; }
    public void setHeightProfile(String heightProfile) { this.heightProfile = heightProfile; }
    public void setFrictionCoefficient(double frictionCoefficient) { this.frictionCoefficient = frictionCoefficient; }
    public void setStartPositionX(double startPositionX) { this.startPositionX = startPositionX; }
    public void setStartPositionY(double startPositionY) { this.startPositionY = startPositionY; }
    public void setTargetLocationX(double targetLocationX) { this.targetLocationX = targetLocationX; }
    public void setTargetLocationY(double targetLocationY) { this.targetLocationY = targetLocationY; }
    public void setTargetRadius(double targetRadius) { this.targetRadius = targetRadius; }


}
//////////////////// CLASS //////////////////////////////////////////////
