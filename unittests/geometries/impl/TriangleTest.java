package geometries.impl;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Triangle class.
 */
class TriangleTest {
    /** Default constructor to satisfy JavaDoc generator */
    TriangleTest() { /* to satisfy JavaDoc generator */ }

    // ================== Test Constants ==================
    /** First vertex of the triangle. */
    private static final Point P1 = new Point(1, 2, 3);
    /** Second vertex of the triangle. */
    private static final Point P2 = new Point(4, 6, 3);
    /** Third vertex of the triangle. */
    private static final Point P3 = new Point(1, 2, 8);
    /** Delta value for accuracy when comparing double values. */
    private static final double DELTA = 1e-6;
    /** Error message for when a triangle is constructed with co-located points. */
    private static final String ERROR_CONSTRUCTED_COLOCATED_POINTS = "Constructed a triangle with co-located points";

    /**
     * Test method for {@link geometries.impl.Triangle#Triangle(Point, Point, Point)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Correct triangle constructor
        assertDoesNotThrow(() -> new Triangle(P1, P2, P3),
                "Failed constructing a correct triangle");

        // =============== Boundary Values Tests ==================
        // TC02: Two points are equal, first and second
        assertThrows(IllegalArgumentException.class, () -> new Triangle(P1, P1, P2),
                ERROR_CONSTRUCTED_COLOCATED_POINTS);

        // TC03: Two points are equal, first and third
        assertThrows(IllegalArgumentException.class, () -> new Triangle(P1, P2, P1),
                ERROR_CONSTRUCTED_COLOCATED_POINTS);
        
        // TC04: Two points are equal, second and third
        assertThrows(IllegalArgumentException.class, () -> new Triangle(P1, P2, P2),
                ERROR_CONSTRUCTED_COLOCATED_POINTS);

        // TC05: Points are collinear
        assertThrows(IllegalArgumentException.class, () -> new Triangle(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
                "Constructed a triangle with collinear points");
    }

    /**
     * Test method for {@link geometries.impl.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC06: A standard point on the triangle
        Triangle triangle = new Triangle(P1, P2, P3);
        Vector normal = triangle.getNormal(P1);

        // Ensure normal is a unit vector
        assertEquals(1, normal.length(), DELTA, "Triangle's normal is not a unit vector");

        // Ensure normal is orthogonal to the edges
        Vector edge1 = P2.subtract(P1);
        Vector edge2 = P3.subtract(P1);
        assertEquals(0, normal.dotProduct(edge1), DELTA, "Triangle's normal is not orthogonal to an edge");
        assertEquals(0, normal.dotProduct(edge2), DELTA, "Triangle's normal is not orthogonal to an edge");
    }
}
