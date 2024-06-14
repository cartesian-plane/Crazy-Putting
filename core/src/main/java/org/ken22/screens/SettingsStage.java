package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.ken22.input.courseinput.Settings;

public class SettingsStage extends Stage {
    private ScreenManager manager;

    private Table table;
    private SelectBox<String> odeSolverBox;
    private TextField stepSizeField;
    private TextField differentiationField;
    private CheckBox simplifiedPhysicsCheckBox;
    private CheckBox allowPlayingCheckBox;
    private Skin skin;

    public SettingsStage(ScreenManager manager) {
        super();

        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);

        skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));



        // ODE solver
        table.add(new Label("ODE Solver", skin)).pad(10);
        odeSolverBox = new SelectBox<>(skin);
        odeSolverBox.setItems("Euler", "Runge Kutta 2", "Runge Kutta 4");
        table.add(odeSolverBox).pad(10).row();

        // Step size
        table.add(new Label("Step Size", skin)).pad(10);
        stepSizeField = new TextField("", skin);
        table.add(stepSizeField).pad(10).row();

        // differentiation
        table.add(new Label("Differentiation", skin)).pad(10);
        differentiationField = new TextField("", skin);
        table.add(differentiationField).pad(10).row();

        // physics
        simplifiedPhysicsCheckBox = new CheckBox(" Simplified Physics", skin);
        table.add(simplifiedPhysicsCheckBox).pad(10).colspan(2).left().row();

        // playing
        allowPlayingCheckBox = new CheckBox(" Allow Playing", skin);
        table.add(allowPlayingCheckBox).pad(10).colspan(2).left().row();





        // save button
        TextButton saveButton = new TextButton("Save", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
            }
        });
        table.add(saveButton).pad(10).colspan(2).center().row();

        // back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });
        table.add(backButton).pad(10).colspan(2).center().row();

        loadSettings();
    }



    //save settings
    private void saveSettings() {
        Settings settings = Settings.getInstance();
        settings.setOdeSolver(odeSolverBox.getSelected());
        settings.setStepSize(Double.parseDouble(stepSizeField.getText()));
        settings.setDifferentiation(Double.parseDouble(differentiationField.getText()));
        settings.setSimplifiedPhysics(simplifiedPhysicsCheckBox.isChecked());
        settings.setAllowPlaying(allowPlayingCheckBox.isChecked());
    }


    //load settings
    private void loadSettings() {
        Settings settings = Settings.getInstance();
        odeSolverBox.setSelected(settings.getOdeSolver());
        stepSizeField.setText(Double.toString(settings.getStepSize()));
        differentiationField.setText(Double.toString(settings.getDifferentiation()));
        simplifiedPhysicsCheckBox.setChecked(settings.isSimplifiedPhysics());
        allowPlayingCheckBox.setChecked(settings.isAllowPlaying());
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
    }
}
