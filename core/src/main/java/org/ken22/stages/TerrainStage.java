package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ken22.models.Minimap;
import org.ken22.screens.ScreenManager;
import org.ken22.utils.userinput.UIElementFactory;

public class TerrainStage extends Stage {

    private ScreenManager manager;

    private static Viewport viewport = new ScreenViewport();

    private Table mainTable;
    private Table controlTable;
    private TextButton backButton;
    private TextButton resetButton;
    private MinimapListener minimapListener;

    private Minimap minimap;



    public TerrainStage(ScreenManager manager) {
        super(viewport);
        this.manager = manager;

        // main table
        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.addActor(mainTable);

        //minimap
        minimap = new Minimap(manager.selectedCourse);
        minimap.image.setScale(1f);


        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        //back
        this.backButton = new TextButton("Back", skin);
        this.backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });

        //reset
        this.resetButton = new TextButton("Reset", skin);
        this.resetButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.selectedCourse.trees.clear();
                manager.selectedCourse.sandPits.clear();
                minimap.update();
                System.out.println("All obstacles removed");
            }
        });

        //current radius
        var radiusField = UIElementFactory.createTextField(String.valueOf(3), UIElementFactory.TextFieldType.NUMERICAL);
        var radiusLabel = new Label("Obstacle Radius:", skin);


        //tree
        var treeButton = new TextButton("Add Trees", skin);
        treeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                minimapListener.setAddingTree(true);
                minimapListener.setAddingSandPit(false);
            }
        });

        //sand
        var sandPitButton = new TextButton("Add Sand Pits", skin);
        sandPitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                minimapListener.setAddingTree(false);
                minimapListener.setAddingSandPit(true);
            }
        });

        var coordinatesLabel = new Label("X: 0, Y: 0", skin);

        //controle tabble
        controlTable = new Table();
        controlTable.add(coordinatesLabel).colspan(2).center().pad(10);
        controlTable.row();
        controlTable.add(radiusLabel).pad(5);
        controlTable.add(radiusField).width(100).pad(5);
        controlTable.row();
        controlTable.add(treeButton).colspan(2).pad(5).width(200).height(50);
        controlTable.row();
        controlTable.add(sandPitButton).colspan(2).pad(5).width(200).height(50);
        controlTable.row();
        controlTable.add(backButton).colspan(2).pad(5).width(200).height(50);
        controlTable.row();
        controlTable.add(resetButton).colspan(2).pad(5).width(200).height(50);


        mainTable.add(minimap.image).expand().center().colspan(2).row();
        mainTable.add(controlTable).center().expandY().fillY().pad(10).colspan(2);

        var golfCourse = manager.selectedCourse;
        var trees = golfCourse.trees;

        minimapListener = new MinimapListener(minimap, golfCourse, radiusField, coordinatesLabel);
        minimap.image.addListener(minimapListener);

        //update what changed to the minimappe
        minimap.update();
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
