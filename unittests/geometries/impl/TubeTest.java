package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
    /** An axis ray for the tube used in tests. */
    private static final Ray AXIS_RAY = new Ray(new Point(10, 10, 10), new Vector(3, 4, 0));
    /** A tube for testing. */
    private static final Tube TUBE = new Tube(10, AXIS_RAY);
    /** A vector perpendicular to the tube's axis, used for calculating expected normals. */
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
}
