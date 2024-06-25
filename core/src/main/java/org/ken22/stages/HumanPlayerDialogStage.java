package org.ken22.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ken22.physics.vectors.StateVector4;
import org.ken22.screens.GolfScreen;
import org.ken22.utils.userinput.UIElementFactory;

public class HumanPlayerDialogStage extends Stage  {

    private static Viewport viewport = new ScreenViewport();
    private GolfScreen gameScreen;

    private TextField vxTextField;
    private TextField vyTextField;

    public HumanPlayerDialogStage(GolfScreen gameScreen) {
        super(viewport);
        this.gameScreen = gameScreen;

        Skin skin = new Skin(Gdx.files.internal("skins/test/uiskin.json")); // Load the skin file

        // Create the text fields
        vxTextField = UIElementFactory.createTextField("0.00", UIElementFactory.TextFieldType.NUMERICAL);
        vyTextField = UIElementFactory.createTextField("0.00", UIElementFactory.TextFieldType.NUMERICAL);

        // Create the buttons
        TextButton acceptButton = new TextButton("Accept", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        acceptButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                gameScreen.humanDialogAccepted();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                gameScreen.humanDialogRejected();
            }
        });

        // Create a table to layout the widgets
        Table table = new Table();
        table.setFillParent(true); // Make the table fill the whole stage
        table.add(vxTextField).width(200).pad(10);
        table.row();
        table.add(vyTextField).width(200).pad(10);
        table.row();
        table.add(acceptButton).width(200).pad(10);
        table.row();
        table.add(cancelButton).width(200).pad(10);

        // Add the table to the stage
        addActor(table);
    }

    public StateVector4 getShot(StateVector4 state) {
        return new StateVector4(
            state.x(),
            state.y(),
            Double.parseDouble(vxTextField.getText()),
            Double.parseDouble(vyTextField.getText())
        );
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }

    public Viewport getViewport() {
        return viewport;
    }
}
