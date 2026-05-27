package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import geometries.api.Intersectable.Intersection;

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

    /** Error message for wrong number of points */
    private static final String WRONG_PTS = "Wrong number of points";
    /** Error message for wrong intersection points */
    private static final String WRONG_INT = "Ray crosses tube returning wrong points";

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

        // ============ Group 1: Orthogonal Rays Intersecting the Axis ==================
        Vector vZ = new Vector(0, 0, 1);
        Point pAxisOut1 = new Point(10, 10, 0);
        Point pAxisOut2 = new Point(10, 10, 20);

        // TC07: Starts outside, goes through axis (2 points)
        result = TUBE.findIntersections(new Ray(new Point(10, 10, -5), vZ));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(pAxisOut1) && result.contains(pAxisOut2), WRONG_INT);

        // TC08: Starts exactly on surface, goes in (1 point)
        result = TUBE.findIntersections(new Ray(pAxisOut1, vZ));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pAxisOut2), result, WRONG_INT);

        // TC09: Starts strictly inside, goes out (1 point)
        result = TUBE.findIntersections(new Ray(new Point(10, 10, 5), vZ));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pAxisOut2), result, WRONG_INT);

        // TC10: Starts exactly on axis, goes out (1 point)
        result = TUBE.findIntersections(new Ray(new Point(10, 10, 10), vZ));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pAxisOut2), result, WRONG_INT);

        // TC11: Starts between axis and far surface (1 point)
        result = TUBE.findIntersections(new Ray(new Point(10, 10, 15), vZ));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pAxisOut2), result, WRONG_INT);

        // TC12: Starts exactly on far surface, goes out (0 points)
        assertNull(TUBE.findIntersections(new Ray(pAxisOut2, vZ)), WRONG_PTS);

        // TC13: Starts outside, goes away (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 25), vZ)), WRONG_PTS);


        // ============ Group 2: Orthogonal Rays Secant (Misses Axis) ===================
        Point pSec1 = new Point(1, 8, 2);
        Point pSec2 = new Point(1, 8, 18);

        // TC14: Starts outside, goes through (2 points)
        result = TUBE.findIntersections(new Ray(new Point(1, 8, -5), vZ));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(pSec1) && result.contains(pSec2), WRONG_INT);

        // TC15: Starts on surface, goes in (1 point)
        result = TUBE.findIntersections(new Ray(pSec1, vZ));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pSec2), result, WRONG_INT);

        // TC16: Starts inside, goes out (1 point)
        result = TUBE.findIntersections(new Ray(new Point(1, 8, 10), vZ));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pSec2), result, WRONG_INT);

        // TC17: Starts on far surface, goes out (0 points)
        assertNull(TUBE.findIntersections(new Ray(pSec2, vZ)), WRONG_PTS);

        // TC18: Starts outside, goes away (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(1, 8, 25), vZ)), WRONG_PTS);


        // ============ Group 3: Orthogonal Rays Tangent to the Tube ====================
        // TC19: Starts before tangent point (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(-1, 12, 5), vZ)), WRONG_PTS);

        // TC20: Starts exactly at tangent point (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(-1, 12, 10), vZ)), WRONG_PTS);

        // TC21: Starts after tangent point (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(-1, 12, 15), vZ)), WRONG_PTS);


        // ============ Group 4: Orthogonal Rays Missing Completely =====================
        // TC22: Ray closest approach ahead (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(-4, 12, 5), vZ)), WRONG_PTS);

        // TC23: Ray closest approach behind (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(-4, 12, 15), vZ)), WRONG_PTS);


        // ============ Group 5: Slanted Rays Intersecting the Axis =====================
        Vector vSlanted = new Vector(3, 4, 5);
        Point pSlanted1 = new Point(4, 2, 0);
        Point pSlanted2 = new Point(16, 18, 20);

        // TC24: Starts outside, goes through (2 points)
        result = TUBE.findIntersections(new Ray(new Point(1, -2, -5), vSlanted));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(pSlanted1) && result.contains(pSlanted2), WRONG_INT);

        // TC25: Starts exactly on surface, goes in (1 point)
        result = TUBE.findIntersections(new Ray(pSlanted1, vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pSlanted2), result, WRONG_INT);

        // TC26: Starts strictly inside, goes out (1 point)
        result = TUBE.findIntersections(new Ray(new Point(7, 6, 5), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pSlanted2), result, WRONG_INT);

        // TC27: Starts exactly on axis, goes out (1 point)
        result = TUBE.findIntersections(new Ray(new Point(10, 10, 10), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pSlanted2), result, WRONG_INT);

        // TC28: Starts between axis and far surface (1 point)
        result = TUBE.findIntersections(new Ray(new Point(13, 14, 15), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pSlanted2), result, WRONG_INT);

        // TC29: Starts exactly on far surface, goes out (0 points)
        assertNull(TUBE.findIntersections(new Ray(pSlanted2, vSlanted)), WRONG_PTS);

        // TC30: Starts outside, goes away (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(19, 22, 25), vSlanted)), WRONG_PTS);


        // ============ Group 6: Parallel Rays (0 Degrees) ==============================
        Vector vParallel = new Vector(3, 4, 0);

        // TC31: Strictly inside (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 15), vParallel)), WRONG_PTS);

        // TC32: Exactly on the axis (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 10), vParallel)), WRONG_PTS);

        // TC33: Exactly on the surface (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 20), vParallel)), WRONG_PTS);

        // TC34: Strictly outside (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 25), vParallel)), WRONG_PTS);


        // ============ Group 7: Anti-Parallel Rays (180 Degrees) =======================
        Vector vAntiParallel = new Vector(-3, -4, 0);

        // TC35: Strictly inside (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 15), vAntiParallel)), WRONG_PTS);

        // TC36: Exactly on the axis (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 10), vAntiParallel)), WRONG_PTS);

        // TC37: Exactly on the surface (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 20), vAntiParallel)), WRONG_PTS);

        // TC38: Strictly outside (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(10, 10, 25), vAntiParallel)), WRONG_PTS);


        // ============ Group 8: Slanted Rays Secant (Misses Axis) ======================
        Point pSlantedSec1 = new Point(10, 0, 2);
        Point pSlantedSec2 = new Point(19.6, 12.8, 18);

        // TC39: Starts outside, goes through (2 points)
        result = TUBE.findIntersections(new Ray(new Point(7, -4, -3), vSlanted));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(pSlantedSec1) && result.contains(pSlantedSec2), WRONG_INT);

        // TC40: Starts on surface, goes in (1 point)
        result = TUBE.findIntersections(new Ray(pSlantedSec1, vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pSlantedSec2), result, WRONG_INT);

        // TC41: Starts inside, goes out (1 point)
        result = TUBE.findIntersections(new Ray(new Point(13, 4, 7), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(pSlantedSec2), result, WRONG_INT);

        // TC42: Starts exactly on far surface, goes out (0 points)
        assertNull(TUBE.findIntersections(new Ray(pSlantedSec2, vSlanted)), WRONG_PTS);

        // TC43: Starts outside, goes away (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(22.6, 16.8, 23), vSlanted)), WRONG_PTS);

        // ============ Group 9: Slanted Rays Missing Completely ========================
        // TC44: Ray closest approach ahead (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(-2, 19, 5), vSlanted)), WRONG_PTS);

        // TC45: Ray closest approach at origin (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(-2, 19, 10), vSlanted)), WRONG_PTS);

        // TC46: Ray closest approach behind (0 points)
        assertNull(TUBE.findIntersections(new Ray(new Point(-2, 19, 15), vSlanted)), WRONG_PTS);
    }

    /**
     * Test method for {@link geometries.impl.Tube#calcIntersections(primitives.Ray)}.
     */
    @Test
    void testCalcIntersections() {
        // ============ Equivalence Partitions Tests ==============
        // TC47: Ray crosses the tube (2 points)
        Ray ray = new Ray(new Point(10, 10, -5), new Vector(0, 0, 1));
        List<Intersection> result = TUBE.calcIntersections(ray);
        assertEquals(2, result.size(), "Wrong number of intersections");
        assertSame(TUBE, result.get(0).geometry, "Intersection does not belong to the correct geometry");
        assertSame(TUBE, result.get(1).geometry, "Intersection does not belong to the correct geometry");

        // TC48: Ray starts inside the tube (1 point)
        result = TUBE.calcIntersections(new Ray(new Point(10, 10, 5), new Vector(0, 0, 1)));
        assertEquals(1, result.size(), "Wrong number of intersections");
        assertSame(TUBE, result.get(0).geometry, "Intersection does not belong to the correct geometry");

        // TC49: Ray is parallel to the tube (0 points)
        assertNull(TUBE.calcIntersections(new Ray(new Point(10, 10, 15), new Vector(3, 4, 0))),
                "Ray parallel to tube should return null");

        // =============== Boundary Values Tests (maxDistance) ==================
        // Based on the ray from TC47 which intersects at dist=5 and dist=25

        // TC50: maxDistance is smaller than the first intersection (0 points)
        assertNull(TUBE.calcIntersections(ray, 2), "maxDistance smaller than first intersection should return null");

        // TC51: maxDistance is between the two intersections (1 point)
        result = TUBE.calcIntersections(ray, 15);
        assertEquals(1, result.size(), "maxDistance between intersections should return 1 point");

        // TC52: maxDistance is larger than both intersections (2 points)
        result = TUBE.calcIntersections(ray, 30);
        assertEquals(2, result.size(), "maxDistance larger than both should return 2 points");

        // TC53: maxDistance is exactly at the first intersection - BVA (1 point)
        result = TUBE.calcIntersections(ray, 5);
        assertEquals(1, result.size(), "maxDistance exactly on first intersection should return 1 point");
    }
}