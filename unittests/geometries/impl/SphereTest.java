package geometries.impl;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Sphere class.
 */
class SphereTest {
    /**
     * Default constructor for Javadoc purposes.
     */
    SphereTest() {}

    // ================== Test Constants ==================
    /** The center point of the sphere. */
    private static final Point CENTER = new Point(0, 0, 0);
    /** The radius of the sphere. */
    private static final double RADIUS = 5;
    /** A sphere for testing. */
    private static final Sphere SPHERE = new Sphere(CENTER, RADIUS);

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
    }

    /**
     * Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // TC03: A random point on the sphere
        Point pointOnSphere = new Point(3, 4, 0);
        Vector normal = SPHERE.getNormal(pointOnSphere);
        assertEquals(new Vector(3, 4, 0).normalize(), normal, "getNormal() for sphere is wrong");
    }
}
