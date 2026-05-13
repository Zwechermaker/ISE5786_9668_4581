package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.api.Intersectable.Intersection;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link Polygon}.
 * The tests verify:
 * <ul>
 * <li>Polygon constructor validity</li>
 * <li>{@link Polygon#getNormal(Point)}</li>
 * <li>{@link Polygon#findIntersections(Ray)}</li>
 * </ul>
 * Tests follow the methodology of
 * Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PolygonTests {
   /** Default constructor to satisfy JavaDoc generator */
   PolygonTests() { /* to satisfy JavaDoc generator */ }

   /** Vertex (1,0,0) used in polygon tests */
   private static final Point  POINT_X       = new Point(1, 0, 0);
   /** Vertex (0,1,0) used in polygon tests */
   private static final Point  POINT_Y       = new Point(0, 1, 0);
   /** Vertex (0,0,1) used in polygon tests */
   private static final Point  POINT_Z       = new Point(0, 0, 1);

   /** Additional vertex used for valid polygon construction */
   private static final Point  POINT1        = new Point(-1, 1, 1);
   /** Point not in the polygon plane */
   private static final Point  POINT2        = new Point(0, 2, 2);
   /** Point that creates a concave polygon */
   private static final Point  POINT3        = new Point(0.5, 0.25, 0.5);
   /** Point located on one of the polygon edges */
   private static final Point  POINT4        = new Point(0, 0.5, 0.5);

   /** Delta value for accuracy when comparing double values. */
   private static final double DELTA         = 1e-6;

   /** Error message for wrong polygon intersection */
   private static final String ERROR_POLYGON = "ERROR: wrong polygon intersection";

   /** Ray origin for intersection tests */
   private static final Point  RAY_ORIGIN    = new Point(0, -1, 0);

   /**
    * Test method for {@link Polygon#Polygon(Point...)}.
    * Verifies correct and incorrect polygon constructions.
    */
   @Test
   void testConstructor() {

      // ============ Equivalence Partitions Tests ==============

      // TC01: Correct convex quadrilateral with vertices in correct order
      assertDoesNotThrow(() -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT1),
              "Failed constructing a correct polygon");

      // TC02: Wrong vertices order
      assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_Y, POINT_X, POINT1),
              "Constructed a polygon with wrong order of vertices");

      // TC03: Vertices not in the same plane
      assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT2),
              "Constructed a polygon with vertices that are not in the same plane");

      // TC04: Concave quadrilateral
      assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT3),
              "Constructed a concave polygon");

      // =============== Boundary Values Tests ==================

      // TC05: Vertex on a side
      assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT4),
              "Constructed a polygon with a vertex on a side");

      // TC06: Last point equals first point
      assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT_Z),
              "Constructed a polygon with duplicate first/last vertex");

      // TC07: Co-located points
      assertThrows(IllegalArgumentException.class, () -> new Polygon(POINT_Z, POINT_X, POINT_Y, POINT_Y),
              "Constructed a polygon with co-located vertices");
   }

   /**
    * Test method for {@link Polygon#getNormal(Point)}.
    * Verifies that the returned normal vector is unit length and orthogonal
    * to all polygon edges.
    */
   @Test
   void testGetNormal() {
      // ============ Equivalence Partitions Tests ==============
      Point[] pts     =
              { POINT_Z, POINT_X, POINT_Y, POINT1 };
      Polygon polygon = new Polygon(pts);

      // TC08: Correct normal calculation
      assertDoesNotThrow(() -> polygon.getNormal(POINT_Z), "getNormal() threw unexpected exception");
      Vector result = polygon.getNormal(POINT_Z);
      // Ensure |n| = 1
      assertEquals(1, result.length(), DELTA, "Polygon normal is not a unit vector");
      // Ensure normal is orthogonal to all edges
      for (int i = 0; i < pts.length; ++i) {
         Vector edge = pts[i].subtract(pts[i == 0 ? pts.length - 1 : i - 1]);
         assertEquals(0d, result.dotProduct(edge), DELTA, "Polygon normal is not orthogonal to an edge");
      }
   }

   /**
    * Test method for {@link Polygon#findIntersections(Ray)}.
    */
   @Test
   void testFindIntersections() {
      Polygon poly = new Polygon(POINT_Z, POINT_X, POINT_Y, POINT1);

      // ============ Equivalence Partitions Tests ==============

      // TC09: Inside polygon (1 point)
      Ray ray1 = new Ray(RAY_ORIGIN, new Vector(0, 1.5, 0.5));
      List<Point> result1 = poly.findIntersections(ray1);
      assertEquals(1, result1.size(), ERROR_POLYGON);
      assertEquals(new Point(0, 0.5, 0.5), result1.get(0), ERROR_POLYGON);

      // TC10: Outside against edge (0 points)
      Ray ray2 = new Ray(RAY_ORIGIN, new Vector(0, 1, 2));
      assertNull(poly.findIntersections(ray2), ERROR_POLYGON);

      // TC11: Outside against vertex (0 points)
      Ray ray3 = new Ray(RAY_ORIGIN, new Vector(1, 1, 1));
      assertNull(poly.findIntersections(ray3), ERROR_POLYGON);

      // =============== Boundary Values Tests ==================

      // TC12: On edge (0 points)
      Ray ray4 = new Ray(RAY_ORIGIN, new Vector(0.5, 1, 0));
      assertNull(poly.findIntersections(ray4), ERROR_POLYGON);

      // TC13: In vertex (0 points)
      Ray ray5 = new Ray(RAY_ORIGIN, new Vector(1, 1, 0));
      assertNull(poly.findIntersections(ray5), ERROR_POLYGON);

      // TC14: On edge's continuation (0 points)
      Ray ray6 = new Ray(RAY_ORIGIN, new Vector(-2, 1, 2));
      assertNull(poly.findIntersections(ray6), ERROR_POLYGON);
   }

    /**
     * Test method for {@link geometries.impl.Polygon#calcIntersections(primitives.Ray)}.
     */
    @Test
    void testCalcIntersections() {
        Polygon poly = new Polygon(POINT_Z, POINT_X, POINT_Y, POINT1);

        // ============ Equivalence Partitions Tests ==============
        // TC15: Ray intersects the polygon
        List<Intersection> result = poly.calcIntersections(new Ray(RAY_ORIGIN, new Vector(0, 1.5, 0.5)));
        assertEquals(1, result.size(), "Wrong number of intersections");
        assertSame(poly, result.get(0).geometry, "Intersection does not belong to the correct geometry");

        // TC16: Ray does not intersect the polygon
        assertNull(poly.calcIntersections(new Ray(RAY_ORIGIN, new Vector(0, 1, 2))),
                "Ray hitting outside polygon should return null");
    }
}
