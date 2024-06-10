package org.ken22.controller;

//////////////////// IMPORTS //////////////////////////////////////////////
import org.ken22.ui.InputPage2;
import org.ken22.ui.LevelSelectPage;
import org.ken22.ui.Level;
import java.util.ArrayList;
import java.util.List;
//////////////////// IMPORTS //////////////////////////////////////////////


//////////////////// CLASS //////////////////////////////////////////////
public class ApplicationController2 {
    private List<Level> sampleLevels;



    //main
    public static void main(String[] args) {
        new ApplicationController2().run();
    }





    //////////////////// METHODS //////////////////////////////////////////////
    //get the initial levels
    public ApplicationController2() {
        this.sampleLevels = createSampleLevels();
    }




    //running the stuff
    private void run() {
        new InputPage2(this);
    }






    //some intial levels change as you please
    private List<Level> createSampleLevels() {
        List<Level> sampleLevels = new ArrayList<>();
        sampleLevels.add(new Level("Albatross Alps", "sin(x) * cos(y)", 0.6, 0, 0, 15, 15, 1.5));
        sampleLevels.add(new Level("Bogey Beach", "tan(x) + tan(y)", 0.7, 5, 5, 12, 12, 1.8));
        sampleLevels.add(new Level("Eagle's Nest", "x^2 + y^2", 0.5, 10, 10, 18, 18, 2.0));
        sampleLevels.add(new Level("Putt Pirates", "x / y + y / x", 0.55, 2, 2, 16, 16, 2.2));
        sampleLevels.add(new Level("Paradise Park", "x * log(y)", 0.6, 4, 4, 10, 10, 1.3));
        sampleLevels.add(new Level("Sandy Swing", "abs(x - y)", 0.75, 6, 6, 11, 11, 1.9));
        sampleLevels.add(new Level("Golfer's Lagoon", "sin(x * y)", 0.58, 1, 1, 17, 17, 2.3));

        return sampleLevels;
    }





    //When the user selects a level
    public void onLevelSelect(InputPage2 inputPage) {
        LevelSelectPage levelSelectPage = new LevelSelectPage(this.sampleLevels, inputPage::updateSelectedLevel);
        levelSelectPage.setLevelEditListener(editedLevel -> {
            int levelIndex = -1;
            for (int i = 0; i < sampleLevels.size(); i++) {
                if (sampleLevels.get(i).getName().equals(editedLevel.getName())) {
                    levelIndex = i;
                    break;
                }
            }

            if (levelIndex != -1) {
                sampleLevels.set(levelIndex, editedLevel);
            }
        });
        levelSelectPage.setVisible(true);
    }





    //if you wanna see phase 1 stuff then this runs the original controller
    public void onPhaseOne() {
        new ApplicationController().run();
    }
    //////////////////// METHODS //////////////////////////////////////////////

}
//////////////////// CLASS //////////////////////////////////////////////
