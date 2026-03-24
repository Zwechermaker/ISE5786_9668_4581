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
    /** A small tolerance for floating-point comparisons */
    private static final double DELTA = 1E-15;

    // ================== Test Points ==================
    /** A point for tests at (1,1,1) */
    private static final Point P1 = new Point(1, 1, 1);
    /** A point for tests at (2,3,4) */
    private static final Point P2 = new Point(2, 3, 4);
    /** A point for tests at (-1,-1,-1) */
    private static final Point P_NEGATIVE = new Point(-1, -1, -1);

    // ================== Test Vectors ==================
    /** A vector for tests to (1,1,1) */
    private static final Vector V1 = new Vector(1, 1, 1);
    /** A vector for tests to (-1,-1,-1) */
    private static final Vector V_NEGATIVE = new Vector(-1, -1, -1);

    // ================== Error Messages ==================
    private static final String ERROR_SUBTRACT_SAME_QUADRANT = "ERROR: subtract() for points in the same quadrant does not work correctly";
    private static final String ERROR_SUBTRACT_DIFFERENT_QUADRANTS = "ERROR: subtract() for points in different quadrants does not work correctly";
    private static final String ERROR_SUBTRACT_SELF = "ERROR: subtract() of a point from itself should throw an exception";
    private static final String ERROR_SUBTRACT_FROM_ORIGIN = "ERROR: subtract() of a point from the origin does not work correctly";
    private static final String ERROR_SUBTRACT_ORIGIN = "ERROR: subtract() of the origin from a point does not work correctly";
    private static final String ERROR_SUBTRACT_ON_AXIS = "ERROR: subtract() for points on the same axis does not work correctly";
    private static final String ERROR_ADD_VECTOR = "ERROR: add() for a vector to a point does not work correctly";
    private static final String ERROR_ADD_TO_ORIGIN = "ERROR: add() a vector to the origin point does not work correctly";
    private static final String ERROR_ADD_RESULTING_IN_ORIGIN = "ERROR: add() a vector to a point resulting in the origin does not work correctly";
    private static final String ERROR_DISTANCE_SQ_SELF = "ERROR: distanceSquared() between a point and itself should be 0";
    private static final String ERROR_DISTANCE_SQ_GENERAL = "ERROR: distanceSquared() returns wrong value";
    private static final String ERROR_DISTANCE_SQ_SYMMETRY = "ERROR: distanceSquared() is not symmetric";
    private static final String ERROR_DISTANCE_SELF = "ERROR: distance() between a point and itself should be 0";
    private static final String ERROR_DISTANCE_GENERAL = "ERROR: distance() returns wrong value";
    private static final String ERROR_DISTANCE_SYMMETRY = "ERROR: distance() is not symmetric";
    private static final String ERROR_DISTANCE_CONSISTENCY = "ERROR: distance() and distanceSquared() are not consistent";


    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: Subtracting a point from another point in the same quadrant
        assertEquals(new Vector(1, 2, 3), P2.subtract(P1), ERROR_SUBTRACT_SAME_QUADRANT);

        // EP02: Subtracting a point from another point in a different quadrant
        assertEquals(new Vector(2, 2, 2), P1.subtract(P_NEGATIVE), ERROR_SUBTRACT_DIFFERENT_QUADRANTS);

        // ================== Boundary Values Tests ==================
        // BVA01: Subtracting a point from itself
        assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1), ERROR_SUBTRACT_SELF);

        // BVA02: Subtracting the origin from a point
        assertEquals(V1, P1.subtract(Point.ZERO), ERROR_SUBTRACT_ORIGIN);

        // BVA03: Subtracting a point from the origin
        assertEquals(V_NEGATIVE, Point.ZERO.subtract(P1), ERROR_SUBTRACT_FROM_ORIGIN);

        // BVA04: Subtracting points on the same axis
        Point p_x1 = new Point(1, 0, 0);
        Point p_x2 = new Point(5, 0, 0);
        assertEquals(new Vector(4, 0, 0), p_x2.subtract(p_x1), ERROR_SUBTRACT_ON_AXIS);
    }

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: Adding a vector to a point
        assertEquals(P2, P1.add(new Vector(1, 2, 3)), ERROR_ADD_VECTOR);

        // ================== Boundary Values Tests ==================
        // BVA01: Adding a vector to a point to get the origin
        assertEquals(Point.ZERO, P1.add(V_NEGATIVE), ERROR_ADD_RESULTING_IN_ORIGIN);

        // BVA02: Adding a vector to the origin
        assertEquals(P1, Point.ZERO.add(V1), ERROR_ADD_TO_ORIGIN);
    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void testDistanceSquared() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: General case
        assertEquals(14, P1.distanceSquared(P2), DELTA, ERROR_DISTANCE_SQ_GENERAL);

        // ================== Boundary Values Tests ==================
        // BVA01: Distance to self
        assertEquals(0, P1.distanceSquared(P1), DELTA, ERROR_DISTANCE_SQ_SELF);

        // BVA02: Symmetry check
        assertEquals(P1.distanceSquared(P2), P2.distanceSquared(P1), DELTA, ERROR_DISTANCE_SQ_SYMMETRY);
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: General case
        assertEquals(Math.sqrt(14), P1.distance(P2), DELTA, ERROR_DISTANCE_GENERAL);

        // ================== Boundary Values Tests ==================
        // BVA01: Distance to self
        assertEquals(0, P1.distance(P1), DELTA, ERROR_DISTANCE_SELF);

        // BVA02: Symmetry check
        assertEquals(P1.distance(P2), P2.distance(P1), DELTA, ERROR_DISTANCE_SYMMETRY);

        // BVA03: Consistency with distanceSquared
        assertEquals(P1.distance(P2), Math.sqrt(P1.distanceSquared(P2)), DELTA, ERROR_DISTANCE_CONSISTENCY);
    }
}
