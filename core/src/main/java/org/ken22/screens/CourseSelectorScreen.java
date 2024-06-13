package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.ken22.input.courseinput.GolfCourse;

import java.util.ArrayList;
import java.util.List;

public class CourseSelectorScreen extends Stage {
    private ScreenManager manager;
    private Table table;
    private List<GolfCourse> courses;
    private Skin skin;
    private Table scrollTable;
    private ScrollPane scrollPane;
    private TextButton backButton;
    private TextButton addButton;

    // Declare TextFields as fields
    private TextField nameField;
    private TextField profileField;
    private TextField startXField;
    private TextField startYField;
    private TextField targetXField;
    private TextField targetYField;

    public CourseSelectorScreen(ScreenManager manager, List<GolfCourse> courses) {
        super(new ScreenViewport());
        this.manager = manager;
        this.courses = new ArrayList<>(courses); // Ensure the list is modifiable

        this.skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        this.table = new Table();
        this.table.setFillParent(true);
        this.addActor(table);

        scrollTable = new Table();
        for (GolfCourse course : this.courses) {
            Table courseTable = createCoursePanel(course);
            scrollTable.add(courseTable).pad(10).row();
        }

        scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);

        table.add(scrollPane).expand().fill().row();

        this.addButton = new TextButton("+", skin);
        this.addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openAddCourseDialog();
            }
        });
        table.add(addButton).pad(10).align(Align.center).row();

        this.backButton = new TextButton("Back", skin);
        this.backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });
        table.add(backButton).pad(10).align(Align.bottom);
    }

    private Table createCoursePanel(GolfCourse course) {
        Table courseTable = new Table();
        courseTable.setBackground(skin.newDrawable("default-round", Color.DARK_GRAY));
        courseTable.pad(10);
        courseTable.defaults().pad(5);

        Label nameLabel = new Label("Course: " + course.name(), skin);
        Label profileLabel = new Label("Height profile: " + course.courseProfile(), skin);
        Label startLocationLabel = new Label("Start location: (" + course.ballX() + ", " + course.ballY() + ")", skin);
        Label targetLocationLabel = new Label("Target location: (" + course.targetXcoord() + ", " + course.targetYcoord() + ")", skin);

        TextButton selectButton = new TextButton("Select", skin);
        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Handle level selection
                // manager.setCurrentCourse(course); // Example
            }
        });

        courseTable.add(nameLabel).align(Align.left).row();
        courseTable.add(profileLabel).align(Align.left).row();
        courseTable.add(startLocationLabel).align(Align.left).row();
        courseTable.add(targetLocationLabel).align(Align.left).row();
        courseTable.add(selectButton).align(Align.center).padTop(10);

        return courseTable;
    }

    private void openAddCourseDialog() {
        Dialog dialog = new Dialog("Add New Course", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    String name = nameField.getText();
                    String profile = profileField.getText();
                    double startX = Double.parseDouble(startXField.getText());
                    double startY = Double.parseDouble(startYField.getText());
                    double targetX = Double.parseDouble(targetXField.getText());
                    double targetY = Double.parseDouble(targetYField.getText());

                    GolfCourse newCourse = new GolfCourse(name, profile, 100, 1, 9.81, 0.3, 0.4, 0.5, 0.6, 30, 5, targetX, targetY, startX, startY);
                    courses.add(newCourse);
                    scrollTable.add(createCoursePanel(newCourse)).pad(10).row();
                    scrollTable.layout();
                }
            }
        };

        nameField = new TextField("", skin);
        profileField = new TextField("", skin);
        startXField = new TextField("", skin);
        startYField = new TextField("", skin);
        targetXField = new TextField("", skin);
        targetYField = new TextField("", skin);

        dialog.getContentTable().add("Name:").pad(5);
        dialog.getContentTable().add(nameField).width(200).pad(5).row();
        dialog.getContentTable().add("Height profile:").pad(5);
        dialog.getContentTable().add(profileField).width(200).pad(5).row();
        dialog.getContentTable().add("Start X:").pad(5);
        dialog.getContentTable().add(startXField).width(200).pad(5).row();
        dialog.getContentTable().add("Start Y:").pad(5);
        dialog.getContentTable().add(startYField).width(200).pad(5).row();
        dialog.getContentTable().add("Target X:").pad(5);
        dialog.getContentTable().add(targetXField).width(200).pad(5).row();
        dialog.getContentTable().add("Target Y:").pad(5);
        dialog.getContentTable().add(targetYField).width(200).pad(5).row();

        dialog.button("Add", true);
        dialog.button("Cancel", false);
        dialog.key(com.badlogic.gdx.Input.Keys.ENTER, true);
        dialog.key(com.badlogic.gdx.Input.Keys.ESCAPE, false);
        dialog.show(this);
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        backButton.getSkin().dispose();
    }
}
