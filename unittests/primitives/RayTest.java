package primitives;

import org.junit.jupiter.api.Test;

import java.util.List;

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

    // Points at varying distances from the Ray's head (P1) for closest point tests
    /** A point for tests at (2, 2, 3) representing a distance of 1. */
    private static final Point P_CLOSE = new Point(2, 2, 3);

    /** A point for tests at (3, 2, 3) representing a distance of 2. */
    private static final Point P_FAR_1 = new Point(3, 2, 3);

    /** A point for tests at (4, 2, 3) representing a distance of 3. */
    private static final Point P_FAR_2 = new Point(4, 2, 3);

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
        assertEquals(new Point(3, 2, 3), RAY.getPoint(2), "ERROR: getPoint fails for positive parameter");

        // TC04: Parameter is negative
        assertEquals(new Point(-1, 2, 3), RAY.getPoint(-2), "ERROR: getPoint fails for negative parameter");

        // ================== Boundary Values Tests ==================
        // TC05: Parameter is zero
        assertEquals(P1, RAY.getPoint(0), "ERROR: getPoint fails for zero parameter");
    }

    /**
     * Test method for {@link primitives.Ray#getPoints(double, double)}.
     * Tests sorting and filtering of intersection points (t > 0).
     */
    @Test
    void testGetPoints() {
        // ================== Equivalence Partitions Tests ==================
        // TC06: Both parameters are positive and in order
        assertEquals(List.of(new Point(3, 2, 3), new Point(4, 2, 3)),
                RAY.getPoints(2, 3),
                "ERROR: getPoints fails for positive parameters in order");

        // TC07: Both parameters are positive but reversed (should sort closest first)
        assertEquals(List.of(new Point(3, 2, 3), new Point(4, 2, 3)),
                RAY.getPoints(3, 2),
                "ERROR: getPoints fails to sort reversed positive parameters");

        // TC08: Both parameters are negative (should return null)
        assertNull(RAY.getPoints(-2, -3),
                "ERROR: getPoints should return null for two negative parameters");

        // TC09: Mixed sign parameters (should filter out the negative, returning 1 point)
        assertEquals(List.of(new Point(4, 2, 3)),
                RAY.getPoints(-2, 3),
                "ERROR: getPoints fails to filter mixed sign parameters");

        // ================== Boundary Values Tests ==================
        // TC10: Both parameters are zero (should return null)
        assertNull(RAY.getPoints(0, 0),
                "ERROR: getPoints should return null when both parameters are zero");

        // TC11: One parameter is zero, one positive (should filter out zero, returning 1 point)
        assertEquals(List.of(new Point(3, 2, 3)),
                RAY.getPoints(0, 2),
                "ERROR: getPoints fails when one parameter is zero and the other positive");

        // TC12: One parameter is zero, one negative (should return null)
        assertNull(RAY.getPoints(0, -2),
                "ERROR: getPoints should return null when parameters are zero and negative");

        // TC13: Identical positive parameters
        assertEquals(List.of(new Point(3, 2, 3), new Point(3, 2, 3)),
                RAY.getPoints(2, 2),
                "ERROR: getPoints fails for identical positive parameters");
    }

    /**
     * Test method for {@link primitives.Ray#findClosestPoint(List)}.
     */
    @Test
    void testFindClosestPoint() {
        // ================== Equivalence Partitions Tests ==================
        // TC14: A middle point is the closest to the ray's head
        assertEquals(P_CLOSE, RAY.findClosestPoint(List.of(P_FAR_1, P_CLOSE, P_FAR_2)),
                "ERROR: findClosestPoint fails when the closest point is in the middle of the list");

        // ================== Boundary Values Tests ==================
        // TC15: Empty list (should return null)
        assertNull(RAY.findClosestPoint(List.of()),
                "ERROR: findClosestPoint should return null for an empty list");

        // TC16: The first point is the closest to the ray's head
        assertEquals(P_CLOSE, RAY.findClosestPoint(List.of(P_CLOSE, P_FAR_1, P_FAR_2)),
                "ERROR: findClosestPoint fails when the closest point is the first in the list");

        // TC17: The last point is the closest to the ray's head
        assertEquals(P_CLOSE, RAY.findClosestPoint(List.of(P_FAR_1, P_FAR_2, P_CLOSE)),
                "ERROR: findClosestPoint fails when the closest point is the last in the list");
    }
}