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
    /** A ray for tests. */
    private static final Ray RAY = new Ray(P1, V_NORMALIZED);
    /**
     * Test method for {@link primitives.Ray#Ray(Point, Vector)}.
     */
    @Test
    void testConstructor() {
        // ================== Equivalence Partitions Tests ==================
        // TC01: Creating a ray with a non-normalized vector
        Ray ray = new Ray(P1, V_NON_NORMALIZED);
        assertEquals(1, RAY.direction().length(), DELTA, "ERROR: Ray constructor does not normalize the direction vector");

        // ================== Boundary Values Tests ==================
        // TC02: Creating a ray with an already normalized vector
        assertEquals(V_NORMALIZED, RAY.direction(), "ERROR: Ray constructor modifies an already normalized vector");
    }
    /**
     * Test method for {@link primitives.Ray#getPoint(double)}.
     */
    @Test
    void testGetPoint() {
        // ================== Equivalence Partitions Tests ==================
        // TC03: Parameter is positive
        assertEquals(new Point(3,2,3), RAY.getPoint(2), "ERROR: getPoint fails for positive parameter");

        // TC04: Parameter is negative
        assertEquals(new Point(-1,2,3), RAY.getPoint(-2), "ERROR: getPoint fails for negative parameter");

        // ================== Boundary Values Tests ==================
        // TC05: Parameter is zero
        assertEquals(P1, RAY.getPoint(0), "ERROR: getPoint fails for zero parameter");
    }
}
