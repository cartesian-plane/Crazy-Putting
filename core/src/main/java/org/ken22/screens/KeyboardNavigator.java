package org.ken22.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.util.ArrayList;

public class KeyboardNavigator implements InputProcessor {
    private Stage stage;
    private ArrayList<TextField> textFields;
    private int currentFieldIndex;

    public KeyboardNavigator(Stage stage) {
        System.out.println("KeyboardNavigator constructor");
        this.textFields = textFields;
        this.currentFieldIndex = 0;
        this.stage = stage;
    }

    public void setTextFields(ArrayList<TextField> textFields) {
        this.textFields = textFields;
    }
    @Override
    public boolean keyDown(int keycode) {

        System.out.println("keycode = " + keycode);
        System.out.println("Aaaaaa");
        // Move to the previous text field
        boolean outOfBounds = (currentFieldIndex + 1) > textFields.size();
        currentFieldIndex = outOfBounds ? currentFieldIndex : currentFieldIndex + 1;
        stage.setKeyboardFocus(textFields.get(currentFieldIndex));

        return false;
    }



    // Other methods of the InputProcessor interface are not being used
    @Override
    public boolean keyUp(int keycode) {
        boolean outOfBounds = (currentFieldIndex - 1) > textFields.size();
        currentFieldIndex = outOfBounds ? currentFieldIndex : currentFieldIndex - 1;
        System.out.println("currentFieldIndex: " + currentFieldIndex);
        stage.setKeyboardFocus(textFields.get(currentFieldIndex));

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
