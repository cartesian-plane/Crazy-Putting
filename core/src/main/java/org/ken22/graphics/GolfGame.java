package org.ken22.graphics;

import com.badlogic.gdx.Game;
import org.ken22.graphics.GolfScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GolfGame extends Game {
    @Override
    public void create() {
        setScreen(new GolfScreen());
    }
}
