package lighting.impl;

import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Double3;
import lighting.impl.AmbientLight;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AmbientLight} class.
 */
class AmbientLightTest {

    /**
     * Default constructor for Javadoc purposes.
     */
    public AmbientLightTest() {}

    // ================== Test Constants ==================
    /** Base original intensity of the ambient light. */
    private static final Color IA = new Color(200, 150, 100);

    /**
     * Test method for {@link AmbientLight#getIntensity()}.
     */
    @Test
    void testGetIntensity() {
        // ================== Equivalence Partitions Tests ==================
        // TC01: Getting the intensity of the ambient light (Ia)
        AmbientLight ambientLight = new AmbientLight(IA);

        assertEquals(IA, ambientLight.getIntensity(),
                "ERROR: getIntensity() for AmbientLight does not return the correctly scaled color");
    }
}