package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import geometries.api.Intersectable.Intersection;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Cylinder class.
 */
class CylinderTest {
    /**
     * Default constructor for Javadoc purposes.
     */
    CylinderTest() {}

    // ================== Test Constants ==================
    /** The center point of the cylinder's bottom base. */
    private static final Point BOTTOM_BASE_CENTER = new Point(10, 10, 10);
    /** An axis ray for the cylinder used in tests. */
    private static final Ray AXIS_RAY = new Ray(BOTTOM_BASE_CENTER, new Vector(3, 4, 0));
    /** A cylinder for testing normal and constructor logic. */
    private static final Cylinder CYLINDER = new Cylinder(10, AXIS_RAY, 20);
    /** The normalized direction vector of the axis ray. */
    private static final Vector AXIS_DIRECTION = AXIS_RAY.direction();
    /** A vector perpendicular to the cylinder's axis, used for calculating expected normals. */
    private static final Vector PERP_VEC = new Vector(-0.8, 0.6, 0);
    /** The negated direction vector of the axis ray, for bottom base normal checks. */
    private static final Vector NEGATIVE_AXIS_DIRECTION = AXIS_DIRECTION.scale(-1);

    /** Error message for incorrect number of intersection points */
    private static final String WRONG_PTS = "Wrong number of points";
    /** Error message for incorrect intersection values */
    private static final String WRONG_INT = "Ray crosses cylinder returning wrong points";

    /** Vector pointing straight UP the Z-axis (used in intersection tests) */
    private static final Vector V_UP = new Vector(0, 0, 1);
    /** Vector pointing straight DOWN the Z-axis (used in intersection tests) */
    private static final Vector V_DOWN = new Vector(0, 0, -1);


    /**
     * Test method for {@link geometries.impl.Cylinder#Cylinder(double, Ray, double)}.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Correct cylinder constructor
        assertDoesNotThrow(() -> new Cylinder(10, AXIS_RAY, 20), "Failed constructing a correct cylinder");

        // =============== Boundary Values Tests ==================
        // TC02: Zero radius
        assertThrows(IllegalArgumentException.class, () -> new Cylinder(0, AXIS_RAY, 20),
                "Constructed a cylinder with zero radius");
        // TC03: Negative radius
        assertThrows(IllegalArgumentException.class, () -> new Cylinder(-5, AXIS_RAY, 20),
                "Constructed a cylinder with a negative radius");
        // TC04: Zero height
        assertThrows(IllegalArgumentException.class, () -> new Cylinder(10, AXIS_RAY, 0),
                "Constructed a cylinder with zero height");
        // TC05: Negative height
        assertThrows(IllegalArgumentException.class, () -> new Cylinder(10, AXIS_RAY, -10),
                "Constructed a cylinder with a negative height");
    }

    /**
     * Test method for {@link geometries.impl.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC06: Point on the round side surface
        assertEquals(PERP_VEC, CYLINDER.getNormal(new Point(8, 24, 10)), "getNormal() for point on side surface is wrong");

        // TC07: Point on the bottom base
        assertEquals(NEGATIVE_AXIS_DIRECTION, CYLINDER.getNormal(new Point(6, 13, 10)), "getNormal() for point on bottom base is wrong");

        // TC08: Point on the top base
        assertEquals(AXIS_DIRECTION, CYLINDER.getNormal(new Point(18, 29, 10)), "getNormal() for point on top base is wrong");

        // =============== Boundary Values Tests ==================
        // TC09: Center of the bottom base
        assertEquals(NEGATIVE_AXIS_DIRECTION, CYLINDER.getNormal(BOTTOM_BASE_CENTER), "getNormal() for center of bottom base is wrong");

        // TC10: Center of the top base
        assertEquals(AXIS_DIRECTION, CYLINDER.getNormal(new Point(22, 26, 10)), "getNormal() for center of top base is wrong");

        // TC11: Point on the rim of the bottom base
        assertEquals(NEGATIVE_AXIS_DIRECTION, CYLINDER.getNormal(new Point(2, 16, 10)), "getNormal() for point on bottom rim is wrong (should be like side surface)");

        // TC12: Point on the rim of the top base
        assertEquals(AXIS_DIRECTION, CYLINDER.getNormal(new Point(14, 32, 10)), "getNormal() for point on top rim is wrong (should be like side surface)");
    }

    /**
     * Test method for {@link geometries.impl.Cylinder#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // Local upright cylinder for clean intersection coordinates (r=5, h=20)
        Cylinder cylInt = new Cylinder(5.0, new Ray(new Point(0, 0, 0), V_UP), 20.0);
        List<Point> result;

        // ==== Group 1: Orthogonal Rays Intersecting the Axis (z=10, half-height) ======
        Vector vOrthogonal = new Vector(1, 0, 0);

        // TC13: Starts outside, goes through axis (2 points)
        result = cylInt.findIntersections(new Ray(new Point(-10, 0, 10), vOrthogonal));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(-5, 0, 10)) && result.contains(new Point(5, 0, 10)), WRONG_INT);

        // TC14: Starts exactly on surface, goes in (1 point)
        result = cylInt.findIntersections(new Ray(new Point(-5, 0, 10), vOrthogonal));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 10)), result, WRONG_INT);

        // TC15: Starts strictly inside, goes out (1 point)
        result = cylInt.findIntersections(new Ray(new Point(-2, 0, 10), vOrthogonal));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 10)), result, WRONG_INT);

        // TC16: Starts exactly on axis, goes out (1 point)
        result = cylInt.findIntersections(new Ray(new Point(0, 0, 10), vOrthogonal));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 10)), result, WRONG_INT);

        // TC17: Starts between axis and far surface (1 point)
        result = cylInt.findIntersections(new Ray(new Point(3, 0, 10), vOrthogonal));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 10)), result, WRONG_INT);

        // TC18: Starts exactly on far surface, goes out (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(5, 0, 10), vOrthogonal)), WRONG_PTS);

        // TC19: Starts outside, goes away (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 0, 10), vOrthogonal)), WRONG_PTS);

        // ==== Group 2: Orthogonal Rays Secant (Misses Axis) (z=10, y=3) ===============
        // TC20: Starts outside, goes through (2 points)
        result = cylInt.findIntersections(new Ray(new Point(-10, 3, 10), vOrthogonal));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(-4, 3, 10)) && result.contains(new Point(4, 3, 10)), WRONG_INT);

        // TC21: Starts on surface, goes in (1 point)
        result = cylInt.findIntersections(new Ray(new Point(-4, 3, 10), vOrthogonal));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(4, 3, 10)), result, WRONG_INT);

        // TC22: Starts inside, goes out (1 point)
        result = cylInt.findIntersections(new Ray(new Point(0, 3, 10), vOrthogonal));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(4, 3, 10)), result, WRONG_INT);

        // TC23: Starts on far surface, goes out (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(4, 3, 10), vOrthogonal)), WRONG_PTS);

        // TC24: Starts outside, goes away (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 3, 10), vOrthogonal)), WRONG_PTS);

        // ==== Group 3: Orthogonal Rays Tangent to the Cylinder (z=10, y=5) ============
        // TC25: Starts before tangent point (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(-10, 5, 10), vOrthogonal)), WRONG_PTS);

        // TC26: Starts exactly at tangent point (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(0, 5, 10), vOrthogonal)), WRONG_PTS);

        // TC27: Starts after tangent point (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 5, 10), vOrthogonal)), WRONG_PTS);

        // ==== Group 4: Orthogonal Rays Missing Completely (z=10, y=6) =================
        // TC28: Ray closest approach ahead (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(-10, 6, 10), vOrthogonal)), WRONG_PTS);

        // TC29: Ray closest approach behind (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 6, 10), new Vector(-1, 0, 0))), WRONG_PTS);

        // ==== Group 5: Slanted Rays Intersecting the Axis =============================
        Vector vSlanted = new Vector(1, 0, 1); // Hits side envelope at (-5,0,5) and (5,0,15)

        // TC30: Starts outside, goes through (2 points)
        result = cylInt.findIntersections(new Ray(new Point(-10, 0, 0), vSlanted));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(-5, 0, 5)) && result.contains(new Point(5, 0, 15)), WRONG_INT);

        // TC31: Starts exactly on surface, goes in (1 point)
        result = cylInt.findIntersections(new Ray(new Point(-5, 0, 5), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 15)), result, WRONG_INT);

        // TC32: Starts strictly inside, goes out (1 point)
        result = cylInt.findIntersections(new Ray(new Point(-2, 0, 8), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 15)), result, WRONG_INT);

        // TC33: Starts exactly on axis, goes out (1 point)
        result = cylInt.findIntersections(new Ray(new Point(0, 0, 10), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 15)), result, WRONG_INT);

        // TC34: Starts between axis and far surface (1 point)
        result = cylInt.findIntersections(new Ray(new Point(3, 0, 13), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 15)), result, WRONG_INT);

        // TC35: Starts exactly on far surface, goes out (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(5, 0, 15), vSlanted)), WRONG_PTS);

        // TC36: Starts outside, goes away (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 0, 20), vSlanted)), WRONG_PTS);

        // ==== Group 6: Parallel Rays (0 Degrees - Adjusted to hit caps) ===============
        // TC37: Strictly inside, hits both caps (2 points)
        result = cylInt.findIntersections(new Ray(new Point(3, 0, -5), V_UP));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(3, 0, 0)) && result.contains(new Point(3, 0, 20)), WRONG_INT);

        // TC38: Exactly on the axis, hits both cap centers (2 points)
        result = cylInt.findIntersections(new Ray(new Point(0, 0, -5), V_UP));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(0, 0, 0)) && result.contains(new Point(0, 0, 20)), WRONG_INT);

        // TC39: Exactly on the surface (0 points - tangent line ignored)
        assertNull(cylInt.findIntersections(new Ray(new Point(5, 0, -5), V_UP)), WRONG_PTS);

        // TC40: Strictly outside (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 0, -5), V_UP)), WRONG_PTS);

        // ==== Group 7: Anti-Parallel Rays (180 Degrees - Adjusted to hit caps) ========
        // TC41: Strictly inside, hits both caps (2 points)
        result = cylInt.findIntersections(new Ray(new Point(3, 0, 25), V_DOWN));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(3, 0, 20)) && result.contains(new Point(3, 0, 0)), WRONG_INT);

        // TC42: Exactly on the axis, hits both cap centers (2 points)
        result = cylInt.findIntersections(new Ray(new Point(0, 0, 25), V_DOWN));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(0, 0, 20)) && result.contains(new Point(0, 0, 0)), WRONG_INT);

        // TC43: Exactly on the surface (0 points - tangent line ignored)
        assertNull(cylInt.findIntersections(new Ray(new Point(5, 0, 25), V_DOWN)), WRONG_PTS);

        // TC44: Strictly outside (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 0, 25), V_DOWN)), WRONG_PTS);

        // ==== Group 8: Slanted Rays Secant (Misses Axis) (y=3) ========================
        // TC45: Starts outside, goes through (2 points)
        result = cylInt.findIntersections(new Ray(new Point(-10, 3, 0), vSlanted));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(-4, 3, 6)) && result.contains(new Point(4, 3, 14)), WRONG_INT);

        // TC46: Starts on surface, goes in (1 point)
        result = cylInt.findIntersections(new Ray(new Point(-4, 3, 6), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(4, 3, 14)), result, WRONG_INT);

        // TC47: Starts inside, goes out (1 point)
        result = cylInt.findIntersections(new Ray(new Point(0, 3, 10), vSlanted));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(4, 3, 14)), result, WRONG_INT);

        // TC48: Starts exactly on far surface, goes out (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(4, 3, 14), vSlanted)), WRONG_PTS);

        // TC49: Starts outside, goes away (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 3, 20), vSlanted)), WRONG_PTS);

        // ==== Group 9: Slanted Rays Missing Completely (y=6) ==========================
        // TC50: Ray closest approach ahead (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(-10, 6, 0), vSlanted)), WRONG_PTS);

        // TC51: Ray closest approach at origin (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(0, 6, 10), vSlanted)), WRONG_PTS);

        // TC52: Ray closest approach behind (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(10, 6, 20), vSlanted)), WRONG_PTS);

        // ==== Group 10: Cylinder Cap Intersections ====================================
        // TC53: Slanted entering bottom cap, exiting top cap (2 points)
        result = cylInt.findIntersections(new Ray(new Point(0, 0, -5), new Vector(0, 0.1, 1)));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(0, 0.5, 0)) && result.contains(new Point(0, 2.5, 20)), WRONG_INT);

        // TC54: Slanted entering bottom cap, exiting side (2 points)
        result = cylInt.findIntersections(new Ray(new Point(0, -3, -5), new Vector(0, 1, 1)));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(0, 2, 0)) && result.contains(new Point(0, 5, 3)), WRONG_INT);

        // TC55: Slanted entering side, exiting top cap (2 points)
        result = cylInt.findIntersections(new Ray(new Point(0, -10, 5), new Vector(0, 1, 1)));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(0, -5, 10)) && result.contains(new Point(0, 5, 20)), WRONG_INT);

        // ==== Group 11: Corner Boundaries =============================================
        // TC56: Slanted piercing exactly through bottom corner and side (2 points)
        result = cylInt.findIntersections(new Ray(new Point(0, -10, -5), new Vector(0, 1, 1)));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(0, -5, 0)) && result.contains(new Point(0, 5, 10)), WRONG_INT);

        // TC57: Slanted crossing diagonal from bottom corner to top opposite corner (2 points)
        result = cylInt.findIntersections(new Ray(new Point(-10, 0, -10), new Vector(1, 0, 2)));
        assertEquals(2, result.size(), WRONG_PTS);
        assertTrue(result.contains(new Point(-5, 0, 0)) && result.contains(new Point(5, 0, 20)), WRONG_INT);

        // TC58: Ray starts exactly ON the bottom corner, enters cylinder (1 point)
        result = cylInt.findIntersections(new Ray(new Point(-5, 0, 0), new Vector(1, 0, 2)));
        assertEquals(1, result.size(), WRONG_PTS);
        assertEquals(List.of(new Point(5, 0, 20)), result, WRONG_INT);

        // TC59: Ray starts exactly ON the top corner, goes OUT of cylinder (0 points)
        assertNull(cylInt.findIntersections(new Ray(new Point(5, 0, 20), new Vector(1, 0, 2))), WRONG_PTS);
    }

    /**
     * Test method for {@link geometries.impl.Cylinder#calcIntersections(primitives.Ray)}.
     */
    @Test
    void testCalcIntersections() {
        Cylinder cylInt = new Cylinder(5.0, new Ray(new Point(0, 0, 0), V_UP), 20.0);

        // TC60: Ray intersects the bottom base
        List<Intersection> result = cylInt.calcIntersections(new Ray(new Point(0, 0, -5), V_UP));
        assertEquals(2, result.size(), "Wrong number of intersections for bottom base");
        assertSame(cylInt, result.get(0).geometry, "Intersection does not belong to the correct geometry");

        // TC61: Ray intersects the top base
        result = cylInt.calcIntersections(new Ray(new Point(0, 0, 25), V_DOWN));
        assertEquals(2, result.size(), "Wrong number of intersections for top base");
        assertSame(cylInt, result.get(0).geometry, "Intersection does not belong to the correct geometry");

        // TC62: Ray intersects the side
        result = cylInt.calcIntersections(new Ray(new Point(-10, 0, 10), new Vector(1, 0, 0)));
        assertEquals(2, result.size(), "Wrong number of intersections for side");
        assertSame(cylInt, result.get(0).geometry, "Intersection does not belong to the correct geometry");
        assertSame(cylInt, result.get(1).geometry, "Intersection does not belong to the correct geometry");

        // TC63: Ray does not intersect the cylinder
        assertNull(cylInt.calcIntersections(new Ray(new Point(10, 10, 10), new Vector(1, 0, 0))),
                "Ray parallel to and outside cylinder should return null");
    }
}
