package org.ken22.physicsx.differentiation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FivePointCenteredDifferenceTest {

    @Test
    void differentiate() {
        assertEquals(3.0, new FivePointCenteredDifference().differentiate(0.001, 1.0, x -> x*x*x), 0.0000000001);
    }
}
