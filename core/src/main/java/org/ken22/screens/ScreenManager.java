package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.ken22.Application;
import org.ken22.controller.ApplicationController;

/**
 * Screen adaptor class for managing active screens and stages.
 * Exists because the libGDX Game class does not support settign an active stage.
 * We work around this calling the render method of the active stage/screen in this class, and
 * setting this as the only active screen in Apllication extends Game.
 * Ideally each stage would be "event"-rendered, instead of rerendered every frame, but this is the most practical solution.
 */
public class ScreenManager extends ScreenAdapter {
    private Application app;

    private Stage currentStage;
    private Screen currentScreen;
    private boolean isStage = true; //whether current is a stage or a screen

    private KeyboardNavigator keyboardNavigator;

    public ScreenManager(Application app) {
        this.app = app;

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




    /// Transitions
    public void toGolfScreen() {
        if(this.currentScreen != null) this.currentScreen.dispose();
        this.currentScreen = new GolfScreen();
        this.isStage = false;
        //Gdx.input.setInputProcessor(this.currentScreen);
    }

    public void toMainStage() {
        this.currentStage.dispose();
        this.currentStage = new MainStage(this);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toSettingsStage() {
        this.currentStage.dispose();
        this.currentStage = new SettingsStage(this);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toCourseSelectorScreen() {
        this.currentStage.dispose();
        this.currentStage = new CourseSelectorScreen(this);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toTerrainEditorScreen() {
        this.currentStage.dispose();
        this.currentStage = new TerrainEditorScreen(this);
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
        this.currentStage = new BotSettingsScreen(this);
        Gdx.input.setInputProcessor(this.currentStage);
        this.isStage = true;
    }

    public void toOdeSolverScreen() {
        if (this.currentStage != null) this.currentStage.dispose();
        ApplicationController.main(new String[0]);
    }


    @Override
    public void resize(int width, int height) {
        currentStage.getViewport().update(width, height);
    }

    public void exit() {
        Gdx.app.exit();
    }
}
