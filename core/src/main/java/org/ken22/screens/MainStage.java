package org.ken22.screens;

import com.badlogic.gdx.Gdx;
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

    private Table table; //manages layout

    private TextButton playButton;
    private TextButton terrainButton;
    private TextButton odesolverButton;
    private TextButton exitButton;
    private TextButton settingsButton;
    private TextButton instructionsButton;
    private TextButton courseSelectorButton;
    private TextButton courseEditorButton;
    private TextButton leaderboardButton;

    public MainStage(ScreenManager manager) {
        // if you don't do this viewport thing, the buttons won't look nice on high dpi displays
        super(ViewportType.SCREEN.getViewport());
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);
        this.table.defaults().pad(10);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        terrainButton = new TextButton("Play", skin);
        terrainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {manager.toGolfScreen();}
        });
        table.add(terrainButton);

        table.row();

        terrainButton = new TextButton("Settings", skin);
        terrainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {manager.toSettingsStage();}
        });
        table.add(terrainButton);

        table.row();

        exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {manager.exit();}
        });
        table.add(exitButton);
    }

    public void dispose() {
        super.dispose();
        terrainButton.getSkin().dispose();
    }

    private static Viewport makeViewport() {
        var viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(0.5f/Gdx.graphics.getDensity());

        return viewport;
    }
}
