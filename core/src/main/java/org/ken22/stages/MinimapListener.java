package org.ken22.stages;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.models.Minimap;
import org.ken22.obstacles.Tree;
import org.ken22.obstacles.SandPit;
import org.ken22.physics.utils.PhysicsUtils;
import java.util.List;
import org.ken22.obstacles.Wall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;




public class MinimapListener extends InputListener {
    private Minimap minimap;
    private GolfCourse course;
    private boolean addingTree = true;
    private boolean addingSandPit = false;
    private boolean addingWall = false;
    private double[] wallStartPoint = null;

    private TextField radiusField;
    private TextField thicknessField;
    private Label coordinatesLabel;

    private boolean inMinimap = false;



    public MinimapListener(Minimap minimap, GolfCourse course, TextField radiusField, TextField thicknessField, Label coordinatesLabel) {
        this.minimap = minimap;
        this.course = course;
        this.radiusField = radiusField;
        this.thicknessField = thicknessField;
        this.coordinatesLabel = coordinatesLabel;
    }



    public void setAddingTree(boolean addingTree) {
        this.addingTree = addingTree;
    }

    public void setAddingSandPit(boolean addingSandPit) {
        this.addingSandPit = addingSandPit;
    }

    public void setAddingWall(boolean addingWall) {
        this.addingWall = addingWall;
    }



    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (!isValidRadius(radiusField.getText())) {
            showInvalidRadiusDialog();
            return false;
        }

        // this was the problem
        y = minimap.image.getHeight() - y;

        double unprojectedX = minimap.unprojectX((int) x);
        double unprojectedY = minimap.unprojectY((int) y);
        System.out.println("Clicked on minimap: " + x + ", " + y);
        System.out.println("World coord: " + unprojectedX + ", " + unprojectedY);


        //mouse adding removing
        switch (button) {
            case Input.Buttons.LEFT -> {
                var radius = Double.parseDouble(radiusField.getText());
                if (addingTree) {
                    //TODO: negating unprojectedY might be a mistake
                    Tree tree = new Tree(new double[]{unprojectedX, -unprojectedY}, radius);
                    course.trees.add(tree);
                    System.out.println("Added tree at: " + unprojectedX + ", " + unprojectedY);
                    System.out.println();
                } else if (addingSandPit) {
                    SandPit sandPit = new SandPit(new double[]{unprojectedX, unprojectedY}, radius);
                    course.sandPits.add(sandPit);
                    System.out.println("Added sand pit at: " + unprojectedX + ", " + unprojectedY);
                    System.out.println();
                } else if (addingWall) {
                    if (wallStartPoint == null) {
                        wallStartPoint = new double[]{unprojectedX, unprojectedY};
                        System.out.println("Started wall at: " + unprojectedX + ", " + unprojectedY);
                    } else {
                        if (!isValidThickness(thicknessField.getText())) {
                            showInvalidThicknessDialog();
                            return false;
                        }
                        var thickness = Double.parseDouble(thicknessField.getText());
                        Wall wall = new Wall(wallStartPoint, new double[]{unprojectedX, unprojectedY}, thickness);
                        course.walls.add(wall);
                        wallStartPoint = null;
                        System.out.println("Added wall from " + wall.startPoint()[0] + ", " + wall.startPoint()[1] + " to " + wall.endPoint()[0] + ", " + wall.endPoint()[1]);
                        System.out.println();
                    }
                }
                minimap.update();
            }
            case Input.Buttons.RIGHT -> {
                if (addingTree && !course.trees.isEmpty()) {
                    course.trees.remove(course.trees.size() - 1);
                    System.out.println("Removed last tree");
                } else if (addingSandPit && !course.sandPits.isEmpty()) {
                    course.sandPits.remove(course.sandPits.size() - 1);
                    System.out.println("Removed last sand pit");
                } else if (addingWall && !course.walls.isEmpty()) {
                    course.walls.remove(course.walls.size() - 1);
                    System.out.println("Removed last wall");
                }
                minimap.update();
            }
        }

        return super.touchDown(event, x, y, pointer, button);
    }



    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        if (inMinimap) {
            // also important
            y = minimap.image.getHeight() - y;
            var unprojectedX = minimap.unprojectX((int) x);
            var unprojectedY = minimap.unprojectY((int) y);
            coordinatesLabel.setText("X: " + unprojectedX + " Y: " + unprojectedY);
        }
        return super.mouseMoved(event, x, y);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        inMinimap = true;
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        inMinimap = false;
    }





    private boolean isValidRadius(String radiusText) {
        if (radiusText == null || radiusText.isEmpty()) {
            return false;
        }
        try {
            double radius = Double.parseDouble(radiusText);
            return radius > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    private boolean isValidThickness(String thicknessText) {
        if (thicknessText == null || thicknessText.isEmpty()) {
            return false;
        }
        try {
            double thickness = Double.parseDouble(thicknessText);
            return thickness > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    private void showInvalidRadiusDialog() {
        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        Dialog dialog = new Dialog("Invalid radius", skin);
        dialog.text("The radius must be a positive number.");
        dialog.button("OK");
        dialog.show(minimap.image.getStage());
    }



    private void showInvalidThicknessDialog() {
        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        Dialog dialog = new Dialog("Invalid thickness", skin);
        dialog.text("The thickness must be a positive number");
        dialog.button("OK");
        dialog.show(minimap.image.getStage());
    }
}
