package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.screens.KeyboardNavigator;
import org.ken22.screens.ScreenManager;

import java.util.ArrayList;

import static org.ken22.stages.UIElementFactory.createNumericalTextField;

public class CourseEditorStage extends Stage {
    private ScreenManager manager;

    private GolfCourse course;

    private Table table;

    private TextButton mainButton;
    private TextButton saveButton;

    public CourseEditorStage(ScreenManager manager, GolfCourse course) {
        super();
        this.manager = manager;
        this.course = course;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);
        this.table.defaults().pad(10);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        TextField textField = new TextField("yellooow", skin);
        textField.setMaxLength(30);
        this.addActor(textField);

        this.mainButton = new TextButton("Back", skin);
        this.saveButton = new TextButton("Save", skin);

        // course parameter text fields
        var nameField = new TextField("Example Golf Course", skin);
        var nameLabel = new Label("Name:", skin);

        var functionField = new TextField("sin(x + y)", skin);
        functionField.setMaxLength(30);
        var functionLabel = new Label("Terrain function", skin);

        var rangeField = createNumericalTextField("0.5", skin);
        var rangeLabel = new Label("Range:", skin);

        var massField = createNumericalTextField("0.0459", skin);
        var massLabel = new Label("Mass:", skin);

        var gravitationalConstantField = createNumericalTextField("9.80665", skin);
        var gravitationalConstantLabel = new Label("Gravitational Constant:", skin);

        var kineticFrictionGrassField = createNumericalTextField("0.15", skin);
        var kineticFrictionGrassLabel = new Label("Kinetic Friction Grass:", skin);

        var staticFrictionGrassField = createNumericalTextField("0.3", skin);
        var staticFrictionGrassLabel = new Label("Static Friction Grass:", skin);

        var kineticFrictionSandField = createNumericalTextField("0.8", skin);
        var kineticFrictionSandLabel = new Label("Kinetic Friction Sand:", skin);

        var staticFrictionSandField = createNumericalTextField("0.9", skin);
        var staticFrictionSandLabel = new Label("Static Friction Sand:", skin);

        var maximumVelocityField = createNumericalTextField("5", skin);
        var maximumVelocityLabel = new Label("Maximum Velocity:", skin);

        var targetRadiusField = createNumericalTextField("0.10", skin);
        var targetRadiusLabel = new Label("Target Radius:", skin);

        var ballCoordField = createNumericalTextField("(2.0, 2.0)", skin);
        var ballCoordLabel = new Label("Ball Coordinates:", skin);

        var holeCoordField = createNumericalTextField("(2.0, 0.5)", skin);
        var holeCoordLabel = new Label("Hole Coordinates:", skin);

        this.mainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });




        this.saveButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                // still needs to save this somehow
                manager.setSelectedCourse(course);
                manager.toMainStage();
            }
        });

        table.defaults().pad(10).width(300).height(50);

        table.add(nameLabel).left();
        table.add(nameField).left();
        table.row();
        table.add(functionLabel).left();
        table.add(functionField).left();
        table.row();
        table.add(rangeLabel).left();
        table.add(rangeField).left();
        table.row();
        table.add(massLabel).left();
        table.add(massField).left();
        table.row();
        table.add(gravitationalConstantLabel).left();
        table.add(gravitationalConstantField).left();
        table.row();
        table.add(kineticFrictionGrassLabel).left();
        table.add(kineticFrictionGrassField).left();
        table.row();
        table.add(staticFrictionGrassLabel).left();
        table.add(staticFrictionGrassField).left();
        table.row();
        table.add(kineticFrictionSandLabel).left();
        table.add(kineticFrictionSandField).left();
        table.row();
        table.add(staticFrictionSandLabel).left();
        table.add(staticFrictionSandField).left();
        table.row();
        table.add(maximumVelocityLabel).left();
        table.add(maximumVelocityField).left();
        table.row();
        table.add(targetRadiusLabel).left();
        table.add(targetRadiusField).left();
        table.row();
        table.add(ballCoordLabel).left();
        table.add(ballCoordField).left();
        table.row();
        table.add(holeCoordLabel).left();
        table.add(holeCoordField).left();
        table.row();
        table.add(mainButton).padTop(20).left();
        table.add(saveButton).padTop(20).left();

        var textFields = new ArrayList<TextField>();
        for (Actor actor : table.getChildren()) {
            if (actor instanceof TextField) {
                textFields.add((TextField) actor);
            }
        }

        KeyboardNavigator keyboardNavigator = new KeyboardNavigator(this);
        Gdx.input.setInputProcessor(keyboardNavigator);
    }

    @Override
    public void dispose() {
        super.dispose();
        mainButton.getSkin().dispose();
    }
}
