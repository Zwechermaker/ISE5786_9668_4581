package lighting.impl;

import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import lighting.impl.PointLight;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PointLight} class.
 */
class PointLightTest {

    /**
     * Default constructor for Javadoc purposes.
     */
    public PointLightTest() {}

    // ================== Test Constants ==================
    /** Original intensity of the point light. */
    private static final Color I0 = new Color(1000, 1000, 1000);
    /** Position of the point light. */
    private static final Point POSITION = new Point(0, 0, 0);
    /** A point light instance for tests, configured with attenuation factors. */
    private static final PointLight LIGHT = new PointLight(I0, POSITION)
            .setKl(0.1).setKq(0.01);
    /** A point at a standard distance. */
    private static final Point P1 = new Point(0, 10, 0);

    /**
     * Test method for {@link PointLight#getIntensity(primitives.Point)}.
     */
    @Test
    void testGetIntensity() {
        // ================== Equivalence Partitions Tests ==================
        // TC01: Point at a normal distance
        // Distance is 10. Attenuation denominator: 1 + 0.1*10 + 0.01*100 = 3
        Color expectedIntensity = I0.scale(1.0 / 3.0);
        assertEquals(expectedIntensity, LIGHT.getIntensity(P1),
                "ERROR: getIntensity() calculates wrong intensity at standard distance");

        // ================== Boundary Values Tests ==================
        // TC02: Point coincides with light position
        assertEquals(I0, LIGHT.getIntensity(POSITION),
                "ERROR: getIntensity() should return original I0 when point is exactly on the light");
    }

    /**
     * Test method for {@link PointLight#getL(primitives.Point)}.
     */
    @Test
    void testGetL() {
        // ================== Equivalence Partitions Tests ==================
        // TC03: Normal point
        assertEquals(new Vector(0, 1, 0), LIGHT.getL(P1),
                "ERROR: getL() returns wrong direction vector from point light");

        // ================== Boundary Values Tests ==================
        // TC04: Point coincides with light position
        assertThrows(IllegalArgumentException.class, () -> LIGHT.getL(POSITION),
                "ERROR: getL() should throw an exception when point coincides with light source (zero vector)");
    }
}