package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import org.ken22.screens.ScreenManager;

public class ODEStage extends Stage {
    private ScreenManager manager;
    private Table table;
    private ScrollPane scrollPane;
    private TextButton backButton;

    public ODEStage(ScreenManager manager) {
        super(new ScreenViewport());
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));



        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(true, false);

        this.addActor(scrollPane);



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
