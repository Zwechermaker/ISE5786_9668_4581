package lighting.impl;

import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import lighting.PointLight;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PointLight} class.
 */
class PointLightTest {

    private final Color originalIntensity = new Color(1000, 1000, 1000);
    private final Point lightPosition = new Point(0, 0, 0);
    private final PointLight light = new PointLight(originalIntensity, lightPosition)
            .setKl(0.1).setKq(0.01);

    public PointLightTest() {}

    /**
     * Test method for {@link PointLight#getIntensity(primitives.Point)}.
     */
    @Test
    void testGetIntensity() {
        // TC01: EP 1 - Point at a normal distance
        Point p1 = new Point(0, 10, 0);
        // Distance is 10. Attenuation denominator: 1 + 0.1*10 + 0.01*100 = 1 + 1 + 1 = 3
        // Expected intensity: 1000 / 3
        Color expectedIntensity = originalIntensity.scale(1.0 / 3.0);
        assertEquals(expectedIntensity, light.getIntensity(p1),
                "Wrong intensity at standard distance");

        // TC11: BV 1 - Point coincides with light position
        // According to requirements: should return original intensity I0
        assertEquals(originalIntensity, light.getIntensity(lightPosition),
                "Intensity should be original I0 when point is exactly on the light");
    }

    /**
     * Test method for {@link PointLight#getL(primitives.Point)}.
     */
    @Test
    void testGetL() {
        // TC02: EP 1 - Normal point
        Point p1 = new Point(0, 10, 0);
        assertEquals(new Vector(0, 1, 0), light.getL(p1),
                "Wrong direction vector from point light");

        // TC12: BV 1 - Point coincides with light position
        // According to requirements: Expected to fail due to attempt to create zero vector
        assertThrows(IllegalArgumentException.class, () -> light.getL(lightPosition),
                "Expected exception when point coincides with light source");
    }
}