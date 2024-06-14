package org.ken22.screens;

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

    public static TextField createNumericalTextField(String text, Skin skin) {
        var numericalField = new TextField(text, skin);
        numericalField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        return numericalField;
    }

}
