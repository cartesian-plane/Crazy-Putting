package org.ken22;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;

public class Application extends Game {
    @Override
    public void create() {
        this.setScreen(new CubeScreen());
    }
}
