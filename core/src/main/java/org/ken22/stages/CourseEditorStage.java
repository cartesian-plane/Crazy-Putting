package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.screens.KeyboardNavigator;
import org.ken22.screens.ScreenManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.ken22.stages.UIElementFactory.createNumericalTextField;
import static org.ken22.utils.userinput.TextFieldUtils.parseCoordinates;

public class CourseEditorStage extends Stage {
    private ScreenManager manager;


    private Table table;

    private TextButton mainButton;
    private GolfCourse selectedCourse;

    public CourseEditorStage(ScreenManager manager) {
        // if you don't do this viewport thing, the buttons won't look nice on high dpi displays
        super();
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);
        this.table.defaults().pad(10);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));


        selectedCourse = manager.selectedCourse;
        this.mainButton = new TextButton("Main Menu", skin);
        var submit = new TextButton("Submit", skin);
        submit.setColor(Color.GREEN);

        table.defaults().pad(10).width(300).height(50);
        table.add(mainButton);
        table.add(submit);
        // button set up and layout
        // course parameter text fields & accompanying labels
        var nameField = new TextField(selectedCourse.name(), skin);
        var nameLabel = new Label("Name:", skin);

        var functionField = new TextField(selectedCourse.courseProfile(), skin);
        functionField.setMaxLength(30);
        var functionLabel = new Label("Terrain function", skin);

        //var rangeField = new TextField("0.5", skin);
        var rangeField = createNumericalTextField(String.valueOf(selectedCourse.range()), skin);
        var rangeLabel = new Label("Range:", skin);

        var massField = createNumericalTextField("0.0459", skin);
        var massLabel = new Label("Mass:", skin);

        var gravitationalConstantField = createNumericalTextField(String.valueOf(selectedCourse.gravitationalConstant()), skin);
        var gravitationalConstantLabel = new Label("Gravitational Constant:", skin);

        var kineticFrictionGrassField = createNumericalTextField(String.valueOf(selectedCourse.kineticFrictionGrass()), skin);
        var kineticFrictionGrassLabel = new Label("Kinetic Friction Grass:", skin);

        var staticFrictionGrassField = createNumericalTextField(String.valueOf(selectedCourse.staticFrictionGrass()), skin);
        var staticFrictionGrassLabel = new Label("Static Friction Grass:", skin);

        var kineticFrictionSandField = createNumericalTextField(String.valueOf(selectedCourse.kineticFrictionSand()), skin);
        var kineticFrictionSandLabel = new Label("Kinetic Friction Sand:", skin);

        var staticFrictionSandField = createNumericalTextField(String.valueOf(selectedCourse.staticFrictionSand()), skin);
        var staticFrictionSandLabel = new Label("Static Friction Sand:", skin);

        var maximumVelocityField = createNumericalTextField(String.valueOf(selectedCourse.maximumSpeed()), skin);
        var maximumVelocityLabel = new Label("Maximum Velocity:", skin);

        var targetRadiusField = createNumericalTextField(String.valueOf(selectedCourse.targetRadius()), skin);
        var targetRadiusLabel = new Label("Target Radius:", skin);

        String ballCoords = "(" + selectedCourse.ballX() + ", " + selectedCourse.ballY() + ")";
        var ballCoordField = createNumericalTextField(ballCoords, skin);
        var ballCoordLabel = new Label("Ball Coordinates:", skin);

        String holeCoords = "(" + selectedCourse.targetXcoord() + ", " + selectedCourse.targetYcoord() + ")";
        var targetCoordField = createNumericalTextField(holeCoords, skin);
        var targetCoordLabel = new Label("Hole Coordinates:", skin);

        table.row();

        this.table.add(nameLabel);
        this.table.add(nameField);

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

        this.table.add(targetCoordLabel);
        this.table.add(targetCoordField);

        table.row();


        // button listeners
        this.mainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });
        submit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                selectedCourse = new GolfCourse(
                    nameField.getText(),
                    functionField.getText(),
                    Double.parseDouble(rangeField.getText()),
                    Double.parseDouble(massField.getText()),
                    Double.parseDouble(gravitationalConstantField.getText()),
                    Double.parseDouble(kineticFrictionGrassField.getText()),
                    Double.parseDouble(staticFrictionGrassField.getText()),
                    Double.parseDouble(kineticFrictionSandField.getText()),
                    Double.parseDouble(staticFrictionSandField.getText()),
                    Double.parseDouble(maximumVelocityField.getText()),
                    Double.parseDouble(targetRadiusField.getText()),
                    parseCoordinates(targetCoordField.getText())[0],
                    parseCoordinates(targetCoordField.getText())[1],
                    parseCoordinates(ballCoordField.getText())[0],
                    parseCoordinates(ballCoordField.getText())[1]
                );

                var mapper = new ObjectMapper();
                String jsonString;
                try {
                    jsonString = mapper.writeValueAsString(selectedCourse);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
//
//                FileHandle dirHandle = Gdx.files.internal("");
//                for (FileHandle entry: dirHandle.list()) {
//                    System.out.println(entry.name());
//                }



                String filePath = "input/" + selectedCourse.name().trim() + ".json";
                try {
                    Files.writeString(Paths.get(filePath), jsonString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


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
