package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ken22.input.BotSettings;
import org.ken22.input.BotType;
import org.ken22.input.GeneralSettings;
import org.ken22.input.LocalSearchType;
import org.ken22.input.odeinput.GraphAlgorithmType;
import org.ken22.physics.odesolvers.ODESolver;
import org.ken22.screens.ScreenManager;
import org.ken22.utils.userinput.TextFieldType;
import org.ken22.utils.userinput.UIElementFactory;

import java.io.File;
import java.io.IOException;

import static org.ken22.utils.userinput.UIElementFactory.createTextField;

public class BotSettingsStage extends Stage {

    private ScreenManager manager;
    private Table table;
    private ScrollPane scrollPane;
    private TextButton backButton;

    private SelectBox<BotType> botSelector;
    private SelectBox<LocalSearchType> localSearchSelector;
    private SelectBox<GraphAlgorithmType> graphAlgorithmSelector;
    private TextField randomRestarts;  // random restart count for the hill-climber


    // data holder for the settings
    private BotSettings settings;

    public BotSettingsStage(ScreenManager manager) {
        super(new ScreenViewport());
        this.manager = manager;

        this.table = new Table();
        table.defaults().pad(10);
        this.table.setFillParent(true);

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));

        scrollPane = new ScrollPane(table, skin);
        scrollPane.setFillParent(true);
        scrollPane.setScrollingDisabled(true, false);

        this.addActor(scrollPane);

        table.add(new Label("Bot", skin));
        botSelector = new SelectBox<>(skin);
        botSelector.setItems(BotType.values());
        table.add(botSelector);

        table.add(new Label("Local search", skin));
        localSearchSelector = new SelectBox<>(skin);
        localSearchSelector.setItems(LocalSearchType.values());
        table.add(localSearchSelector);

        table.add(new Label("Graph algorithm", skin));
        graphAlgorithmSelector = new SelectBox<>(skin);
        graphAlgorithmSelector.setItems(GraphAlgorithmType.values());
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

        loadSettings();


        // listeners
        botSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (botSelector.getSelected().equals(BotType.PLANAR)) {
                    localSearchSelector.setSelected(LocalSearchType.NONE);
                    localSearchSelector.setDisabled(true);

                    graphAlgorithmSelector.setSelected(GraphAlgorithmType.NONE);
                    graphAlgorithmSelector.setDisabled(true);
                } else {
                    graphAlgorithmSelector.setDisabled(false);
                    localSearchSelector.setDisabled(false);
                }
            }
        });
    }

    private void saveSettings() {
        settings.botType = botSelector.getSelected();
        settings.localSearchType = localSearchSelector.getSelected();
        settings.graphAlgorithmType = graphAlgorithmSelector.getSelected();
        settings.randomRestarts = Integer.parseInt(randomRestarts.getText());

        // save the new settings in the .json

        // Note: everything is written into the default bot settings, meaning there currently aren't multiple settings
        // presets to choose from (unlike the courses, for instance).

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(new File("input/settings/default-settings.json"), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadSettings() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            settings = mapper.readValue(new File("input/settings/default-bot-settings.json"),
                BotSettings.class);

            // make the UI reflect the loaded settings
            botSelector.setSelected(settings.botType);
            localSearchSelector.setSelected(settings.localSearchType);
            graphAlgorithmSelector.setSelected(settings.graphAlgorithmType);
            randomRestarts.setText(String.valueOf(settings.randomRestarts));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        backButton.getSkin().dispose();
    }
}
