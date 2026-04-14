package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Tube class.
 */
class TubeTest {
    /**
     * Default constructor for Javadoc purposes.
     */
    TubeTest() {}

    // ================== Test Constants ==================
    /** An axis ray for the tube: starts at (10,10,10), direction (3,4,0). */
    private static final Ray AXIS_RAY = new Ray(new Point(10, 10, 10), new Vector(3, 4, 0));
    /** A tube with radius 10. Axis is in the XY plane, constant Z=10. */
    private static final Tube TUBE = new Tube(10, AXIS_RAY);
    /** A vector perpendicular to the tube's axis (normalized). */
    private static final Vector PERP_VEC_NORMAL = new Vector(-0.8, 0.6, 0);

    /**
     * Test method for {@link geometries.impl.Tube#Tube(double, Ray)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Correct tube constructor
        assertDoesNotThrow(() -> new Tube(10, AXIS_RAY), "Failed constructing a correct tube");

        // =============== Boundary Values Tests ==================
        // TC02: Zero radius
        assertThrows(IllegalArgumentException.class, () -> new Tube(0, AXIS_RAY),
                "Constructed a tube with zero radius");

        // TC03: Negative radius
        assertThrows(IllegalArgumentException.class, () -> new Tube(-5, AXIS_RAY),
                "Constructed a tube with a negative radius");
    }

    /**
     * Test method for {@link geometries.impl.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC04: Point on the round surface in the direction of the axis ray ("in front")
        assertEquals(PERP_VEC_NORMAL, TUBE.getNormal(new Point(14, 32, 10)), "getNormal() for point in front of axis origin is wrong");

        // TC05: Point on the round surface behind the axis ray's origin ("behind")
        assertEquals(PERP_VEC_NORMAL, TUBE.getNormal(new Point(-10, 0, 10)), "getNormal() for point behind axis origin is wrong");

        // =============== Boundary Values Tests ==================
        // TC06: Point on the tube on the same plane as the axis ray's origin (boundary)
        assertEquals(PERP_VEC_NORMAL, TUBE.getNormal(new Point(2, 16, 10)), "getNormal() for point on boundary plane is wrong");
    }

    /**
     * Test method for {@link geometries.impl.Tube#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {
        List<Point> result;

        // ============ Equivalence Partitions Tests ==============
        // General cases: Oblique angles, not passing through axis, not tangent.

        // TC10: Ray starts outside and crosses tube (2 points, oblique angle)
        result = TUBE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1)));
        assertEquals(2, result.size(), "EP: Ray crosses tube obliquely should have 2 points");

        // TC11: Ray starts outside and points away (0 points, oblique)
        assertNull(TUBE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(-1, -1, -1))),
                "EP: Ray pointing away obliquely should have 0 points");

        // TC12: Ray starts outside and line misses tube (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(0, 40, 0), new Vector(1, 0, 0))),
                "EP: Ray line missing tube should have 0 points");

        // TC13: Ray starts inside and points toward surface (1 point, oblique)
        result = TUBE.findIntersections(new Ray(new Point(12, 12, 10), new Vector(1, 2, 3)));
        assertEquals(1, result.size(), "EP: Ray inside pointing out obliquely should have 1 point");

        // TC14: Ray starts inside and points toward surface (1 point, different quadrant)
        result = TUBE.findIntersections(new Ray(new Point(10, 10, 10), new Vector(-1, 0, -1)));
        assertEquals(1, result.size(), "EP: Ray from axis pointing out obliquely should have 1 point");

        // =============== Boundary Values Tests ==================

        // **** Group 1: Parallel to Axis (0 points) ****
        // TC20: Ray is parallel to axis, starts inside
        assertNull(TUBE.findIntersections(new Ray(new Point(12, 12, 10), new Vector(3, 4, 0))), "BVA: Parallel inside");
        // TC21: Ray is parallel to axis, starts outside
        assertNull(TUBE.findIntersections(new Ray(new Point(0, 0, 0), new Vector(3, 4, 0))), "BVA: Parallel outside");
        // TC22: Ray is parallel to axis, starts on surface
        assertNull(TUBE.findIntersections(new Ray(new Point(2, 16, 10), new Vector(3, 4, 0))), "BVA: Parallel on surface");

        // **** Group 2: Orthogonal to Axis (90 degrees) ****
        // TC23: Orthogonal, starts outside, crosses through axis (2 points)
        result = TUBE.findIntersections(new Ray(new Point(2, 16, 10), new Vector(4, 3, 0)));
        assertEquals(2, result.size(), "BVA: Orthogonal crossing through axis");
        // TC24: Orthogonal, starts inside (1 point)
        result = TUBE.findIntersections(new Ray(new Point(10, 10, 10), new Vector(-4, 3, 0)));
        assertEquals(1, result.size(), "BVA: Orthogonal from axis origin");

        // **** Group 3: Ray Line Crosses the Center Axis ****
        // TC25: Oblique ray starts outside, crosses axis (2 points)
        result = TUBE.findIntersections(new Ray(new Point(2, 16, 0), new Vector(4, 3, 1)));
        assertEquals(2, result.size(), "BVA: Oblique ray crossing axis");
        // TC26: Oblique ray starts on axis (1 point)
        result = TUBE.findIntersections(new Ray(new Point(10, 10, 10), new Vector(1, 0, 1)));
        assertEquals(1, result.size(), "BVA: Oblique ray starting on axis");

        // **** Group 4: Ray starts on the surface (Radius distance = 10) ****
        Point pSurf = new Point(2, 16, 10);
        // TC27: Starts on surface, points inside (1 point)
        result = TUBE.findIntersections(new Ray(pSurf, new Vector(1, 0, 0)));
        assertEquals(1, result.size(), "BVA: Surface point pointing inside");
        // TC28: Starts on surface, points outside (0 points)
        assertNull(TUBE.findIntersections(new Ray(pSurf, new Vector(-1, 0, 0))), "BVA: Surface point pointing outside");
        // TC29: Starts on surface, points tangent (0 points)
        assertNull(TUBE.findIntersections(new Ray(pSurf, new Vector(3, 4, 0))), "BVA: Surface point tangent (parallel to axis)");

        // **** Group 5: Tangency (Line touches surface at 1 point) ****
        // TC30: Ray line is tangent, starts before tangent point (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(2, 26, 10), new Vector(1, 0, 0))), "BVA: Tangent line");
        // TC31: Ray line is tangent, starts at tangent point
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 20, 10), new Vector(1, 0, 0))), "BVA: Start at tangent point");

        // **** Group 6: Special Vector Orientations ****
        // TC32: Ray direction is exactly the axis direction (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 10), new Vector(3, 4, 0))), "BVA: Direction same as axis");
        // TC33: Ray direction is exactly opposite the axis direction (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 10), new Vector(-3, -4, 0))), "BVA: Direction opposite axis");

        // **** Group 7: Skew Rays (Misses axis and tube) ****
        // TC34: Ray passes "over" the tube at a distance (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 30), new Vector(1, 0, 0))), "BVA: Skew ray missing tube");
        // TC35: Ray starts outside, direction is perpendicular to the normal at closest point (Tangent-like)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 30, 10), new Vector(1, 0, 0))), "BVA: Orthogonal to normal at distance > R");

        // **** Group 8: Extreme Distances ****
        // TC36: Ray starts very far, crosses tube (2 points)
        result = TUBE.findIntersections(new Ray(new Point(1000, 1000, 1000), new Vector(-1, -1, -1)));
        assertEquals(2, result.size(), "BVA: Far origin crossing tube");

        // **** Group 9: Near-Parallel/Shallow Angles ****
        // TC37: Ray almost parallel to axis, starts inside (1 point)
        result = TUBE.findIntersections(new Ray(new Point(11, 11, 10), new Vector(3, 4, 0.001)));
        assertEquals(1, result.size(), "BVA: Shallow angle from inside");

        // **** Group 10: Point on Axis Origin Plane ****
        // TC38: Ray starts at (10,10,10) pointing perpendicular to axis
        result = TUBE.findIntersections(new Ray(new Point(10, 10, 10), new Vector(-4, 3, 0)));
        assertEquals(1, result.size(), "BVA: Start at axis origin pointing perpendicular");

        // **** Group 11: Corner cases of quadratic solver (Discriminant = 0) ****
        // TC39: Ray hits the tube exactly at a tangent point (handled as 0 points in many implementations)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 20, 0), new Vector(0, 0, 1))), "BVA: Tangent vertical line");

        // TC40: Ray starts inside, points exactly towards the axis
        result = TUBE.findIntersections(new Ray(new Point(13, 14, 10), new Vector(-3, -4, 0)));
        assertEquals(1, result.size(), "BVA: Inside pointing to axis");
    }
}