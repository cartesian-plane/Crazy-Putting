package org.ken22;

import com.badlogic.gdx.Game;
import org.ken22.screens.ScreenManager;

public class Application extends Game {

    private ScreenManager stageManager;

    @Override
    public void create() {
        this.stageManager = new ScreenManager(this);
        this.setScreen(stageManager);
    }
}
