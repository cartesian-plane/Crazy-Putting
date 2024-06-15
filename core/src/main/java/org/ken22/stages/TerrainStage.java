package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ken22.models.Minimap;
import org.ken22.screens.ScreenManager;
import org.ken22.utils.GolfExpression;


public class TerrainStage extends Stage {

    private ScreenManager manager;

    private static Viewport viewport = new ScreenViewport();
    private Table table;
    private ScrollPane scrollPane;
    private TextButton backButton;

    public TerrainStage(ScreenManager manager) {
        super(viewport);
        this.manager = manager;

        // Create a table that fills the screen
        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);

        // Create minimap
        Minimap minimap = new Minimap(GolfExpression.expr(manager.selectedCourse));
        Pixmap p = minimap.createPixmapFromHaightMap();
        Image image = new Image(new Texture(p));

        // Create a back button
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

        // Add actors to the table
        this.table.defaults().pad(10);
        this.table.add(image).colspan(2).row();
        this.table.add(backButton);
    }

    @Override
    public void dispose() {
        super.dispose();
        backButton.getSkin().dispose();
    }

    public Viewport getViewport() {
        return viewport;
    }
}
