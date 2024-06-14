package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.input.courseinput.Settings;

import java.util.Arrays;
import java.util.List;

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

    public MainStage(ScreenManager manager) {
        super(viewport);

        this.manager = manager;

        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.addActor(mainTable);

        this.buttonTable = new Table();
        this.infoTable = new Table();

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        playButton = createStyledButton("Play", skin, Color.GREEN, () -> manager.toGolfScreen());


        //premade levels
        courseSelectorButton = createStyledButton("Course Selector", skin, null, () -> {
            List<GolfCourse> courses = Arrays.asList(
                new GolfCourse("Mountain Peak", "sin(x) * cos(y)", 100, 1, 9.81, 0.3, 0.4, 0.5, 0.6, 30, 5, 50, 50, 10, 10),
                new GolfCourse("Desert Dunes", "tan(x) + tan(y)", 200, 1, 9.81, 0.3, 0.4, 0.5, 0.6, 30, 5, 50, 50, 20, 20),
                new GolfCourse("Forest Glade", "x^2 + y^2", 100, 1, 9.81, 0.3, 0.4, 0.5, 0.6, 30, 5, 60, 60, 15, 15),
                new GolfCourse("Ocean Breeze", "sin(x) + cos(y)", 200, 1, 9.81, 0.3, 0.4, 0.5, 0.6, 30, 5, 70, 70, 25, 25),
                new GolfCourse("Canyon Run", "x * y", 100, 1, 9.81, 0.3, 0.4, 0.5, 0.6, 30, 5, 80, 80, 12, 12),
                new GolfCourse("Sunset Valley", "exp(x) - y", 200, 1, 9.81, 0.3, 0.4, 0.5, 0.6, 30, 5, 90, 90, 22, 22)
            );
            manager.toCourseSelectorScreen(courses);
        });



        terrainEditorButton = createStyledButton("Terrain Editor", skin, null, () -> manager.toTerrainEditorScreen());
        courseEditorButton = createStyledButton("Course Editor", skin, null, () -> manager.toCourseEditorScreen());
        botSettingsButton = createStyledButton("Bot Settings", skin, null, () -> manager.toBotSettingsScreen());
        generalSettingsButton = createStyledButton("General Settings", skin, null, () -> manager.toSettingsStage());
        odeSolverButton = createStyledButton("ODE Solver", skin, null, () -> manager.toOdeSolverScreen());
        exitButton = createStyledButton("Exit", skin, Color.RED, () -> manager.exit());

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
        GolfCourse selectedCourse = Settings.getInstance().getSelectedCourse();
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
        infoTable.add(new Label("Differentiation: " + Settings.getInstance().getDifferentiation(), skin)).row();

        mainTable.add(buttonTable).expand().fill().left();
        mainTable.add(infoTable).expand().fill().right();
    }


    //button creation
    private TextButton createStyledButton(String text, Skin skin, Color color, Runnable action) {
        TextButton button = new TextButton(text, skin);
        if (color != null) {
            button.setColor(color);
        }
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
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
