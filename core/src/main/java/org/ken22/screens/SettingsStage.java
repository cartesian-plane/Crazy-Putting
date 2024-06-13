package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsStage extends Stage {
    private ScreenManager manager;

    private Table table;
    private TextButton backButton;
    private SelectBox<String> odeSolverBox;
    private TextField stepSizeField;
    private TextField differentiationField;
    private ButtonGroup<CheckBox> physicsGroup;
    private CheckBox simplifiedPhysics;
    private CheckBox completePhysics;
    private CheckBox allowPlayingCheckBox;

    public SettingsStage(ScreenManager manager) {
        super(new ScreenViewport());
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        // ODE Solver Selection
        Label odeSolverLabel = new Label("ODE Solver", skin);
        odeSolverBox = new SelectBox<>(skin);
        odeSolverBox.setItems("Euler", "Runge Kutta 2", "Runge Kutta 4");

        // Step Size Input
        Label stepSizeLabel = new Label("Step Size", skin);
        stepSizeField = new TextField("", skin);

        // Differentiation Input
        Label differentiationLabel = new Label("Differentiation", skin);
        differentiationField = new TextField("", skin);

        // Physics Selection
        Label physicsLabel = new Label("Physics", skin);
        simplifiedPhysics = new CheckBox("Simplified Physics", skin);
        completePhysics = new CheckBox("Complete Physics", skin);
        physicsGroup = new ButtonGroup<>(simplifiedPhysics, completePhysics);
        physicsGroup.setMaxCheckCount(1);
        physicsGroup.setMinCheckCount(1);

        // Allow Playing Checkbox
        allowPlayingCheckBox = new CheckBox("Allow Playing", skin);

        // Back Button
        this.backButton = new TextButton("Back", skin);
        this.backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });

        this.table.defaults().pad(10);

        // Adding widgets to the table
        table.add(odeSolverLabel).left();
        table.add(odeSolverBox).width(200).row();
        table.add(stepSizeLabel).left();
        table.add(stepSizeField).width(200).row();
        table.add(differentiationLabel).left();
        table.add(differentiationField).width(200).row();
        table.add(physicsLabel).left();
        table.add(simplifiedPhysics).left().row();
        table.add().left();
        table.add(completePhysics).left().row();
        table.add(allowPlayingCheckBox).colspan(2).left().row();
        table.add(backButton).colspan(2).center().padTop(20);

    }

    @Override
    public void dispose() {
        super.dispose();
        backButton.getSkin().dispose();
    }
}
