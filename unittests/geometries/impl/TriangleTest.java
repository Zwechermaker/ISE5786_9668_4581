package geometries.impl;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;
import geometries.api.Intersectable.Intersection;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Triangle class.
 */
class TriangleTest {
    /** Default constructor to satisfy JavaDoc generator */
    TriangleTest() { /* to satisfy JavaDoc generator */ }

    // ================== Test Constants ==================
    /** First vertex of the triangle (Top). */
    private static final Point P1 = new Point(0, 1, 1);
    /** Second vertex of the triangle (Bottom Right). */
    private static final Point P2 = new Point(1, -1, 1);
    /** Third vertex of the triangle (Bottom Left). */
    private static final Point P3 = new Point(-1, -1, 1);

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

    /**
     * Test method for {@link geometries.impl.Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(P1, P2, P3);

        // ============ Equivalence Partitions Tests ==============
        // ---- Plane EP Inherited Tests ----

        // TC07: Ray intersects the plane and is inside the triangle (Triangle EP 1)
        // Ray starts below at y=-1, z=0, goes diagonally up (0, 1, 1). Hits plane at (0, 0, 1) - safely inside.
        List<Point> result = triangle.findIntersections(new Ray(new Point(0, -1, 0), new Vector(0, 1, 1)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(new Point(0, 0, 1)), result, "Ray crosses triangle returning wrong point");

        // TC08: Ray does not intersect the plane at all (Plane EP 2)
        // Ray starts above the plane (z=2) and points away (z=1 direction).
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
                "Ray pointing away from the plane should return null");

        // ---- Triangle-Specific EP Tests ----

        // TC09: Ray intersects the plane, but outside against an edge (Triangle EP 2)
        // Ray shoots straight up, hitting plane at (0, -2, 1). This is directly below edge P2-P3.
        assertNull(triangle.findIntersections(new Ray(new Point(0, -2, -1), new Vector(0, 0, 1))),
                "Ray hitting outside against an edge should return null");

        // TC10: Ray intersects the plane, but outside against a vertex (Triangle EP 3)
        // Ray shoots straight up, hitting plane at (0, 2, 1). This is directly above vertex P1.
        assertNull(triangle.findIntersections(new Ray(new Point(0, 2, -1), new Vector(0, 0, 1))),
                "Ray hitting outside against a vertex should return null");


        // =============== Boundary Values Tests ==================
        // ---- Plane BVA Inherited Tests ----

        // TC11: Ray is parallel to the plane and included in it.
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 0))),
                "Ray parallel and included in plane should return null");

        // TC12: Ray is parallel to the plane and NOT included in it.
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, 2), new Vector(1, 0, 0))),
                "Ray parallel and not included in plane should return null");

        // TC13: Ray is orthogonal to the plane and starts before it (Hits inside).
        // Ray starts at (0,0,-1) and goes straight up (0,0,1). Hits at (0,0,1).
        result = triangle.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(new Point(0, 0, 1)), result, "Ray orthogonal to triangle plane, starts before");

        // TC14: Ray is orthogonal to the plane and starts exactly IN the plane.
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1))),
                "Ray orthogonal to plane, starts in plane should return null");

        // TC15: Ray is orthogonal to the plane and starts after it.
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
                "Ray orthogonal to plane, starts after plane should return null");

        // TC16: Ray starts on the plane, neither orthogonal nor parallel.
        assertNull(triangle.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 1, 1))),
                "Ray starting in plane (not orthogonal/parallel) should return null");

        // ---- Triangle-Specific BVA Tests ----

        // TC17: Ray intersects exactly on an edge (Triangle BVA 1)
        // Ray shoots straight up, hitting plane at (0, -1, 1). This is exactly on the edge between P2 and P3.
        assertNull(triangle.findIntersections(new Ray(new Point(0, -1, -1), new Vector(0, 0, 1))),
                "Ray intersecting exactly on an edge should return null");

        // TC18: Ray intersects exactly on a vertex (Triangle BVA 2)
        // Ray shoots straight up, hitting plane at (0, 1, 1). This is exactly Vertex P1.
        assertNull(triangle.findIntersections(new Ray(new Point(0, 1, -1), new Vector(0, 0, 1))),
                "Ray intersecting exactly on a vertex should return null");

        // TC19: Ray intersects on an edge's continuation (Triangle BVA 3)
        // Ray shoots straight up, hitting plane at (2, -1, 1). This is on the infinite line of edge P2-P3, but outside the triangle.
        assertNull(triangle.findIntersections(new Ray(new Point(2, -1, -1), new Vector(0, 0, 1))),
                "Ray intersecting on edge continuation should return null");
    }

    /**
     * Test method for {@link geometries.impl.Triangle#calcIntersections(primitives.Ray)}.
     */
    @Test
    void testCalcIntersections() {
        Triangle triangle = new Triangle(P1, P2, P3);

        // ============ Equivalence Partitions Tests ==============
        // TC20: Ray intersects the triangle
        List<Intersection> result = triangle.calcIntersections(new Ray(new Point(0, -1, 0), new Vector(0, 1, 1)));
        assertEquals(1, result.size(), "Wrong number of intersections");
        assertSame(triangle, result.get(0).geometry, "Intersection does not belong to the correct geometry");

        // TC21: Ray does not intersect the triangle
        assertNull(triangle.calcIntersections(new Ray(new Point(0, 2, -1), new Vector(0, 0, 1))),
                "Ray hitting outside against a vertex should return null");
    }
}
