package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ken22.input.settings.*;
import org.ken22.input.odeinput.GridPathfindingType;
import org.ken22.screens.ScreenManager;
import org.ken22.utils.userinput.UIElementFactory;

import javax.swing.text.View;
import java.io.File;
import java.io.IOException;

import static org.ken22.utils.userinput.UIElementFactory.createTextField;

public class BotSettingsStage extends Stage {

    private ScreenManager manager;

    private static Viewport viewport = new ScreenViewport();
    private Table table;
    private ScrollPane scrollPane;
    private TextButton backButton;

    private SelectBox<LocalSearchType> localSearchSelector;
    private SelectBox<GridPathfindingType> graphAlgorithmSelector;
    private TextField randomRestarts;  // random restart count for the hill-climber

    private SelectBox<ODESolverType> odeSolverBox;
    private SelectBox<DifferentiatorType> differentiatorBox;
    private SelectBox<ErrorFunctionType> errorFunctionBox;
    private SelectBox<WeightingType> weightingBox;
    private TextField stepSizeField;

    // data holder for the settings
    private BotSettings settings;

    public BotSettingsStage(ScreenManager manager, BotSettings settings) {
        super(viewport);

        this.settings = settings;
        this.manager = manager;

        this.table = new Table();
        table.defaults().pad(10);
        this.table.setFillParent(true);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(true, false);

        this.addActor(scrollPane);


        table.add(new Label("Local search", skin));
        localSearchSelector = new SelectBox<>(skin);
        localSearchSelector.setItems(LocalSearchType.values());
        table.add(localSearchSelector);

        table.add(new Label("Graph algorithm", skin));
        graphAlgorithmSelector = new SelectBox<>(skin);
        graphAlgorithmSelector.setItems(GridPathfindingType.values());
        table.add(graphAlgorithmSelector);

        this.backButton = new TextButton("Back", skin);
        this.backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });

        table.row();

        table.add(new Label("Random restarts", skin));
        randomRestarts = createTextField("10", UIElementFactory.TextFieldType.NUMERICAL);
        table.add(randomRestarts);

        table.add(new Label("ODE Solver", skin));
        odeSolverBox = new SelectBox<>(skin);
        odeSolverBox.setItems(ODESolverType.values());


        table.add(new Label("Step Size", skin));
        stepSizeField = new TextField("", skin);

        table.add(new Label("Differentiator", skin));
        differentiatorBox = new SelectBox<>(skin);
        differentiatorBox.setItems(DifferentiatorType.values());

        table.add(new Label("Error Function", skin));
        errorFunctionBox = new SelectBox<>(skin);
        errorFunctionBox.setItems(ErrorFunctionType.values());

        this.table.defaults().pad(10);
        table.row();

        var saveButton = new TextButton("Save", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
            }
        });
        table.add(saveButton).pad(10).colspan(2).center().row();
        table.add(backButton).pad(10).colspan(2).center().row();

        loadButtons();

    }

    private void saveSettings() {
        settings.localSearchType = localSearchSelector.getSelected();
        settings.gridPathfindingType = graphAlgorithmSelector.getSelected();
        settings.differentiatorType = differentiatorBox.getSelected();
        settings.errorFunctionType = errorFunctionBox.getSelected();
        settings.weightingType = weightingBox.getSelected();
        settings.odesolverType = odeSolverBox.getSelected();
        settings.stepSize = Double.parseDouble(stepSizeField.getText());

        settings.randomRestarts = Integer.parseInt(randomRestarts.getText());

        this.manager.botSettings = this.settings;
        // save the new settings in the .json

        // Note: everything is written into the default bot settings, meaning there currently aren't multiple settings
        // presets to choose from (unlike the courses, for instance).

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(new File("input/settings/default-bot-settings.json"), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadButtons() {
            // make the UI reflect the loaded settings
            localSearchSelector.setSelected(settings.localSearchType);
            graphAlgorithmSelector.setSelected(settings.gridPathfindingType);
            randomRestarts.setText(String.valueOf(settings.randomRestarts));
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
