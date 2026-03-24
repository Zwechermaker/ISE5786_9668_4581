package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Ray class.
 */
class RayTest {
    /**
     * Default constructor for Javadoc purposes.
     */
    RayTest() {}

    // ================== Test Constants ==================
    /** A small tolerance for floating-point comparisons. */
    private static final double DELTA = 1E-15;
    /** A point for creating rays. */
    private static final Point P1 = new Point(1, 2, 3);
    /** A non-normalized vector for creating rays. */
    private static final Vector V_NON_NORMALIZED = new Vector(2, 4, 6);
    /** A normalized vector for creating rays. */
    private static final Vector V_NORMALIZED = new Vector(1, 0, 0);

    /**
     * Test method for {@link primitives.Ray#Ray(Point, Vector)}.
     */
    @Test
    void testConstructor() {
        // ================== Equivalence Partitions Tests ==================
        // TC01: Creating a ray with a non-normalized vector
        Ray ray1 = new Ray(P1, V_NON_NORMALIZED);
        assertEquals(1, ray1.direction().length(), DELTA, "ERROR: Ray constructor does not normalize the direction vector");

        // ================== Boundary Values Tests ==================
        // TC02: Creating a ray with an already normalized vector
        Ray ray2 = new Ray(P1, V_NORMALIZED);
        assertEquals(V_NORMALIZED, ray2.direction(), "ERROR: Ray constructor modifies an already normalized vector");
    }
}
