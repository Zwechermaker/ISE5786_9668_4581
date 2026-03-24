package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Point class.
 */
class PointTest {
    /**
     * Default constructor for Javadoc purposes.
     */
    PointTest() {}

    // ================== Test Constants ==================
    /** A small tolerance for floating-point comparisons. */
    private static final double DELTA = 1E-15;
    /** A point for tests at (1,1,1). */
    private static final Point P1 = new Point(1, 1, 1);
    /** A point for tests at (2,3,4). */
    private static final Point P2 = new Point(2, 3, 4);
    /** A point for tests at (-1,-1,-1). */
    private static final Point P_NEGATIVE = new Point(-1, -1, -1);
    /** A vector for tests to (1,1,1). */
    private static final Vector V1 = new Vector(1, 1, 1);
    /** A vector for tests to (-1,-1,-1). */
    private static final Vector V_NEGATIVE = new Vector(-1, -1, -1);

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        // ================== Equivalence Partitions Tests ==================
        // TC01: Subtracting a point from another point in the same quadrant
        assertEquals(new Vector(1, 2, 3), P2.subtract(P1), "ERROR: subtract() for points in the same quadrant does not work correctly");

        // TC02: Subtracting a point from another point in a different quadrant
        assertEquals(new Vector(2, 2, 2), P1.subtract(P_NEGATIVE), "ERROR: subtract() for points in different quadrants does not work correctly");

        // ================== Boundary Values Tests ==================
        // TC03: Subtracting a point from itself
        assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1), "ERROR: subtract() of a point from itself should throw an exception");

        // TC04: Subtracting the origin from a point
        assertEquals(V1, P1.subtract(Point.ZERO), "ERROR: subtract() of the origin from a point does not work correctly");

        // TC05: Subtracting a point from the origin
        assertEquals(V_NEGATIVE, Point.ZERO.subtract(P1), "ERROR: subtract() of a point from the origin does not work correctly");

        // TC06: Subtracting points on the same axis
        Point p_x1 = new Point(1, 0, 0);
        Point p_x2 = new Point(5, 0, 0);
        assertEquals(new Vector(4, 0, 0), p_x2.subtract(p_x1), "ERROR: subtract() for points on the same axis does not work correctly");
    }

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ================== Equivalence Partitions Tests ==================
        // TC07: Adding a vector to a point
        assertEquals(P2, P1.add(new Vector(1, 2, 3)), "ERROR: add() for a vector to a point does not work correctly");

        // ================== Boundary Values Tests ==================
        // TC08: Adding a vector to a point to get the origin
        assertEquals(Point.ZERO, P1.add(V_NEGATIVE), "ERROR: add() a vector to a point resulting in the origin does not work correctly");

        // TC09: Adding a vector to the origin
        assertEquals(P1, Point.ZERO.add(V1), "ERROR: add() a vector to the origin point does not work correctly");
    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void testDistanceSquared() {
        // ================== Equivalence Partitions Tests ==================
        // TC10: General case
        assertEquals(14, P1.distanceSquared(P2), DELTA, "ERROR: distanceSquared() returns wrong value");

        // ================== Boundary Values Tests ==================
        // TC11: Distance to self
        assertEquals(0, P1.distanceSquared(P1), DELTA, "ERROR: distanceSquared() between a point and itself should be 0");

        // TC12: Symmetry check
        assertEquals(P1.distanceSquared(P2), P2.distanceSquared(P1), DELTA, "ERROR: distanceSquared() is not symmetric");
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {
        // ================== Equivalence Partitions Tests ==================
        // TC13: General case
        assertEquals(Math.sqrt(14), P1.distance(P2), DELTA, "ERROR: distance() returns wrong value");

        // ================== Boundary Values Tests ==================
        // TC14: Distance to self
        assertEquals(0, P1.distance(P1), DELTA, "ERROR: distance() between a point and itself should be 0");

        // TC15: Symmetry check
        assertEquals(P1.distance(P2), P2.distance(P1), DELTA, "ERROR: distance() is not symmetric");

        // TC16: Consistency with distanceSquared
        assertEquals(P1.distance(P2), Math.sqrt(P1.distanceSquared(P2)), DELTA, "ERROR: distance() and distanceSquared() are not consistent");
    }
}
