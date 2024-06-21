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



public class MinimapListener extends InputListener {
    private Minimap minimap;
    private GolfCourse course;
    private boolean addingTree = true;
    private boolean addingSandPit = false;

    private TextField radiusField;
    private Label coordinatesLabel; // label that shows the coordinates when hovering over the map

    private boolean inMinimap = false;


    public MinimapListener(Minimap minimap, GolfCourse course, TextField radiusField, Label coordinatesLabel) {
        this.minimap = minimap;
        this.course = course;

        this.coordinatesLabel = coordinatesLabel;
        this.radiusField = radiusField;
    }

    //adding
    public void setAddingTree(boolean addingTree) {
        this.addingTree = addingTree;
    }

    public void setAddingSandPit(boolean addingSandPit) {
        this.addingSandPit = addingSandPit;
    }

    //click button it do stuff
    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        double unprojectedX = minimap.unprojectX((int) x);
        double unprojectedY = minimap.unprojectY((int) y);

        switch (button) {


            // left click adds tree or sand pit
            case Input.Buttons.LEFT -> {
                System.out.println("Clicked on minimap: " + x + ", " + y);
                System.out.println("World coord: " + unprojectedX + ", " + unprojectedY);

                var radius = Double.parseDouble(radiusField.getText());
                if (addingTree) {
                    //TODO: negating unprojectedY might be a mistake
                    Tree tree = new Tree(new double[]{unprojectedX, -unprojectedY}, radius);
                    course.trees.add(tree);
                    System.out.println("Added tree");
                } else if (addingSandPit) {
                    SandPit sandPit = new SandPit(new double[]{unprojectedX, unprojectedY}, radius);
                    course.sandPits.add(sandPit);
                    System.out.println("Added sand pit");
                }
                minimap.update();
            }


            // right click removes the last added obstacle
            case Input.Buttons.RIGHT -> {
                if (addingTree && !course.trees.isEmpty()) {
                    Tree treeToRemove = course.trees.get(course.trees.size() - 1);
                    course.trees.remove(treeToRemove);
                    System.out.println("Removed tree: " + treeToRemove);
                } else if (addingSandPit && !course.sandPits.isEmpty()) {
                    SandPit sandPitToRemove = course.sandPits.get(course.sandPits.size() - 1);
                    course.sandPits.remove(sandPitToRemove);
                    System.out.println("Removed sand pit: " + sandPitToRemove);
                }
                minimap.update();
            }
        }

        return super.touchDown(event, x, y, pointer, button);
    }



    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        if (inMinimap) {
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
}
