package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Ray class.
 */
class RayTest {
    // ================== Test Constants ==================
    /** A small tolerance for floating-point comparisons. */
    private static final double DELTA = 1E-15;

    // ================== Test Points & Vectors ==================
    /** A point for creating rays. */
    private static final Point P1 = new Point(1, 2, 3);
    /** A non-normalized vector for creating rays. */
    private static final Vector V_NON_NORMALIZED = new Vector(2, 4, 6);
    /** A normalized vector for creating rays. */
    private static final Vector V_NORMALIZED = new Vector(1, 0, 0);

    // ================== Error Messages ==================
    private static final String ERROR_CONSTRUCTOR_NORMALIZATION = "ERROR: Ray constructor does not normalize the direction vector";
    private static final String ERROR_CONSTRUCTOR_NORMALIZED_INPUT = "ERROR: Ray constructor modifies an already normalized vector";

    /**
     * Test method for {@link primitives.Ray#Ray(Point, Vector)}.
     */
    @Test
    void testConstructor() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: Creating a ray with a non-normalized vector
        Ray ray1 = new Ray(P1, V_NON_NORMALIZED);
        assertEquals(1, ray1.direction().length(), DELTA, ERROR_CONSTRUCTOR_NORMALIZATION);

        // ================== Boundary Values Tests ==================
        // BVA01: Creating a ray with an already normalized vector
        Ray ray2 = new Ray(P1, V_NORMALIZED);
        assertEquals(V_NORMALIZED, ray2.direction(), ERROR_CONSTRUCTOR_NORMALIZED_INPUT);
    }
}
