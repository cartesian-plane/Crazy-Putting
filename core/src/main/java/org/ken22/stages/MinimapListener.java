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
import org.ken22.physics.utils.PhysicsUtils;

import java.util.List;

public class  MinimapListener extends InputListener {
    private Minimap minimap;
    private GolfCourse course;

    private TextField radiusField;
    private Label coordinatesLabel; // label that shows the coordinates when hovering over the map

    private boolean inMinimap = false;

    public MinimapListener(Minimap minimap, GolfCourse course, TextField radiusField, Label coordinatesLabel) {
        this.minimap = minimap;
        this.course = course;

        this.coordinatesLabel = coordinatesLabel;
        this.radiusField = radiusField;
        System.out.println("radiusField = " + radiusField);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

        switch (button) {
            // left click adds tree
            case Input.Buttons.LEFT -> {
                System.out.println("Clicked on minimap: " + x + ", " + y + "Color: " + minimap.minimap.getPixel((int) x, (int) y));

                System.out.print("World coord: ");
                var unprojectedX = minimap.unproject((int) x);
                var unprojectedY = minimap.unproject((int) y);
                System.out.print(unprojectedX + ",");
                System.out.print(unprojectedY);
                System.out.println();

                var radius = Double.parseDouble(radiusField.getText());
                Tree tree = new Tree(new double[]{unprojectedX, unprojectedY}, radius);

                course.trees.add(tree);
                minimap.update();
            }
            // right click removes tree
            case Input.Buttons.RIGHT -> {
                var unprojectedX = minimap.unproject((int) x);
                var unprojectedY = minimap.unproject((int) y);
                Tree treeToRemove = null;

                for (Tree tree : course.trees) {
                    double[] treePosition = tree.coordinates();

                    double distance = PhysicsUtils.magnitude(treePosition[0] - unprojectedX, treePosition[1] - unprojectedY);
                    if (distance < tree.radius()) {
                        treeToRemove = tree;
                        break;
                    }
                }
                if (treeToRemove != null) {
                    System.out.println("Removed tree");
                    course.trees.remove(treeToRemove);
                }
            }
            case Input.Buttons.MIDDLE -> {
                System.out.println("trees = " + course.trees);
            }
        }


        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        if (inMinimap) {
            var unprojectedX = minimap.unproject((int)x);
            var unprojectedY = minimap.unproject((int)y);
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
