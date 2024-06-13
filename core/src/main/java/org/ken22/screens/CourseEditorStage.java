package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

import static org.ken22.screens.UIElementCreator.createNumericalTextField;
public class CourseEditorStage extends Stage {
    private ScreenManager manager;

    private Table table;

    private TextButton mainButton;
    private TextButton courseSelector;
    private TextButton terrainEditor;
    private TextButton courseEditor;
    private TextButton generalSettings;

    public CourseEditorStage(ScreenManager manager) {
        // if you don't do this viewport thing, the buttons won't look nice on high dpi displays
        super(ViewportType.SCREEN.getViewport());
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);
        this.table.defaults().pad(10);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        TextField textField = new TextField("yellooow", skin);
        textField.setMaxLength(30);
        this.addActor(textField);

        this.mainButton = new TextButton("Main Menu", skin);

        // course parameter text fields & accompanying labels
        var nameField = new TextField("Example Golf Course", skin);
        var nameLabel = new Label("Name:", skin);

        var functionField = new TextField("sin(x + y)", skin);
        functionField.setMaxLength(30);
        var functionLabel = new Label("Terrain function", skin);

        //var rangeField = new TextField("0.5", skin);
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

        table.defaults().pad(10).width(300).height(50);

        this.table.add(mainButton);
        table.row();
        this.table.add(functionLabel);
        this.table.add(functionField);

        table.row();

        this.table.add(rangeLabel);
        this.table.add(rangeField);

        table.row();

        this.table.add(massLabel);
        this.table.add(massField);

        table.row();

        this.table.add(gravitationalConstantLabel);
        this.table.add(gravitationalConstantField);

        table.row();

        this.table.add(kineticFrictionGrassLabel);
        this.table.add(kineticFrictionGrassField);

        table.row();

        this.table.add(staticFrictionGrassLabel);
        this.table.add(staticFrictionGrassField);

        table.row();

        this.table.add(kineticFrictionSandLabel);
        this.table.add(kineticFrictionSandField);

        table.row();

        this.table.add(staticFrictionSandLabel);
        this.table.add(staticFrictionSandField);

        table.row();

        this.table.add(maximumVelocityLabel);
        this.table.add(maximumVelocityField);

        table.row();

        this.table.add(targetRadiusLabel);
        this.table.add(targetRadiusField);

        table.row();

        this.table.add(ballCoordLabel);
        this.table.add(ballCoordField);

        table.row();

        this.table.add(holeCoordLabel);
        this.table.add(holeCoordField);

        table.row();

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
