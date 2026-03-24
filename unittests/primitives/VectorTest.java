package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Vector class.
 */
class VectorTest {
    /**
     * Default constructor for Javadoc purposes.
     */
    VectorTest() {}

    // ================== Test Constants ==================
    /** A small tolerance for floating-point comparisons */
    private static final double DELTA = 1E-15;

    // ================== Test Vectors ==================
    /** A vector for tests to (1,1,1) */
    private static final Vector V1 = new Vector(1, 1, 1);
    /** A vector for tests to (2,3,4) */
    private static final Vector V2 = new Vector(2, 3, 4);
    /** A vector for tests to (-1,-1,-1) */
    private static final Vector V_NEGATIVE = new Vector(-1, -1, -1);
    /** An orthogonal vector for tests */
    private static final Vector V_ORTHOGONAL = new Vector(1, -2, 1);

    // ================== Error Messages ==================
    private static final String ERROR_CONSTRUCTOR_ZERO = "ERROR: Vector(0,0,0) should throw an exception";
    private static final String ERROR_CONSTRUCTOR_ZERO_DOUBLE3 = "ERROR: Vector(Double3.ZERO) should throw an exception";
    private static final String ERROR_CONSTRUCTOR_NEAR_ZERO = "ERROR: Vector with near-zero components should throw an exception";
    private static final String ERROR_ADD_GENERAL = "ERROR: add() for two vectors does not work correctly";
    private static final String ERROR_ADD_ZERO = "ERROR: add() with a vector and its opposite should throw an exception";
    private static final String ERROR_SUBTRACT_GENERAL = "ERROR: subtract() for two vectors does not work correctly";
    private static final String ERROR_SUBTRACT_ZERO = "ERROR: subtract() with a vector from itself should throw an exception";
    private static final String ERROR_SCALE_POSITIVE = "ERROR: scale() with a positive number does not work correctly";
    private static final String ERROR_SCALE_NEGATIVE = "ERROR: scale() with a negative number does not work correctly";
    private static final String ERROR_SCALE_ONE = "ERROR: scale() with 1 does not return an equal vector";
    private static final String ERROR_SCALE_MINUS_ONE = "ERROR: scale() with -1 does not return the opposite vector";
    private static final String ERROR_SCALE_ZERO = "ERROR: scale() with 0 should throw an exception";
    private static final String ERROR_DOT_PRODUCT_GENERAL = "ERROR: dotProduct() returns wrong value";
    private static final String ERROR_DOT_PRODUCT_ORTHOGONAL = "ERROR: dotProduct() for orthogonal vectors is not zero";
    private static final String ERROR_DOT_PRODUCT_SELF = "ERROR: dotProduct() with self does not equal lengthSquared()";
    private static final String ERROR_DOT_PRODUCT_COMMUTATIVE = "ERROR: dotProduct() is not commutative";
    private static final String ERROR_CROSS_PRODUCT_GENERAL = "ERROR: crossProduct() returns wrong value";
    private static final String ERROR_CROSS_PRODUCT_PARALLEL = "ERROR: crossProduct() for parallel vectors should throw an exception";
    private static final String ERROR_CROSS_PRODUCT_ORTHOGONALITY = "ERROR: crossProduct() result is not orthogonal to its operands";
    private static final String ERROR_CROSS_PRODUCT_ANTI_COMMUTATIVE = "ERROR: crossProduct() is not anti-commutative";
    private static final String ERROR_LENGTH_SQ_GENERAL = "ERROR: lengthSquared() returns wrong value";
    private static final String ERROR_LENGTH_SQ_UNIT = "ERROR: lengthSquared() for a unit vector should be 1";
    private static final String ERROR_LENGTH_GENERAL = "ERROR: length() returns wrong value";
    private static final String ERROR_LENGTH_UNIT = "ERROR: length() for a unit vector should be 1";
    private static final String ERROR_LENGTH_CONSISTENCY = "ERROR: length() and lengthSquared() are not consistent";
    private static final String ERROR_NORMALIZE_LENGTH = "ERROR: normalize() does not create a unit vector";
    private static final String ERROR_NORMALIZE_DIRECTION = "ERROR: normalize() changes the vector's direction";
    private static final String ERROR_NORMALIZE_OPPOSITE_DIRECTION = "ERROR: normalize() creates a vector in the opposite direction";
    private static final String ERROR_NORMALIZE_UNIT_VECTOR = "ERROR: normalize() on a unit vector does not return an equal vector";

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)} and {@link primitives.Vector#Vector(Double3)}.
     */
    @Test
    void testConstructor() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: Valid vector
        assertDoesNotThrow(() -> new Vector(1, 2, 3), "ERROR: Valid vector constructor throws an exception");

        // ================== Boundary Values Tests ==================
        // BVA01: Zero vector from doubles
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0), ERROR_CONSTRUCTOR_ZERO);

        // BVA02: Zero vector from Double3
        assertThrows(IllegalArgumentException.class, () -> new Vector(Double3.ZERO), ERROR_CONSTRUCTOR_ZERO_DOUBLE3);

        // BVA03: Near-zero vector
        assertThrows(IllegalArgumentException.class, () -> new Vector(1e-16, 1e-16, 1e-16), ERROR_CONSTRUCTOR_NEAR_ZERO);

    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: Adding two vectors
        assertEquals(new Vector(3, 4, 5), V1.add(V2), ERROR_ADD_GENERAL);

        // ================== Boundary Values Tests ==================
        // BVA01: Adding a vector to its opposite
        assertThrows(IllegalArgumentException.class, () -> V1.add(V_NEGATIVE), ERROR_ADD_ZERO);
    }

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: Subtracting two vectors
        assertEquals(new Vector(-1, -2, -3), V1.subtract(V2), ERROR_SUBTRACT_GENERAL);

        // ================== Boundary Values Tests ==================
        // BVA01: Subtracting a vector from itself
        assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1), ERROR_SUBTRACT_ZERO);
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: Scaling by a positive number
        assertEquals(new Vector(2, 2, 2), V1.scale(2), ERROR_SCALE_POSITIVE);

        // EP02: Scaling by a negative number
        assertEquals(new Vector(-2, -2, -2), V1.scale(-2), ERROR_SCALE_NEGATIVE);

        // ================== Boundary Values Tests ==================
        // BVA01: Scaling by 1
        assertEquals(V1, V1.scale(1), ERROR_SCALE_ONE);

        // BVA02: Scaling by -1
        assertEquals(V_NEGATIVE, V1.scale(-1), ERROR_SCALE_MINUS_ONE);

        // BVA03: Scaling by 0
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0), ERROR_SCALE_ZERO);
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void testDotProduct() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: General case
        assertEquals(9, V1.dotProduct(V2), DELTA, ERROR_DOT_PRODUCT_GENERAL);

        // ================== Boundary Values Tests ==================
        // BVA01: Orthogonal vectors
        assertEquals(0, V1.dotProduct(V_ORTHOGONAL), DELTA, ERROR_DOT_PRODUCT_ORTHOGONAL);

        // BVA02: Dot product with self
        assertEquals(V1.lengthSquared(), V1.dotProduct(V1), DELTA, ERROR_DOT_PRODUCT_SELF);

        // BVA03: Commutativity
        assertEquals(V1.dotProduct(V2), V2.dotProduct(V1), DELTA, ERROR_DOT_PRODUCT_COMMUTATIVE);
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void testCrossProduct() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: General case
        Vector result = V1.crossProduct(V2);
        assertEquals(new Vector(1, -2, 1), result, ERROR_CROSS_PRODUCT_GENERAL);

        // ================== Boundary Values Tests ==================
        // BVA01: Parallel vectors
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V1.scale(2)), ERROR_CROSS_PRODUCT_PARALLEL);

        // BVA02: Orthogonality
        assertEquals(0, result.dotProduct(V1), DELTA, ERROR_CROSS_PRODUCT_ORTHOGONALITY);
        assertEquals(0, result.dotProduct(V2), DELTA, ERROR_CROSS_PRODUCT_ORTHOGONALITY);

        // BVA03: Anti-commutativity
        assertEquals(result, V2.crossProduct(V1).scale(-1), ERROR_CROSS_PRODUCT_ANTI_COMMUTATIVE);
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: General case
        assertEquals(3, V1.lengthSquared(), DELTA, ERROR_LENGTH_SQ_GENERAL);

        // ================== Boundary Values Tests ==================
        // BVA01: Unit vector
        assertEquals(1, Vector.AXIS_X.lengthSquared(), DELTA, ERROR_LENGTH_SQ_UNIT);
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        // ================== Equivalence Partitions Tests ==================
        // EP01: General case
        assertEquals(Math.sqrt(3), V1.length(), DELTA, ERROR_LENGTH_GENERAL);

        // ================== Boundary Values Tests ==================
        // BVA01: Unit vector
        assertEquals(1, Vector.AXIS_X.length(), DELTA, ERROR_LENGTH_UNIT);

        // BVA02: Consistency with lengthSquared
        assertEquals(V1.length(), Math.sqrt(V1.lengthSquared()), DELTA, ERROR_LENGTH_CONSISTENCY);
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        Vector u = V1.normalize();
        // ================== Equivalence Partitions Tests ==================
        // EP01: Check if the vector is a unit vector
        assertEquals(1, u.length(), DELTA, ERROR_NORMALIZE_LENGTH);

        // EP02: Check if the original vector and the unit vector are parallel
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(u), ERROR_NORMALIZE_DIRECTION);

        // EP03: Check if the angle between the original vector and the unit vector is 90 degrees.
        assertTrue(V1.dotProduct(u) > 0, ERROR_NORMALIZE_OPPOSITE_DIRECTION);

        // ================== Boundary Values Tests ==================

        // BVA01: Normalizing a unit vector
        assertEquals(Vector.AXIS_X, Vector.AXIS_X.normalize(), ERROR_NORMALIZE_UNIT_VECTOR);
    }
}
