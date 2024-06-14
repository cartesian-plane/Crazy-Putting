package org.ken22.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.ken22.input.courseinput.GolfCourse;
import org.ken22.input.courseinput.Settings;

import java.util.ArrayList;



public class CourseSelectorStage extends Stage {
    private ScreenManager manager;
    private ArrayList<GolfCourse> courses;
    private Table table;
    private ScrollPane scrollPane;



    public CourseSelectorStage(ScreenManager manager, ArrayList<GolfCourse> courses) {
        super(new ScreenViewport());
        this.manager = manager;
        this.courses = courses;

        this.table = new Table();
        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));


        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(true, false);

        this.addActor(scrollPane);

        for (GolfCourse course : courses) {
            Table coursePanel = createCoursePanel(course, skin);
            table.add(coursePanel).pad(10).row();
        }



        TextButton addButton = new TextButton("+", skin);
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showAddCourseDialog(skin);
            }
        });
        table.add(addButton).pad(10).row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });

        table.add(backButton).padTop(10).row();
    }




    //creating course panel
    private Table createCoursePanel(GolfCourse course, Skin skin) {
        Table coursePanel = new Table(skin);
        coursePanel.setBackground("default-round");
        coursePanel.defaults().pad(5);

        coursePanel.add(new Label("Course: " + course.name(), skin)).row();
        coursePanel.add(new Label("Height profile: " + course.courseProfile(), skin)).row();
        coursePanel.add(new Label("Start location: (" + course.ballX() + ", " + course.ballY() + ")", skin)).row();
        coursePanel.add(new Label("Target location: (" + course.targetXcoord() + ", " + course.targetYcoord() + ")", skin)).row();

        TextButton selectButton = new TextButton("Select", skin);
        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Settings.getInstance().setSelectedCourse(course);
                manager.toMainStage();
            }
        });
        coursePanel.add(selectButton).padTop(10);

        return coursePanel;
    }

    //adding the new level
    private void showAddCourseDialog(Skin skin) {
        Dialog dialog = new Dialog("Add Course", skin);
        Table contentTable = dialog.getContentTable();
        contentTable.add(new Label("Name: ", skin));
        TextField nameField = new TextField("", skin);
        contentTable.add(nameField).row();
        contentTable.add(new Label("Height Profile: ", skin));
        TextField profileField = new TextField("", skin);
        contentTable.add(profileField).row();
        contentTable.add(new Label("Start X: ", skin));
        TextField startXField = new TextField("", skin);
        contentTable.add(startXField).row();
        contentTable.add(new Label("Start Y: ", skin));
        TextField startYField = new TextField("", skin);
        contentTable.add(startYField).row();
        contentTable.add(new Label("Target X: ", skin));
        TextField targetXField = new TextField("", skin);
        contentTable.add(targetXField).row();
        contentTable.add(new Label("Target Y: ", skin));
        TextField targetYField = new TextField("", skin);
        contentTable.add(targetYField).row();

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });
        dialog.button(cancelButton);

        TextButton submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    String name = nameField.getText();
                    String profile = profileField.getText();
                    double startX = Double.parseDouble(startXField.getText());
                    double startY = Double.parseDouble(startYField.getText());
                    double targetX = Double.parseDouble(targetXField.getText());
                    double targetY = Double.parseDouble(targetYField.getText());

                    GolfCourse newCourse = new GolfCourse(name, profile, 100, 1, 9.81, 0.3, 0.4, 0.5, 0.6, 30, 5, targetX, targetY, startX, startY);
                    courses.add(newCourse);
                    table.row();
                    table.add(createCoursePanel(newCourse, skin)).pad(10).row();
                    dialog.hide();
                } catch (NumberFormatException e) {
                    // Handle invalid number input
                }
            }
        });
        dialog.button(submitButton);

        dialog.show(this);
    }



    @Override
    public void dispose() {
        super.dispose();
        scrollPane.remove();
    }
}
