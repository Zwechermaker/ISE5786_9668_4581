package geometries.impl;

import static primitives.Util.isZero;

import java.util.List;

import geometries.api.Geometry;
import primitives.*;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * <p>
 * The polygon is defined by an ordered sequence of vertices.
 * All vertices must lie in the same plane and be arranged along the
 * polygon edge path.
 * </p>
 * <p>
 * The polygon must be convex.
 * </p>
 * @author Dan Zilberstein
 */
public class Polygon extends Geometry {
   /** Ordered list of polygon vertices */
   protected final List<Point> _vertices;
   /** Plane containing the polygon */
   protected final Plane       _plane;
   /** Number of vertices */
   private final int           _size;

    /**
     * Constructs a convex polygon from an ordered list of vertices.
     * <p>
     * The constructor validates that the provided vertices form a valid convex polygon.
     * The validation includes the following checks:
     * </p>
     * <ul>
     *   <li>The polygon must have at least three vertices.</li>
     *   <li>All vertices must lie on the same plane.</li>
     *   <li>The vertices must be ordered sequentially along the polygon's edge path.</li>
     *   <li>The polygon must be convex. This is verified by checking that the cross product
     *       of consecutive edge vectors consistently points in the same direction relative
     *       to the polygon's normal.</li>
     * </ul>
     *
     * @param vertices An ordered array of the polygon's vertices.
     * @throws IllegalArgumentException if the vertices do not form a valid convex polygon.
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon must have at least 3 vertices.");
        _vertices = List.of(vertices);
        _size = vertices.length;

        // The supporting plane is created from the first three vertices.
        // This plane also provides the constant normal for the polygon.
        _plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (_size == 3) return; // A triangle is always a valid polygon.

        Vector n = _plane.getNormal(null); // Normal is constant for the plane.

        // Check for convexity and coplanarity.
        Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

        // The sign of the dot product of the cross product of consecutive edges and the normal
        // must be consistent for all edges if the polygon is convex and the vertices are ordered.
        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            // Verify that all vertices lie on the same plane.
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lie on the same plane.");

            // Check for consistent orientation of edges.
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered, and the polygon must be convex.");
        }
    }

    @Override
    public void createBoundingBox() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        for (Point p : _vertices) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
        }
        box = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal(point);
    }

    /**
     * Calculates the intersection point of a ray with the polygon.
     * <p>
     * The method first finds the intersection of the ray with the plane containing the polygon.
     * If an intersection point exists, it then checks if this point lies inside the polygon.
     * <p>
     * The inside-outside test is performed by constructing triangles from the ray's origin and each
     * edge of the polygon. The normals of these triangles must all point in the same direction
     * (either all towards or all away from a consistent viewpoint) for the intersection point
     * to be inside the polygon.
     *
     * @param ray         The ray to intersect with the polygon.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} containing the {@link Intersection} point if the ray intersects the polygon, otherwise {@code null}.
     */
    @Override
    public List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {

        List<Point> lst = _plane.findIntersections(ray, maxDistance);

        // If there were no intersections with the plane to begin with.
        if (lst == null) {
            return null;
        }

        // Check if the point intersects the polygon inside the plane.
        Vector vecIterate1 = _vertices.get(0).subtract(ray.origin());
        Boolean positive = null;
        for (int i = 0; i < _size; i++) {
            // Set up the next edge to check.
            Vector vecIterate2 = _vertices.get((i + 1) % _size).subtract(ray.origin());

            // Set up the normal for the plane between the edge and the ray.
            Vector normal = vecIterate1.crossProduct(vecIterate2);
            double val = Util.alignZero(normal.dotProduct(ray.direction()));

            if (i == 0) {
                positive = val > 0;
            }
            if (positive != (val > 0) || val == 0) {
                return null;
            }

            vecIterate1 = vecIterate2;
        }

        return List.of(new Intersection(this, lst.get(0)));
    }

    @Override
    public String toString() {
        return "vertices: " + _vertices + ", plane: " + _plane;
    }
}
