package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ken22.Application;
import org.ken22.controller.ApplicationController;
import org.ken22.input.settings.BotSettings;
import org.ken22.input.settings.GeneralSettings;
import org.ken22.input.courseinput.CourseParser;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.physics.PhysicsFactory;
import org.ken22.players.bots.BotFactory;
import org.ken22.stages.*;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Screen adaptor class for managing active screens and stages.
 * Exists because the libGDX Game class does not support settign an active stage.
 * We work around this calling the render method of the active stage/screen in this class, and
 * setting this as the only active screen in Apllication extends Game.
 * Ideally each stage would be "event"-rendered, instead of rerendered every frame, but this is the most practical solution.
 */
public class ScreenManager extends ScreenAdapter {
    private Application app;

    // very much temporary
    private CourseParser parser = new CourseParser(new File("input/sin(x)sin(y).json")); //default course
    public GolfCourse selectedCourse = parser.getCourse();

    private Stage currentStage;
    private Screen currentScreen;
    private boolean isStage = true; //whether current is a stage or a screen


    public GeneralSettings generalSettings;
    public BotSettings botSettings;

    private PhysicsFactory physicsFactory;
    private BotFactory botFactory;

    private KeyboardNavigator keyboardNavigator;

    public ScreenManager(Application app) {
        this.app = app;
        loadSettings();
        this.physicsFactory = new PhysicsFactory(generalSettings);
        this.botFactory = new BotFactory(botSettings, physicsFactory);
        this.currentStage = new MainStage(this);
        keyboardNavigator = new KeyboardNavigator(this.currentStage);
        Gdx.input.setInputProcessor(keyboardNavigator);
        Gdx.input.setInputProcessor(this.currentStage);
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(this.isStage) {
            this.currentStage.act(deltaTime);
            this.currentStage.draw();
        } else {
            this.currentScreen.render(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        if(this.isStage) {
            this.currentStage.getViewport().update(width, height, true);
        }
    }

    /// transitions
    public void toGolfScreen() {
        if(this.currentScreen != null) this.currentScreen.dispose();
        this.currentScreen = new GolfScreen(selectedCourse, botFactory, physicsFactory);
        this.isStage = false;
        // Gdx.input.setInputProcessor(this.currentScreen);
    }

    public void toMainStage() {
        this.currentStage.dispose();
        this.currentStage = new MainStage(this);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toSettingsStage() {
        this.currentStage.dispose();
        this.currentStage = new SettingsStage(this, generalSettings);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toCourseSelectorScreen(List<GolfCourse> courses) {
        this.currentStage.dispose();
        this.currentStage = new CourseSelectorStage(this);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toTerrainEditorScreen() {
        this.currentStage.dispose();
        this.currentStage = new TerrainStage(this);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toCourseEditorScreen() {
        this.currentStage.dispose();
        this.currentStage = new CourseEditorStage(this);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toBotSettingsScreen() {
        this.currentStage.dispose();
        this.currentStage = new BotSettingsStage(this, botSettings);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toOdeSolverScreen() {
        if (this.currentStage != null) this.currentStage.dispose();
        ApplicationController.main(new String[0]);
    }

    public void setCourse(GolfCourse course) {
        this.selectedCourse = course;
    }

    private void loadSettings() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            generalSettings = mapper.readValue(new File("input/settings/default-settings.json"),
                GeneralSettings.class);
            botSettings = mapper.readValue(new File("input/settings/default-bot-settings.json"),
                    BotSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        Gdx.app.exit();
    }
}
