package lighting.impl;

import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SpotLight} class.
 */
class SpotLightTest {

    /**
     * Default constructor for Javadoc purposes.
     */
    public SpotLightTest() {}

    // ================== Test Constants ==================
    /** Original intensity of the spotlight. */
    private static final Color I0 = new Color(1000, 1000, 1000);
    /** Position of the spotlight. */
    private static final Point POSITION = new Point(0, 0, 0);
    /** Direction of the spotlight beam. */
    private static final Vector DIRECTION = new Vector(0, 1, 0);
    /** A spotlight instance for tests, configured with attenuation factors. */
    private static final SpotLight LIGHT = new SpotLight(I0, POSITION, DIRECTION)
            .setKl(0.1).setKq(0.01);

    /** A point directly in front of the spotlight. */
    private static final Point P_FRONT = new Point(0, 10, 0);
    /** A point directly behind the spotlight. */
    private static final Point P_BEHIND = new Point(0, -10, 0);
    /** A point at exactly 90 degrees to the spotlight's direction. */
    private static final Point P_90 = new Point(10, 0, 0);
    /** A point at a 45-degree angle to test the narrow beam drop-off. */
    private static final Point P_ANGLE = new Point(10, 10, 0);

    /**
     * Test method for {@link SpotLight#getIntensity(primitives.Point)}.
     */
    @Test
    void testGetIntensity() {
        // ================== Equivalence Partitions Tests ==================
        // TC01: Point in front of spotlight (inside the cone)
        // Vector to point is (0,1,0), angle is 0, dot product is 1. Attenuation is same as PointLight.
        Color expectedIntensity = I0.scale(1.0 / 3.0);
        assertEquals(expectedIntensity, LIGHT.getIntensity(P_FRONT),
                "ERROR: getIntensity() wrong intensity for point directly in front");

        // TC02: Point behind the spotlight
        assertEquals(Color.BLACK, LIGHT.getIntensity(P_BEHIND),
                "ERROR: getIntensity() should be Color.BLACK when point is behind the spotlight");

        // ================== Boundary Values Tests ==================
        // TC03: Point coincides with light position
        assertEquals(I0, LIGHT.getIntensity(POSITION),
                "ERROR: getIntensity() should return original I0 when point is exactly on the light");

        // TC04: Point at exactly 90 degrees to spotlight direction
        assertEquals(Color.BLACK, LIGHT.getIntensity(P_90),
                "ERROR: getIntensity() should be Color.BLACK at exactly 90 degrees");
    }

    /**
     * Test method for {@link SpotLight#getL(primitives.Point)}.
     */
    @Test
    void testGetL() {
        // ================== Equivalence Partitions Tests ==================
        // TC05: Normal point
        assertEquals(new Vector(0, 1, 0), LIGHT.getL(P_FRONT),
                "ERROR: getL() returns wrong direction vector from spotlight");

        // ================== Boundary Values Tests ==================
        // TC06: Point coincides with light position
        assertThrows(IllegalArgumentException.class, () -> LIGHT.getL(POSITION),
                "ERROR: getL() should throw an exception when point coincides with light source (zero vector)");
    }

    /**
     * Test method for the Narrow-beam Spotlight bonus.
     */
    @Test
    void testNarrowBeam() {
        SpotLight narrowLight = new SpotLight(I0, POSITION, DIRECTION)
                .setKl(0.1).setKq(0.01).setNarrowBeam(10);

        // ================== Equivalence Partitions Tests ==================

        // TC07: Point off-center (45 degrees)
        // L vector to P_ANGLE is (10, 10, 0), normalized is (1/sqrt(2), 1/sqrt(2), 0).
        // Dot product with direction (0,1,0) is exactly 1/sqrt(2).
        // Distance is sqrt(200). Attenuation denominator = 1 + 0.1*sqrt(200) + 0.01*200 = 3 + sqrt(2).
        // Because narrowBeam is 10, the projection factor is (1/sqrt(2))^10 = 1/32.
        Color expectedNarrowIntensity = I0.scale(1d / (3d + Math.sqrt(2d))).scale(1d / 32d);

        assertEquals(expectedNarrowIntensity, narrowLight.getIntensity(P_ANGLE),
                "ERROR: Narrow beam intensity calculated incorrectly for off-center angle");

        // TC08: Point behind the narrow beam spotlight
        assertEquals(Color.BLACK, narrowLight.getIntensity(P_BEHIND),
                "ERROR: Narrow beam should still be Color.BLACK when point is behind");

        // ================== Boundary Values Tests ==================

        // TC09: Point exactly in the center of the beam (angle = 0)
        // cos(0) = 1.  1^10 = 1. The intensity should be identical to the standard spotlight.
        assertEquals(LIGHT.getIntensity(P_FRONT), narrowLight.getIntensity(P_FRONT),
                "ERROR: Narrow beam intensity should match standard spotlight perfectly in the dead-center of the beam");

        // TC10: Point at exactly 90 degrees
        assertEquals(Color.BLACK, narrowLight.getIntensity(P_90),
                "ERROR: Narrow beam should be Color.BLACK at exactly 90 degrees");
    }
}