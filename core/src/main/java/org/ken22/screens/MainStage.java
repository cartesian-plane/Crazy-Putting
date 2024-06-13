package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        terrainButton = new TextButton("Play", skin);
        terrainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {manager.toGolfScreen();}
        });
        table.add(terrainButton);

        table.row();

        terrainButton = new TextButton("Terrain Settings", skin);
        terrainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {manager.toTerrainStage();}
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
}
