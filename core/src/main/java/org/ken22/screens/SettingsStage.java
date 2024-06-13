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



public class SettingsStage extends Stage {
    private ScreenManager manager;

    private Table table;

    private TextButton backButton;

    public SettingsStage(ScreenManager manager) {
        // if you don't do this viewport thing, the buttons won't look nice on high dpi displays
      //  super(ViewportType.SCREEN.getViewport());
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
