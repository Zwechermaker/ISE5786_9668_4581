package geometries.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import java.util.List;

/**
 * Unit tests for the Plane class.
 */
class PlaneTest {
    /** Default constructor to satisfy JavaDoc generator */
    PlaneTest() { /* to satisfy JavaDoc generator */ }

    // ================== Test Constants ==================
    /** A point on the plane for constructor tests. */
    private static final Point Q0 = new Point(1, 2, 3);
    /** A vector for the plane's normal (not a unit vector). */
    private static final Vector NORMAL_VECTOR = new Vector(2, 4, 4);
    /** First point for the 3-point constructor tests. */
    private static final Point P1 = new Point(1, 0, 0);
    /** Second point for the 3-point constructor tests. */
    private static final Point P2 = new Point(0, 1, 0);
    /** Third point for the 3-point constructor tests. */
    private static final Point P3 = new Point(0, 0, 1);
    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;
    /** Error message for when a plane is constructed with equal points. */
    private static final String ERROR_CONSTRUCTED_EQUAL_POINTS = "Constructed a plane with two equal points";

    /**
     * Test method for {@link Plane} constructors.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Correct 3-points constructor
        assertDoesNotThrow(() -> new Plane(P1, P2, P3), "Failed constructing a correct plane");

        // TC02: Correct point-vector constructor (vector is not normalized)
        Plane plane = new Plane(Q0, NORMAL_VECTOR);
        assertEquals(1, plane.getNormal(null).length(), DELTA, "Plane constructor does not normalize the normal vector");

        // =============== Boundary Values Tests ==================
        // TC03: Two points are equal: first and second
        assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P1, P2),
                ERROR_CONSTRUCTED_EQUAL_POINTS);
        // TC04: Two points are equal: first and third
        assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P2, P1),
                ERROR_CONSTRUCTED_EQUAL_POINTS);
        // TC05: Two points are equal: second and third
        assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P2, P2),
                ERROR_CONSTRUCTED_EQUAL_POINTS);

        // TC06: Three points are equal
        assertThrows(IllegalArgumentException.class, () -> new Plane(P1, P1, P1),
                "Constructed a plane with three equal points");

        // TC07: Three points are on the same line
        assertThrows(IllegalArgumentException.class, () -> new Plane(new Point(1, 2, 3), new Point(2, 4, 6), new Point(3, 6, 9)),
                "Constructed a plane with three points on the same line");
    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC08: A random point on the plane
        Plane plane = new Plane(Q0, NORMAL_VECTOR);
        Vector normal = plane.getNormal(new Point(1, 1, 4));
        // Ensure the normal is correct
        assertEquals(NORMAL_VECTOR.normalize(), normal,
                "getNormal() for plane is wrong");

        // =============== Boundary Values Tests ==================
        // TC09: The point used to construct the plane
        normal = plane.getNormal(Q0);
        // Ensure the normal is correct
        assertEquals(NORMAL_VECTOR.normalize(), normal,
                "getNormal() for plane is wrong for the base point");
    }

    /**
     * Test method for {@link geometries.impl.Plane#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // We establish a simple plane: z = 1.
        // Q0 = (0, 0, 1), normal = (0, 0, 1).
        Plane plane = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1));

        // ============ Equivalence Partitions Tests ==============
        // The Ray is neither parallel nor orthogonal to the plane.

        // TC10: Ray intersects the plane at one point.
        // Ray starts at (1, 1, -1) and goes diagonally up (0, 1, 1).
        // It should intersect the z=1 plane at t=2 -> (1, 3, 1).
        // Notice this intersection point is NOT Q0.
        List<Point> result = plane.findIntersections(new Ray(new Point(1, 1, -1), new Vector(0, 1, 1)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(new Point(1, 3, 1)), result, "Ray crosses plane returning wrong point");

        // TC11: Ray does not intersect the plane.
        // Ray starts at (1, 1, 2) (above the plane) and goes diagonally up (0, 1, 1).
        // It points away from the plane, so no intersection.
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 2), new Vector(0, 1, 1))),
                "Ray pointing away from plane should return null");


        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray is parallel to the plane ****

        // TC12: The ray is included in the plane.
        // Starts on the z=1 plane, moves perfectly flat along x-axis.
        assertNull(plane.findIntersections(new Ray(new Point(0, 1, 1), new Vector(1, 0, 0))),
                "Ray included in plane should return null");

        // TC13: The ray is parallel, but not included in the plane.
        // Starts on the z=2 plane, moves flat along x-axis.
        assertNull(plane.findIntersections(new Ray(new Point(0, 1, 2), new Vector(1, 0, 0))),
                "Ray parallel to plane should return null");


        // **** Group 2: Ray is orthogonal to the plane ****

        // TC14: Ray starts before the plane.
        // Starts below (1, 1, -1), goes straight up (0, 0, 1). Hits exactly at (1, 1, 1).
        result = plane.findIntersections(new Ray(new Point(1, 1, -1), new Vector(0, 0, 1)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(new Point(1, 1, 1)), result, "Ray orthogonal to plane, starts before");

        // TC15: Ray starts IN the plane.
        // Starts on z=1, goes straight up. (t = 0). Standard behavior is returning null.
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 0, 1))),
                "Ray orthogonal to plane, starts in plane should return null");

        // TC16: Ray starts after the plane.
        // Starts above at z=2, goes straight up.
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 2), new Vector(0, 0, 1))),
                "Ray orthogonal to plane, starts after plane should return null");


        // **** Group 3: Ray begins on the plane (but is not parallel/orthogonal) ****

        // TC17: Ray starts on the plane, but not at the reference point Q0.
        // Starts at (1, 1, 1) and goes diagonally up (0, 1, 1).
        assertNull(plane.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 1, 1))),
                "Ray starting in plane (not Q0) should return null");


        // **** Group 4: Ray begins EXACTLY at the reference point Q0 ****

        // TC18: Ray starts at plane's reference point Q0 (0, 0, 1).
        // Goes diagonally up (0, 1, 1).
        assertNull(plane.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 1, 1))),
                "Ray starting at Q0 should return null");
    }
}
