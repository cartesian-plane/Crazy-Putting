package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;



public class OdeSolverScreen extends Stage {
    private ScreenManager manager;

    private Table table;

    private TextButton backButton;

    public OdeSolverScreen(ScreenManager manager) {
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        this.backButton = new TextButton("Back", skin);

        this.backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });

        this.table.defaults().pad(10);
        this.table.add(backButton);
    }

    @Override
    public void dispose() {
        super.dispose();
        backButton.getSkin().dispose();
    }
}
