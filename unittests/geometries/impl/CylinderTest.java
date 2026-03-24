package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
    /** A cylinder for testing. */
    private static final Cylinder CYLINDER = new Cylinder(10, AXIS_RAY, 20);
    /** The normalized direction vector of the axis ray. */
    private static final Vector AXIS_DIRECTION = AXIS_RAY.direction();
    /** A vector perpendicular to the cylinder's axis, used for calculating expected normals. */
    private static final Vector PERP_VEC = new Vector(-0.8, 0.6, 0);
    /** The negated direction vector of the axis ray, for bottom base normal checks. */
    private static final Vector NEGATIVE_AXIS_DIRECTION = AXIS_DIRECTION.scale(-1);

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
}
