package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;

/**
 * A class representing a triangle in 3D space.
 * <p>
 * A triangle is a specific type of {@link Polygon} with three vertices.
 * It is one of the most fundamental shapes in 3D graphics.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class Triangle extends Polygon {
    /**
     * Constructs a {@link Triangle} from three points.
     *
     * @param p1 The first vertex of the triangle.
     * @param p2 The second vertex of the triangle.
     * @param p3 The third vertex of the triangle.
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    /**
     * Calculates the intersection point of a ray with the triangle using barycentric coordinates.
     * <p>
     * This method uses Cramer's rule to solve for the barycentric coordinates (u, v) of the
     * intersection point. An intersection is considered valid only if it is strictly inside
     * the triangle's boundaries (i.e., not on the edges).
     * <p>
     * The intersection point {@code P} is represented as:
     * <pre>
     * P = P0 + u*edge1 + v*edge2
     * </pre>
     * where {@code P0} is the first vertex, and {@code edge1, edge2} are the vectors from {@code P0}
     * to the other two vertices. The ray is {@code R(t) = O + tD}. The intersection is found by
     * solving the equation {@code R(t) = P} for {@code t, u, v}.
     *
     * @param ray         The ray to intersect with the triangle.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} containing the {@link Intersection} point if the ray intersects the triangle, otherwise {@code null}.
     */
    @Override
    public List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        // Calculating two edge vectors (containing the first vertex).
        Vector edge1 = _vertices.get(1).subtract(_vertices.get(0));
        Vector edge2 = _vertices.get(2).subtract(_vertices.get(0));

        if (ray.origin().equals(_vertices.get(0))) {
            return null;
        }
        // Calculate "result" vector.
        Vector result = ray.origin().subtract(_vertices.get(0));

        if (ray.direction().areParallel(edge2)) {
            return null;
        }
        // Precalculate vectors used in Cramer's rule.
        Vector P = ray.direction().crossProduct(edge2);

        if (result.areParallel(edge1)) {
            return null;
        }
        Vector Q = result.crossProduct(edge1);

        // Calculate determinant of the matrix.
        double det = edge1.dotProduct(P);

        // If ray is parallel to the triangle.
        if (Util.isZero(det)) {
            return null;
        }
        // Calculate u using Cramer's rule (u is the edge 1 barycentric component).
        double u = P.dotProduct(result) / det;
        // If the point is outside or on the edge.
        if (Util.alignZero(u) <= 0 || Util.alignZero(u - 1) >= 0) {
            return null;
        }

        // Calculate v using Cramer's rule (v is the edge 2 barycentric component).
        double v = Q.dotProduct(ray.direction()) / det;
        // If the point is outside or on the edge.
        if (Util.alignZero(v) <= 0 || Util.alignZero(u + v - 1) >= 0) {
            return null;
        }

        // Calculating t using Cramer's rule.
        double t = Q.dotProduct(edge2) / det;

        // If the triangle is behind the ray, return null.
        return getPoints(ray, maxDistance, t);
    }

    @Override
    public String toString() {
        return "Triangle(\n" +
                "  p1=" + _vertices.get(0) + ",\n" +
                "  p2=" + _vertices.get(1) + ",\n" +
                "  p3=" + _vertices.get(2) + "\n" +
                ")";
    }
}
