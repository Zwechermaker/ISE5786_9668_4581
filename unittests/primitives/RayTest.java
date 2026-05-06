package primitives;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import geometries.api.Intersectable.Intersection;
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

    /** A dummy geometry for creating intersections. */
    private static final Sphere DUMMY_GEOMETRY = new Sphere(Point.ZERO, 1);

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
     * Test method for {@link primitives.Ray#findClosestIntersection(List)}.
     * Tests sorting and filtering of intersection points (t > 0).
     */
    @Test
    void testFindClosestIntersection() {
        // Create intersection points for testing
        Intersection iClose = new Intersection(DUMMY_GEOMETRY, P_CLOSE);
        Intersection iFar1 = new Intersection(DUMMY_GEOMETRY, P_FAR_1);
        Intersection iFar2 = new Intersection(DUMMY_GEOMETRY, P_FAR_2);

        // ================== Equivalence Partitions Tests ==================
        // TC06: A middle point is the closest to the ray's head
        assertEquals(iClose, RAY.findClosestIntersection(List.of(iFar1, iClose, iFar2)),
                "ERROR: findClosestIntersection fails when the closest point is in the middle of the list");

        // ================== Boundary Values Tests ==================
        // TC07: Empty list (should return null)
        assertNull(RAY.findClosestIntersection(List.of()),
                "ERROR: findClosestIntersection should return null for an empty list");

        // TC08: The first point is the closest to the ray's head
        assertEquals(iClose, RAY.findClosestIntersection(List.of(iClose, iFar1, iFar2)),
                "ERROR: findClosestIntersection fails when the closest point is the first in the list");

        // TC09: The last point is the closest to the ray's head
        assertEquals(iClose, RAY.findClosestIntersection(List.of(iFar1, iFar2, iClose)),
                "ERROR: findClosestIntersection fails when the closest point is the last in the list");
    }
}