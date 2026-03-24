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
    /** A small tolerance for floating-point comparisons. */
    private static final double DELTA = 1E-15;
    /** A vector for tests to (1,1,1). */
    private static final Vector V1 = new Vector(1, 1, 1);
    /** A vector for tests to (2,3,4). */
    private static final Vector V2 = new Vector(2, 3, 4);
    /** A vector for tests to (-1,-1,-1). */
    private static final Vector V_NEGATIVE = new Vector(-1, -1, -1);
    /** An orthogonal vector for tests. */
    private static final Vector V_ORTHOGONAL = new Vector(1, -2, 1);

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)} and {@link primitives.Vector#Vector(Double3)}.
     */
    @Test
    void testConstructor() {
        // ================== Equivalence Partitions Tests ==================
        // TC01: Valid vector
        assertDoesNotThrow(() -> new Vector(1, 2, 3), "ERROR: Valid vector constructor throws an exception");

        // ================== Boundary Values Tests ==================
        // TC02: Zero vector from doubles
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0), "ERROR: Vector(0,0,0) should throw an exception");

        // TC03: Zero vector from Double3
        assertThrows(IllegalArgumentException.class, () -> new Vector(Double3.ZERO), "ERROR: Vector(Double3.ZERO) should throw an exception");

        // TC04: Near-zero vector
        assertThrows(IllegalArgumentException.class, () -> new Vector(1e-16, 1e-16, 1e-16), "ERROR: Vector with near-zero components should throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ================== Equivalence Partitions Tests ==================
        // TC05: Adding two vectors
        assertEquals(new Vector(3, 4, 5), V1.add(V2), "ERROR: add() for two vectors does not work correctly");

        // ================== Boundary Values Tests ==================
        // TC06: Adding a vector to its opposite
        assertThrows(IllegalArgumentException.class, () -> V1.add(V_NEGATIVE), "ERROR: add() with a vector and its opposite should throw an exception");
    }

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        // ================== Equivalence Partitions Tests ==================
        // TC07: Subtracting two vectors
        assertEquals(new Vector(-1, -2, -3), V1.subtract(V2), "ERROR: subtract() for two vectors does not work correctly");

        // ================== Boundary Values Tests ==================
        // TC08: Subtracting a vector from itself
        assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1), "ERROR: subtract() with a vector from itself should throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ================== Equivalence Partitions Tests ==================
        // TC09: Scaling by a positive number
        assertEquals(new Vector(2, 2, 2), V1.scale(2), "ERROR: scale() with a positive number does not work correctly");

        // TC10: Scaling by a negative number
        assertEquals(new Vector(-2, -2, -2), V1.scale(-2), "ERROR: scale() with a negative number does not work correctly");

        // ================== Boundary Values Tests ==================
        // TC11: Scaling by 1
        assertEquals(V1, V1.scale(1), "ERROR: scale() with 1 does not return an equal vector");

        // TC12: Scaling by -1
        assertEquals(V_NEGATIVE, V1.scale(-1), "ERROR: scale() with -1 does not return the opposite vector");

        // TC13: Scaling by 0
        assertThrows(IllegalArgumentException.class, () -> V1.scale(0), "ERROR: scale() with 0 should throw an exception");
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void testDotProduct() {
        // ================== Equivalence Partitions Tests ==================
        // TC14: General case
        assertEquals(9, V1.dotProduct(V2), DELTA, "ERROR: dotProduct() returns wrong value");

        // ================== Boundary Values Tests ==================
        // TC15: Orthogonal vectors
        assertEquals(0, V1.dotProduct(V_ORTHOGONAL), DELTA, "ERROR: dotProduct() for orthogonal vectors is not zero");

        // TC16: Dot product with self
        assertEquals(V1.lengthSquared(), V1.dotProduct(V1), DELTA, "ERROR: dotProduct() with self does not equal lengthSquared()");

        // TC17: Commutativity
        assertEquals(V1.dotProduct(V2), V2.dotProduct(V1), DELTA, "ERROR: dotProduct() is not commutative");
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void testCrossProduct() {
        // ================== Equivalence Partitions Tests ==================
        // TC18: General case
        Vector result = V1.crossProduct(V2);
        assertEquals(new Vector(1, -2, 1), result, "ERROR: crossProduct() returns wrong value");

        // ================== Boundary Values Tests ==================
        // TC19: Parallel vectors
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V1.scale(2)), "ERROR: crossProduct() for parallel vectors should throw an exception");

        // TC20: Orthogonality
        assertEquals(0, result.dotProduct(V1), DELTA, "ERROR: crossProduct() result is not orthogonal to its operands");
        assertEquals(0, result.dotProduct(V2), DELTA, "ERROR: crossProduct() result is not orthogonal to its operands");

        // TC21: Anti-commutativity
        assertEquals(result, V2.crossProduct(V1).scale(-1), "ERROR: crossProduct() is not anti-commutative");
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}.
     */
    @Test
    void testLengthSquared() {
        // ================== Equivalence Partitions Tests ==================
        // TC22: General case
        assertEquals(3, V1.lengthSquared(), DELTA, "ERROR: lengthSquared() returns wrong value");

        // ================== Boundary Values Tests ==================
        // TC23: Unit vector
        assertEquals(1, Vector.AXIS_X.lengthSquared(), DELTA, "ERROR: lengthSquared() for a unit vector should be 1");
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testLength() {
        // ================== Equivalence Partitions Tests ==================
        // TC24: General case
        assertEquals(Math.sqrt(3), V1.length(), DELTA, "ERROR: length() returns wrong value");

        // ================== Boundary Values Tests ==================
        // TC25: Unit vector
        assertEquals(1, Vector.AXIS_X.length(), DELTA, "ERROR: length() for a unit vector should be 1");

        // TC26: Consistency with lengthSquared
        assertEquals(V1.length(), Math.sqrt(V1.lengthSquared()), DELTA, "ERROR: length() and lengthSquared() are not consistent");
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        Vector u = V1.normalize();
        // ================== Equivalence Partitions Tests ==================
        // TC27: Check if the vector is a unit vector
        assertEquals(1, u.length(), DELTA, "ERROR: normalize() does not create a unit vector");

        // TC28: Check if the original vector and the unit vector are parallel
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(u), "ERROR: normalize() changes the vector's direction");

        // TC29: Check if the angle between the original vector and the unit vector is 90 degrees.
        assertTrue(V1.dotProduct(u) > 0, "ERROR: normalize() creates a vector in the opposite direction");

        // ================== Boundary Values Tests ==================
        // TC30: Normalizing a unit vector
        assertEquals(Vector.AXIS_X, Vector.AXIS_X.normalize(), "ERROR: normalize() on a unit vector does not return an equal vector");
    }
}
