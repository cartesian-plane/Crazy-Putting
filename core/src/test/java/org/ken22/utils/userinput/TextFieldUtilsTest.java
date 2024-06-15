package org.ken22.utils.userinput;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextFieldUtilsTest {

    @Test
    @DisplayName("Valid coordinates test no whitespace")
    void validCoordinates() {
        var coordinates = "(1.5, 3.25)";
        double[] expected = {1.5, 3.25};
        var actual = TextFieldUtils.parseCoordinates(coordinates);

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
    }

    @Test
    @DisplayName("Valid coordinates test with whitespace")
    void validCoordinatesWithWhitespace() {
        var coordinates = " (1.5, 3.25) ";
        double[] expected = {1.5, 3.25};
        var actual = TextFieldUtils.parseCoordinates(coordinates);

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
    }

}
