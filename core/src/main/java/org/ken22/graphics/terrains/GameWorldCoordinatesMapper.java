package org.ken22.graphics.terrains;

import com.badlogic.gdx.math.Vector3;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ken22.input.courseinput.GolfCourse;

/**
 * This utility class contains the methods required to convert engine coordinates to game world coordinates.
 */
public final class GameWorldCoordinatesMapper {

    /**
     * <p>This method computes and returns the scaling factor for converting the engine height coordinate to
     * its game world counterpart (in order to be displayed on-screen).</p>
     * <p>The engine height coordinate is multiplied by this scaling factor before displaying it on-screen.</p>
     *
     * <p>The scaling factor can be determined by the following procedure: </p>
     * <ol>
     *     <li>choose a pair of (x, y) in the domain of the function; for example: (5,5)</li>
     *     <li>compute h(x,y) in engine coordinates (E<sub>h</sub>)</li>
     *     <li>get the game world height (G<sub>h</sub>) at (x, y) using the functionality provided by the
     *     HeightField class</li>
     *     <li>scaling factor =  (G<sub>h</sub>) / (E<sub>h</sub>)</li>
     * </ol>
     * <p>For simplicity, the corner of the map is always chosen for the computation.</p>
     * @param golfCourse the golf course
     * @param heightField the height field of the rendered terrain
     * @return the scaling factor
     */
    public static double getHeightScalingFactor(GolfCourse golfCourse, HeightField heightField) {
        Expression heightFunction = new ExpressionBuilder(golfCourse.courseProfile())
            .variables("x", "y")
            .build();

        double cornerX = -golfCourse.range();
        double cornerY = -golfCourse.range();

        heightFunction
            .setVariable("x", cornerX)
            .setVariable("y", cornerY);

        double engineHeight = heightFunction.evaluate();
        System.out.println("engineHeight = " + engineHeight);

        // in libgdx, the y is the height axis
        double gameWorldHeight = heightField.getPositionAt(new Vector3(), 0, 0).y;
        System.out.println("gameWorldHeight = " + gameWorldHeight);

        return gameWorldHeight / engineHeight;
    }
}
