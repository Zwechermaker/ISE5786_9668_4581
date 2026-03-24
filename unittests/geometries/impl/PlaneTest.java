package geometries.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import primitives.Point;
import primitives.Vector;

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
}
