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

    // ================== Test Constants ==================
    /** The center point of the sphere. */
    private static final Point CENTER = new Point(0, 0, 0);
    /** The radius of the sphere. */
    private static final double RADIUS = 5;
    /** A sphere for testing. */
    private static final Sphere SPHERE = new Sphere(CENTER, RADIUS);

    /** Default constructor to satisfy JavaDoc generator */
    SphereTest() { /* to satisfy JavaDoc generator */ }
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
        assertNull(SPHERE.findIntersections(new Ray(new Point(-10, 10, 0), new Vector(1, 0, 0))),
                "Ray's line out of sphere");

        // TC06: Ray starts before and crosses the sphere (2 points)
        // Shifted to y=-3 to avoid passing through the center (BVA)
        Point p1 = new Point(-4, -3, 0);
        Point p2 = new Point(4, -3, 0);
        List<Point> result = SPHERE.findIntersections(new Ray(new Point(-8, -3, 0), new Vector(1, 0, 0)));
        assertEquals(2, result.size(), "Wrong number of points");
        assertTrue(result.contains(p1) && result.contains(p2), "Ray crosses sphere returning wrong points");

        // TC07: Ray starts inside the sphere (1 point)
        // Shifted to y=-3 and x=-1 to be a standard internal point
        result = SPHERE.findIntersections(new Ray(new Point(-1, -3, 0), new Vector(1, 0, 0)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(p2), result, "Ray from inside sphere");

        // TC08: Ray starts after the sphere (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(8, -3, 0), new Vector(1, 0, 0))),
                "Ray starts after the sphere");

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray starts on the sphere surface (but not on center-crossing line) ****
        // TC11: Ray starts at sphere and goes inside (1 point)
        result = SPHERE.findIntersections(new Ray(new Point(-4, 3, 0), new Vector(1, 0, 0)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(new Point(4, 3, 0)), result, "Ray starts on sphere and goes inside");

        // TC12: Ray starts at sphere and goes outside (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(4, 3, 0), new Vector(1, 0, 0))),
                "Ray starts on sphere and goes outside");

        // **** Group 2: Ray's line crosses the sphere's center O (6 cases) ****
        Point cp1 = new Point(-5, 0, 0);
        Point cp2 = new Point(5, 0, 0);

        // TC13: Ray starts before the sphere (2 points)
        result = SPHERE.findIntersections(new Ray(new Point(-10, 0, 0), new Vector(1, 0, 0)));
        assertEquals(2, result.size(), "Wrong number of points");
        assertTrue(result.contains(cp1) && result.contains(cp2), "Ray starts before sphere, crosses center");

        // TC14: Ray starts at sphere and goes inside (1 point)
        result = SPHERE.findIntersections(new Ray(new Point(-5, 0, 0), new Vector(1, 0, 0)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(cp2), result, "Ray starts on sphere, crosses center");

        // TC15: Ray starts inside (1 point)
        result = SPHERE.findIntersections(new Ray(new Point(-2, 0, 0), new Vector(1, 0, 0)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(cp2), result, "Ray starts inside, crosses center");

        // TC16: Ray starts at the center (1 point)
        result = SPHERE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 0, 0)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(cp2), result, "Ray starts at center");

        // TC17: Ray starts at sphere and goes outside (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(5, 0, 0), new Vector(1, 0, 0))),
                "Ray starts on sphere, goes outside from center line");

        // TC18: Ray starts after sphere (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(10, 0, 0), new Vector(1, 0, 0))),
                "Ray starts after sphere on center line");

        // **** Group 3: Ray's line is tangent to the sphere (3 cases, all 0 points) ****
        // TC19: Ray starts before the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(-10, 5, 0), new Vector(1, 0, 0))),
                "Tangent ray, starts before tangent point");

        // TC20: Ray starts at the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(0, 5, 0), new Vector(1, 0, 0))),
                "Tangent ray, starts at tangent point");

        // TC21: Ray starts after the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(10, 5, 0), new Vector(1, 0, 0))),
                "Tangent ray, starts after tangent point");

        // **** Group 4: Special cases (Ray is orthogonal to the start-point vector) ****
        // TC22: Ray starts outside, orthogonal to Center-P0 vector (0 points)
        assertNull(SPHERE.findIntersections(new Ray(new Point(-10, 0, 0), new Vector(0, 1, 0))),
                "Ray orthogonal to P0-O vector, starts outside");

        // TC23: Ray starts inside, orthogonal to Center-P0 vector (1 point)
        result = SPHERE.findIntersections(new Ray(new Point(-3, 0, 0), new Vector(0, 1, 0)));
        assertEquals(1, result.size(), "Wrong number of points");
        assertEquals(List.of(new Point(-3, 4, 0)), result, "Ray orthogonal to P0-O vector, starts inside");
    }
}
