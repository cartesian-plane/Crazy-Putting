package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CourseEditorStage extends Stage {
    private ScreenManager manager;

    private Table table;

    private TextButton mainButton;
    private TextButton courseSelector;
    private TextButton terrainEditor;
    private TextButton courseEditor;
    private TextButton generalSettings;

    public CourseEditorStage(ScreenManager manager) {
        // if you don't do this viewport thing, the buttons won't look nice on high dpi displays
        super(ViewportType.SCREEN.getViewport());
        this.manager = manager;

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);
        this.table.defaults().pad(10);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        TextField textField = new TextField("yellooow", skin);
        textField.setMaxLength(30);
        this.addActor(textField);

        this.mainButton = new TextButton("Main Menu", skin);

        // course parameter text fields & accompanying labels
        var functionField = new TextField("yellooow", skin);
        functionField.setMaxLength(30);
        var functionLabel = new Label("Terrain function", skin);

        this.courseSelector = new TextButton("Course Selector", skin);
        this.terrainEditor = new TextButton("Terrain Editor", skin);
        this.courseEditor = new TextButton("Course Editor", skin);
        this.generalSettings = new TextButton("General Settings", skin);

        this.mainButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });

        this.table.add(mainButton);
        table.row();
        this.table.add(functionLabel);
        this.table.add(functionField);
        this.table.add(courseSelector);
        table.row();
        this.table.add(terrainEditor);
        table.row();
        this.table.add(courseEditor);
        table.row();
        this.table.add(generalSettings);
    }

    @Override
    public void dispose() {
        super.dispose();
        mainButton.getSkin().dispose();
    }
}
