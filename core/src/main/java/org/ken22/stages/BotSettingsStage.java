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
import org.ken22.input.settings.GridPathfindingType;
import org.ken22.screens.ScreenManager;
import org.ken22.utils.userinput.UIElementFactory;

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

    private SelectBox<ErrorFunctionType> errorFunctionBox;
    private SelectBox<WeightingType> weightingBox;
    private TextField gridResolution;

    // Hill Climbing
    private TextField hcMaxIterations;
    private TextField hcErrorThreshold;
    private TextField hcConvergenceThreshold;
    private TextField hcStepSize;
    private TextField hcRandomRestarts;
    private TextField hcSidewaysMoves;

    // Newton-Raphson
    private TextField nrMaxIterations;
    private TextField nrTolerance;
    private TextField nrErrorThreshold;

    // Simulated Annealing
    private TextField saInitialTemperature;
    private TextField saCoolingRate;
    private TextField saDelta;
    private TextField saAllottedTime;

    // Gradient Descent
    private TextField gdDelta;
    private TextField gdThreshold;
    private TextField gdMaxSidewaysMoves;
    private TextField gdMaxRestarts;

    // data holder for the settings
    private BotSettings settings;

    public BotSettingsStage(ScreenManager manager, BotSettings settings) {
        super(viewport);

        this.settings = settings;
        this.manager = manager;

        this.table = new Table();
        table.defaults().pad(10);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setOverscroll(false, false);

        this.addActor(scrollPane);

        addSelectBoxOption("Local search:", localSearchSelector = new SelectBox<>(skin), LocalSearchType.values());
        addSelectBoxOption("Graph algorithm:", graphAlgorithmSelector = new SelectBox<>(skin), GridPathfindingType.values());
        addSelectBoxOption("Weighting:", weightingBox = new SelectBox<>(skin), WeightingType.values());
        addSelectBoxOption("Error Function:", errorFunctionBox = new SelectBox<>(skin), ErrorFunctionType.values());

        addTextFieldOption("Grid Resolution:", gridResolution = createTextField(String.valueOf(settings.gridResolution), UIElementFactory.TextFieldType.NUMERICAL));

        // Hill Climbing
        table.row();
        Label hcSettingsLabel = new Label("Hill Climbing Bot Settings", skin);
        table.add(hcSettingsLabel).colspan(2).center();

        addTextFieldOption("Max Iterations:", hcMaxIterations = createTextField(String.valueOf(settings.hcMaxIterations), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Error Threshold:", hcErrorThreshold = createTextField(String.valueOf(settings.hcErrorThreshold), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Convergence Threshold:", hcConvergenceThreshold = createTextField(String.valueOf(settings.hcConvergenceThreshold), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Step Size:", hcStepSize = createTextField(String.valueOf(settings.hcStepSize), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Random Restarts:", hcRandomRestarts = createTextField(String.valueOf(settings.randomRestarts), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Sideways Moves:", hcSidewaysMoves = createTextField(String.valueOf(settings.sidewaysMoves), UIElementFactory.TextFieldType.NUMERICAL));

        // Newton-Raphson
        table.row();
        Label nrSettingsLabel = new Label("Newton-Raphson Bot Settings", skin);
        table.add(nrSettingsLabel).colspan(2).center();

        addTextFieldOption("Max Iterations:", nrMaxIterations = createTextField(String.valueOf(settings.nrMaxIterations), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Tolerance:", nrTolerance = createTextField(String.valueOf(settings.nrTolerance), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Error Threshold:", nrErrorThreshold = createTextField(String.valueOf(settings.nrErrorThreshold), UIElementFactory.TextFieldType.NUMERICAL));

        // Simulated Annealing
        table.row();
        Label saSettingsLabel = new Label("Simulated Annealing Bot Settings", skin);
        table.add(saSettingsLabel).colspan(2).center();

        addTextFieldOption("Initial Temperature:", saInitialTemperature = createTextField(String.valueOf(settings.saInitialTemperature), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Cooling Rate:", saCoolingRate = createTextField(String.valueOf(settings.saCoolingRate), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Delta:", saDelta = createTextField(String.valueOf(settings.saDelta), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Allotted Time:", saAllottedTime = createTextField(String.valueOf(settings.saAllottedTime), UIElementFactory.TextFieldType.NUMERICAL));

        // Gradient Descent
        table.row();
        Label gdSettingsLabel = new Label("Gradient Descent Bot Settings", skin);
        table.add(gdSettingsLabel).colspan(2).center();

        addTextFieldOption("Delta:", gdDelta = createTextField(String.valueOf(settings.gdDelta), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Threshold:", gdThreshold = createTextField(String.valueOf(settings.gdThreshold), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Max Sideways Moves:", gdMaxSidewaysMoves = createTextField(String.valueOf(settings.gdMaxSidewaysMoves), UIElementFactory.TextFieldType.NUMERICAL));
        addTextFieldOption("Max Restarts:", gdMaxRestarts = createTextField(String.valueOf(settings.gdMaxRestarts), UIElementFactory.TextFieldType.NUMERICAL));

        // Save and Back
        var buttonTable = new Table();
        var saveButton = new TextButton("Save", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
            }
        });

        backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                manager.toMainStage();
            }
        });

        buttonTable.add(saveButton).pad(10).expandX().fillX();
        buttonTable.add(backButton).pad(10).expandX().fillX();

        table.row();
        table.add(buttonTable).colspan(2).center();

        loadButtons();
    }

    private <T> void addSelectBoxOption(String labelText, SelectBox<T> selectBox, T[] items) {
        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        table.row();
        table.add(new Label(labelText, skin)).left().padRight(10);
        selectBox.setItems(items);
        table.add(selectBox).expandX().fillX();
    }

    private void addTextFieldOption(String labelText, TextField textField) {
        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        table.row();
        table.add(new Label(labelText, skin)).left().padRight(10);
        table.add(textField).expandX().fillX();
    }

    private void saveSettings() {
        settings.localSearchType = localSearchSelector.getSelected();
        settings.gridPathfindingType = graphAlgorithmSelector.getSelected();
        settings.errorFunctionType = errorFunctionBox.getSelected();
        settings.weightingType = weightingBox.getSelected();
        settings.gridResolution = Double.parseDouble(gridResolution.getText());

        // saving all
        settings.hcMaxIterations = Integer.parseInt(hcMaxIterations.getText());
        settings.hcErrorThreshold = Double.parseDouble(hcErrorThreshold.getText());
        settings.hcConvergenceThreshold = Double.parseDouble(hcConvergenceThreshold.getText());
        settings.hcStepSize = Double.parseDouble(hcStepSize.getText());
        settings.randomRestarts = Integer.parseInt(hcRandomRestarts.getText());
        settings.sidewaysMoves = Integer.parseInt(hcSidewaysMoves.getText());

        settings.nrMaxIterations = Integer.parseInt(nrMaxIterations.getText());
        settings.nrTolerance = Double.parseDouble(nrTolerance.getText());
        settings.nrErrorThreshold = Double.parseDouble(nrErrorThreshold.getText());

        settings.saInitialTemperature = Double.parseDouble(saInitialTemperature.getText());
        settings.saCoolingRate = Double.parseDouble(saCoolingRate.getText());
        settings.saDelta = Double.parseDouble(saDelta.getText());
        settings.saAllottedTime = Integer.parseInt(saAllottedTime.getText());

        settings.gdDelta = Double.parseDouble(gdDelta.getText());
        settings.gdThreshold = Double.parseDouble(gdThreshold.getText());
        settings.gdMaxSidewaysMoves = Integer.parseInt(gdMaxSidewaysMoves.getText());
        settings.gdMaxRestarts = Integer.parseInt(gdMaxRestarts.getText());

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
        graphAlgorithmSelector.setSelected(settings.gridPathfindingType);
        weightingBox.setSelected(settings.weightingType);
        errorFunctionBox.setSelected(settings.errorFunctionType);
        localSearchSelector.setSelected(settings.localSearchType);
        gridResolution.setText(String.valueOf(settings.gridResolution));


        // loading
        hcMaxIterations.setText(String.valueOf(settings.hcMaxIterations));
        hcErrorThreshold.setText(String.valueOf(settings.hcErrorThreshold));
        hcConvergenceThreshold.setText(String.valueOf(settings.hcConvergenceThreshold));
        hcStepSize.setText(String.valueOf(settings.hcStepSize));
        hcRandomRestarts.setText(String.valueOf(settings.randomRestarts));
        hcSidewaysMoves.setText(String.valueOf(settings.sidewaysMoves));


        nrMaxIterations.setText(String.valueOf(settings.nrMaxIterations));
        nrTolerance.setText(String.valueOf(settings.nrTolerance));
        nrErrorThreshold.setText(String.valueOf(settings.nrErrorThreshold));


        saInitialTemperature.setText(String.valueOf(settings.saInitialTemperature));
        saCoolingRate.setText(String.valueOf(settings.saCoolingRate));
        saDelta.setText(String.valueOf(settings.saDelta));
        saAllottedTime.setText(String.valueOf(settings.saAllottedTime));

        gdDelta.setText(String.valueOf(settings.gdDelta));
        gdThreshold.setText(String.valueOf(settings.gdThreshold));
        gdMaxSidewaysMoves.setText(String.valueOf(settings.gdMaxSidewaysMoves));
        gdMaxRestarts.setText(String.valueOf(settings.gdMaxRestarts));
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
