package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ken22.models.Minimap;
import org.ken22.screens.ScreenManager;
import org.ken22.utils.userinput.UIElementFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

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


        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.addActor(mainTable);


        minimap = new Minimap(manager.selectedCourse, viewport);
        minimap.image.setScale(1f);



        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        this.backButton = new TextButton("Back", skin);
        this.backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });



        this.resetButton = new TextButton("Reset", skin);
        this.resetButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.selectedCourse.trees.clear();
                manager.selectedCourse.sandPits.clear();
                manager.selectedCourse.walls.clear();
                minimap.update();
                System.out.println("\nAll obstacles removed\n");
            }
        });


        var radiusField = UIElementFactory.createTextField(String.valueOf(3), UIElementFactory.TextFieldType.NUMERICAL);
        var radiusLabel = new Label("Obstacle Radius:", skin);


        //tree
        var treeButton = new TextButton("Add Trees", skin);
        treeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (isValidRadius(radiusField.getText())) {
                    minimapListener.setAddingTree(true);
                    minimapListener.setAddingSandPit(false);
                    minimapListener.setAddingWall(false);
                } else {
                    showInvalidRadiusDialog();
                }
            }
        });


        //sand
        var sandPitButton = new TextButton("Add Sand Pits", skin);
        sandPitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (isValidRadius(radiusField.getText())) {
                    minimapListener.setAddingTree(false);
                    minimapListener.setAddingSandPit(true);
                    minimapListener.setAddingWall(false);
                } else {
                    showInvalidRadiusDialog();
                }
            }
        });


        //wall
        var wallButton = new TextButton("Add Walls", skin);
        wallButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (isValidRadius(radiusField.getText())) {
                    minimapListener.setAddingTree(false);
                    minimapListener.setAddingSandPit(false);
                    minimapListener.setAddingWall(true);
                } else {
                    showInvalidRadiusDialog();
                }
            }
        });

        var coordinatesLabel = new Label("X: 0, Y: 0", skin);




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
        controlTable.add(wallButton).colspan(2).pad(5).width(200).height(50);
        controlTable.row();
        controlTable.add(backButton).colspan(2).pad(5).width(200).height(50);
        controlTable.row();
        controlTable.add(resetButton).colspan(2).pad(5).width(200).height(50);

        // containerr
        Container<Image> minimapContainer = new Container<>(minimap.image);
        minimapContainer.setTransform(true);
        minimapContainer.size(512, 512); //size
        minimapContainer.setOrigin(minimapContainer.getWidth() / 2, minimapContainer.getHeight() / 2);


        // scroll
        ScrollPane scrollPane = new ScrollPane(controlTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // Legend
        Table legendTable = new Table();
        legendTable.add(new Label("Legend:", skin)).left().row();
        addLegendItem(legendTable, "Tree", Color.BROWN);
        addLegendItem(legendTable, "Sand Pit", Color.WHITE);
        addLegendItem(legendTable, "Wall", Color.PINK);
        addLegendItem(legendTable, "LEFT CLICK = ADD", Color.BLACK);
        addLegendItem(legendTable, "RIGHT CLICK = REMOVE LAST ADDED", Color.BLACK);

        // Layout
        Table rightTable = new Table();
        rightTable.add(minimapContainer).expand().center().padTop(10).padRight(50);
        rightTable.row();
        rightTable.add(legendTable).pad(10).top().center();

        mainTable.add(scrollPane).center().expandY().fillY().pad(10);
        mainTable.add(rightTable).expand().top().left().pad(10);

        var golfCourse = manager.selectedCourse;

        minimapListener = new MinimapListener(minimap, golfCourse, radiusField, coordinatesLabel);
        minimap.image.addListener(minimapListener);

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



    //  radius is valid
    private boolean isValidRadius(String radiusText) {
        try {
            double radius = Double.parseDouble(radiusText);
            return radius > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // dialog when the radius is invalid
    private void showInvalidRadiusDialog() {
        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        Dialog dialog = new Dialog("Invalid Radius", skin);
        dialog.text("The radius must be a positive number.");
        dialog.button("OK");
        dialog.show(this);
    }

    // add a legend item
    private void addLegendItem(Table legendTable, String name, Color color) {
        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        Label label = new Label(name, skin);
        Image colorBox = new Image(new Texture(createPixmap(color)));
        legendTable.add(colorBox).size(20).padRight(10);
        legendTable.add(label).left().row();
    }

    // pixmap of a specific color
    private Pixmap createPixmap(Color color) {
        Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return pixmap;
    }
}
