package geometries.api;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Geometries composite class.
 */
class GeometriesTest {
    /** Default constructor to satisfy JavaDoc generator */
    GeometriesTest() { /* to satisfy JavaDoc generator */ }
    /**
     * Test method for {@link geometries.api.Geometries#add(Intersectable...)}.
     */
    @Test
    void testAdd() {
        Geometries geometries = new Geometries();
        Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 0, 1));
        Sphere sphere = new Sphere(new Point(0, 0, 2), 1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Add multiple geometries at once
        geometries.add(plane, sphere);
        // Ray hits the plane and the sphere at 2 points. total 3 points.
        List<Point> result = geometries.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1)));
        assertNotNull(result, "Adding multiple geometries failed");
        assertEquals(3, result.size(), "Adding multiple geometries resulted in wrong intersection count");


        // =============== Boundary Values Tests ==================

        // TC02: Add no geometries
        Geometries emptyGeometries = new Geometries();
        assertDoesNotThrow(() -> emptyGeometries.add(), "Adding nothing should not throw an exception");
        assertNull(emptyGeometries.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1))),
                "Adding nothing should leave the collection empty (return null for intersections)");

        // TC03: Add a single geometry
        Geometries singleGeometry = new Geometries();
        singleGeometry.add(plane);
        // Ray hits only the plane, total of one point.
        result = singleGeometry.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1)));
        assertNotNull(result, "Adding a single geometry failed");
        assertEquals(1, result.size(), "Adding a single geometry resulted in wrong intersection count");
    }

    /**
     * Test method for {@link geometries.api.Geometries#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Setup standard testing geometries
        // Plane at z = 0
        Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 0, 1));
        // Sphere centered at (0,0,2) with radius 1 (covers z from 1 to 3)
        Sphere sphere = new Sphere(new Point(0, 0, 2), 1);
        // Triangle sitting flat on the z = 4 plane
        Triangle triangle = new Triangle(new Point(0, 1, 4), new Point(1, -1, 4), new Point(-1, -1, 4));

        Geometries geometries = new Geometries(plane, sphere, triangle);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Some geometries intersect, but not all of them
        // Ray starts at z=1.5, pointing up.
        // Misses the plane (starts after it). Hits the sphere once (starts inside). Hits the triangle once. -> Total 2 points.
        List<Point> result = geometries.findIntersections(new Ray(new Point(0, 0, 1.5), new Vector(0, 0, 1)));
        assertNotNull(result, "Should not return null when there are intersections");
        assertEquals(2, result.size(), "EP: Some geometries intersect - wrong number of points returned");


        // =============== Boundary Values Tests ==================

        // TC02: Empty geometries collection
        // According to instructions, this must return null (not an empty list).
        Geometries emptyGeometries = new Geometries();
        assertNull(emptyGeometries.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1))),
                "BVA: Empty geometries collection must return null");

        // TC03: No geometries intersect
        // Ray starts at z=5, pointing up (away from all shapes).
        assertNull(geometries.findIntersections(new Ray(new Point(0, 0, 5), new Vector(0, 0, 1))),
                "BVA: No geometries intersect must return null (not an empty list)");

        // TC04: Exactly ONE geometry intersects
        // Misses the plane and the sphere. hits only the triangle. total 1 point.
        result = geometries.findIntersections(new Ray(new Point(0, 0, 3.5), new Vector(0, 0, 1)));
        assertNotNull(result, "Should not return null when there is an intersection");
        assertEquals(1, result.size(), "BVA: Exactly one geometry intersects - wrong number of points");

        // TC05: all geometries intersect
        result = geometries.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1)));
        assertNotNull(result, "Should not return null when there are intersections");
        assertEquals(4, result.size(), "BVA: All geometries intersect - wrong number of points returned");
    }
}