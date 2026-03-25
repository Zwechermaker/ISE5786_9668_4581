package geometries.impl;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;
import primitives.Ray;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Sphere class.
 */
class SphereTest {
    /**
     * Default constructor for Javadoc purposes.
     */
    SphereTest() {}

    // ================== Test Constants ==================
    /** The center point of the sphere. */
    private static final Point CENTER = new Point(0, 0, 0);
    /** The radius of the sphere. */
    private static final double RADIUS = 5;
    /** A sphere for testing. */
    private static final Sphere SPHERE = new Sphere(CENTER, RADIUS);

    /**
     * Test method for {@link geometries.impl.Sphere#Sphere(Point, double)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Correct sphere constructor
        assertDoesNotThrow(() -> new Sphere(CENTER, RADIUS), "Failed constructing a correct sphere");

        // =============== Boundary Values Tests ==================
        // TC02: Zero radius
        assertThrows(IllegalArgumentException.class, () -> new Sphere(CENTER, 0),
                "Constructed a sphere with zero radius");

        // TC03: Negative radius
        assertThrows(IllegalArgumentException.class, () -> new Sphere(CENTER, -5),
                "Constructed a sphere with negative radius");
    }

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC04: A random point on the sphere
        Point pointOnSphere = new Point(3, 4, 0);
        assertEquals(new Vector(3, 4, 0).normalize(), SPHERE.getNormal(pointOnSphere), "getNormal() for sphere is wrong");
    }

    /**
     * Test method for {@link geometries.impl.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // TC05: Ray's line is outside the sphere (0 points)
        // Ray starts at (-10, 10, 0) going straight right. The sphere's max height is y=5, so it misses completely.
        assertNull(SPHERE.findIntersections(new Ray(new Point(-10, 10, 0), new Vector(1, 0, 0))),
                "Ray's line out of sphere");

        // TC06: Ray starts before and crosses the sphere (2 points)
        // Ray starts at (-10, 0, 0) going right. Should hit at (-5, 0, 0) and (5, 0, 0).
        Point p1 = new Point(-5, 0, 0);
        Point p2 = new Point(5, 0, 0);
        List<Point> result = SPHERE.findIntersections(new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0)));
        assertEquals(2, result.size(), "Wrong number of points");

        // We check if it contains both points, since the order returned by your implementation might vary
        assertTrue(result.contains(p1) && result.contains(p2), "Ray crosses sphere returning wrong points");

        // TC07: Ray starts inside the sphere (1 point)
        // Ray starts at the center (0, 0, 0) going right. Should only hit the far edge at (5, 0, 0).
        result = SPHERE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 0)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(p2), result, "Ray from inside sphere");

        // TC08: Ray starts after the sphere (0 points)
        // Ray starts at (10, 0, 0) going right. The line intersects the sphere behind the ray, but not in front.
        assertNull(SPHERE.findIntersections(new Ray(new Point(10, 0, 0), new Vector(1, 0, 0))),
                "Ray starts after the sphere");
    }
}
