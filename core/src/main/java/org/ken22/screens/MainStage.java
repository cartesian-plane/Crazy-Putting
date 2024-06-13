package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainStage extends Stage {

    private ScreenManager manager;
    private Table table;

    private TextButton playButton;
    private TextButton courseSelectorButton;
    private TextButton terrainEditorButton;
    private TextButton courseEditorButton;
    private TextButton botSettingsButton;
    private TextButton generalSettingsButton;
    private TextButton odeSolverButton;
    private TextButton exitButton;

    public MainStage(ScreenManager manager) {
        // if you don't do this viewport thing, the buttons won't look nice on high dpi displays
        //super(ViewportType.SCREEN.getViewport());
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);
        this.table.defaults().pad(10);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        playButton = createStyledButton("Play", skin, Color.GREEN, manager::toGolfScreen);
        courseSelectorButton = createStyledButton("Course Selector", skin, null, manager::toCourseSelectorScreen);
        terrainEditorButton = createStyledButton("Terrain Editor", skin, null, manager::toTerrainEditorScreen);
        courseEditorButton = createStyledButton("Course Editor", skin, null, manager::toCourseEditorScreen);
        botSettingsButton = createStyledButton("Bot Settings", skin, null, manager::toBotSettingsScreen);
        generalSettingsButton = createStyledButton("General Settings", skin, null, manager::toSettingsStage);
        odeSolverButton = createStyledButton("ODE Solver", skin, null, manager::toOdeSolverScreen);
        exitButton = createStyledButton("Exit", skin, Color.RED, manager::exit);

        table.defaults().pad(10).width(300).height(50);

        table.add(playButton).padBottom(30).center();
        table.row();
        table.add(courseSelectorButton).center();
        table.row();
        table.add(terrainEditorButton).center();
        table.row();
        table.add(courseEditorButton).center();
        table.row();
        table.add(botSettingsButton).center();
        table.row();
        table.add(generalSettingsButton).center();
        table.row();
        table.add(odeSolverButton).center();
        table.row();
        table.add(exitButton).padTop(30).center();
    }


    //fancy buttons
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


    //color
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

    private static Viewport makeViewport() {
        var viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(0.5f/Gdx.graphics.getDensity());

        return viewport;
    }
}
