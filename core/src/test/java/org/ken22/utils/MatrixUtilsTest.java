package org.ken22.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.ken22.utils.MatrixUtils.inverse;
import static org.ken22.utils.MatrixUtils.transform;

class MatrixUtilsTest {

    @Test
    @DisplayName("Multiplication")
    void transformTest() {
        double[][] a = new double[][]{
            {5, 3},
            {4, 2}
        };

        double[] b = new double[] {4, 1};

        var actual = transform(a, b);
        var expected = new double[][]{
            {23},
            {18}
        };

        assertEquals(expected[0][0], actual[0][0]);
        assertEquals(expected[1][0], actual[1][0]);
    }

    @Test
    void inverseTest() {
        double[][] a = new double[][]{
            {5, 3},
            {4, 2}
        };

        var actual = inverse(a);
        assertEquals(-1, actual[0][0]);
        assertEquals(1.5, actual[0][1]);
        assertEquals(2, actual[1][0]);
        assertEquals(-2.5, actual[1][1]);
    }
}
