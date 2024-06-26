package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.input.courseinput.Settings;
import org.ken22.models.Minimap;
import org.ken22.screens.ScreenManager;
import org.ken22.utils.userinput.UIElementFactory;

import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MainStage extends Stage {

    private ScreenManager manager;

    private static Viewport viewport = new ScreenViewport();

    private Table mainTable;
    private Table buttonTable;
    private Table infoTable;

    private TextButton playButton;
    private TextButton courseSelectorButton;
    private TextButton terrainEditorButton;
    private TextButton courseEditorButton;
    private TextButton botSettingsButton;
    private TextButton generalSettingsButton;
    private TextButton odeSolverButton;
    private TextButton exitButton;

    private Minimap minimap;

    public MainStage(ScreenManager manager) {
        super(viewport);

        this.manager = manager;

        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.addActor(mainTable);

        this.buttonTable = new Table();
        this.infoTable = new Table();

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        playButton = UIElementFactory.createStyledButton("Play", skin, Color.GREEN, () -> manager.toGolfScreen());

        courseSelectorButton = UIElementFactory.createStyledButton("Course Selector", skin, null, () -> {
            manager.toCourseSelectorScreen();
            updateMinimap();
        });

        terrainEditorButton = UIElementFactory.createStyledButton("Terrain Editor", skin, null, () -> {
            manager.toTerrainEditorScreen();
            updateMinimap();
        });
        courseEditorButton = UIElementFactory.createStyledButton("Course Editor", skin, null, () -> {
            manager.toCourseEditorScreen();
            updateMinimap();
        });
        botSettingsButton = UIElementFactory.createStyledButton("Bot Settings", skin, null, () -> {
            manager.toBotSettingsScreen();
            updateMinimap();
        });
        generalSettingsButton = UIElementFactory.createStyledButton("General Settings", skin, null, () -> {
            manager.toSettingsStage();
            updateMinimap();
        });
        odeSolverButton = UIElementFactory.createStyledButton("ODE Solver", skin, null, () -> {
            manager.toOdeSolverScreen();
            updateMinimap();
        });
        exitButton = UIElementFactory.createStyledButton("Exit", skin, Color.RED, () -> manager.exit());

        buttonTable.defaults().pad(10).width(300).height(50);

        buttonTable.add(playButton).padBottom(30).center();
        buttonTable.row();
        buttonTable.add(courseSelectorButton).center();
        buttonTable.row();
        buttonTable.add(terrainEditorButton).center();
        buttonTable.row();
        buttonTable.add(courseEditorButton).center();
        buttonTable.row();
        buttonTable.add(botSettingsButton).center();
        buttonTable.row();
        buttonTable.add(generalSettingsButton).center();
        buttonTable.row();
        buttonTable.add(odeSolverButton).center();
        buttonTable.row();
        buttonTable.add(exitButton).padTop(30).center();

        // info table about level
        GolfCourse selectedCourse = manager.selectedCourse;
        if (selectedCourse != null) {
            infoTable.add(new Label("Selected Course: " + selectedCourse.name(), skin)).row();
            infoTable.add(new Label("Height Profile: " + selectedCourse.courseProfile(), skin)).row();
            infoTable.add(new Label("Start Location: (" + selectedCourse.ballX() + ", " + selectedCourse.ballY() + ")", skin)).row();
            infoTable.add(new Label("Target Location: (" + selectedCourse.targetXcoord() + ", " + selectedCourse.targetYcoord() + ")", skin)).row();
        } else {
            infoTable.add(new Label("Selected Course: None", skin)).row();
        }

        infoTable.add(new Label("ODE Solver: " + Settings.getInstance().getOdeSolver(), skin)).row();
        infoTable.add(new Label("Step Size: " + Settings.getInstance().getStepSize(), skin)).row();
        infoTable.add(new Label("Differentiation: " + Settings.getInstance().getDifferentiator(), skin)).row();
        infoTable.add(new Label("Differentiation: " + Settings.getInstance().getDifferentiation(), skin)).row();



        // minimap
        minimap = new Minimap(manager.selectedCourse, viewport);
        Container<Image> minimapContainer = new Container<>(minimap.image);
        minimapContainer.setTransform(true);
        minimapContainer.size(500, 500); // set fixed size here
        minimapContainer.setOrigin(minimapContainer.getWidth() / 2, minimapContainer.getHeight() / 2);

        infoTable.add(minimapContainer).padTop(20).center().row();

        mainTable.add(buttonTable).expand().fill().left();
        mainTable.add(infoTable).expand().fill().right();

        updateMinimap(); // initial update
    }



    private void updateMinimap() {
        minimap.update();
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        playButton.getSkin().dispose();
    }

    public Viewport getViewport() {
        return viewport;
    }
}
