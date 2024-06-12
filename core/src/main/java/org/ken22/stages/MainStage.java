package org.ken22.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MainStage extends Stage {
    private StageManager manager;

    private TextButton startButton;
    private TextButton odesolverButton;
    private TextButton exitButton;
    private TextButton settingsButton;
    private TextButton instructionsButton;
    private TextButton courseSelectorButton;
    private TextButton courseEditorButton;
    private TextButton leaderboardButton;

    public MainStage(StageManager manager) {
        this.manager = manager;
    }


}
