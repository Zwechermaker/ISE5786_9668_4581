package lighting.impl;

import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import lighting.impl.DirectionalLight;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DirectionalLight} class.
 */
class DirectionalLightTest {

    /**
     * Default constructor for Javadoc purposes.
     */
    public DirectionalLightTest() {}

    // ================== Test Constants ==================
    /** A constant intensity color for tests. */
    private static final Color INTENSITY = new Color(255, 255, 255);
    /** A standard downward direction vector. */
    private static final Vector DIRECTION = new Vector(0, -1, 0);
    /** A directional light instance for tests. */
    private static final DirectionalLight LIGHT = new DirectionalLight(INTENSITY, DIRECTION);
    /** A standard point for tests. */
    private static final Point P1 = new Point(1, 2, 3);

    /**
     * Test method for {@link DirectionalLight#getIntensity(primitives.Point)}.
     */
    @Test
    void testGetIntensity() {
        // ================== Equivalence Partitions Tests ==================
        // TC01: Intensity should be constant everywhere
        assertEquals(INTENSITY, LIGHT.getIntensity(P1),
                "ERROR: getIntensity() for directional light does not return constant intensity");
    }

    /**
     * Test method for {@link DirectionalLight#getL(primitives.Point)}.
     */
    @Test
    void testGetL() {
        // ================== Equivalence Partitions Tests ==================
        // TC02: Directional light vector should be constant and normalized
        assertEquals(DIRECTION.normalize(), LIGHT.getL(P1),
                "ERROR: getL() for directional light does not return the correct normalized direction vector");
    }
}