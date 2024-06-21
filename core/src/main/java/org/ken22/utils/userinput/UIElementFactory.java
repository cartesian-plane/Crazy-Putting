package org.ken22.utils.userinput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Utility class that contains methods to help maintain a consistent style across the app.
 */
public class UIElementFactory {

    public enum TextFieldType {
        NUMERICAL, ALPHANUMERIC
    }

    public static TextButton createStyledButton(String text, Skin skin, Color color, Runnable action) {
        TextButton button = new TextButton(text, skin);
        if (color != null) {
            button.setColor(color);
        }
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    public static TextButton createStyledButton(String text, Skin skin, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.setColor(null);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    public static TextField createTextField(String text, TextFieldType type) {
        var skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        var textField = new TextField(text, skin);

        switch (type) {
            case NUMERICAL -> {
                TextField.TextFieldFilter decimalFilter = new TextField.TextFieldFilter() {
                    @Override
                    public boolean acceptChar(TextField textField, char c) {
                        // Allow only digits and a single decimal point
                        if (Character.isDigit(c) || (c == '.' && !textField.getText().contains("."))) {
                            return true;
                        }
                        return false;
                    }
                };
                textField.setTextFieldFilter(decimalFilter);
            }


            case ALPHANUMERIC -> {
                TextField.TextFieldFilter alphanumericFilter = new TextField.TextFieldFilter() {
                    @Override
                    public boolean acceptChar(TextField textField, char c) {
                        return Character.isLetterOrDigit(c);
                    }
                };
                textField.setTextFieldFilter(alphanumericFilter);
            }
        }

        return textField;
    }

    public static TextField createNumericalTextField(String text) {
        var skin = new Skin(Gdx.files.internal("skins/test/uiskin.json"));
        return createNumericalTextField(text, skin);
    }

    public static TextField createNumericalTextField(String text, Skin skin) {
        var numericalField = new TextField(text, skin);
        numericalField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        return numericalField;
    }
}
