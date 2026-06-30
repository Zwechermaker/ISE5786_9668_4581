package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

/**
 * A class representing an infinite two-dimensional plane in 3D space.
 * <p>
 * A plane can be defined by a point on the plane and a normal vector, or by three non-collinear points.
 * It is a fundamental geometric primitive used in 3D graphics.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class Plane extends Geometry {
    /**
     * A point on the plane, used as a reference for its position in space.
     */
    private final Point _point;

    /**
     * The normal vector to the plane. This vector is orthogonal to the surface of the plane.
     */
    private final Vector _normal;

    /**
     * Constructs a {@link Plane} from three points.
     * <p>
     * The three points must not be collinear (i.e., they cannot lie on the same straight line).
     * The normal of the plane is calculated using the cross product of the vectors formed by these points.
     *
     * @param p1 The first point on the plane.
     * @param p2 The second point on the plane.
     * @param p3 The third point on the plane.
     * @throws IllegalArgumentException if the points are collinear, as this would not define a unique plane.
     */
    public Plane(Point p1, Point p2, Point p3) {
        // The vectors (p2 - p1) and (p3 - p1) are two vectors on the plane.
        // Their cross product gives the normal to the plane.
        // If the points are collinear, the cross product will be a zero vector, which will throw an exception upon normalization.
        _normal = (p2.subtract(p1)).crossProduct(p3.subtract(p1)).normalize();
        _point = p1;
    }

    /**
     * Constructs a {@link Plane} from a point and a normal vector.
     *
     * @param point  A point on the plane.
     * @param normal The normal vector to the plane. The vector will be normalized.
     */
    public Plane(Point point, Vector normal) {
        this._point = point;
        this._normal = normal.normalize();
    }

    @Override
    public void createBoundingBox() {
        // Infinite geometry, no bounding box
        this.box = null;
    }

    /**
     * Returns the normal vector of the plane.
     * For a plane, the normal is constant at every point on its surface.
     *
     * @param point A point on the surface of the plane (ignored, as the normal is uniform).
     * @return The normal vector of the plane.
     */
    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    /**
     * Calculates the intersection point of a ray with the plane.
     * <p>
     * The intersection is found by solving the equation of the plane for the ray's parameter {@code t}.
     * The equation for a point {@code P} on the plane is {@code (P - P0) · N = 0}, where {@code P0} is a point on the plane
     * and {@code N} is the normal. For a ray {@code R(t) = Q + tV}, we solve for {@code t}:
     * <pre>
     * t = ((P0 - Q) · N) / (V · N)
     * </pre>
     * An intersection exists only if the ray is not parallel to the plane (i.e., {@code V · N ≠ 0}).
     *
     * @param ray         The ray to intersect with the plane.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} containing a single {@link Intersection} point if an intersection is found within the distance, otherwise {@code null}.
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        double nv = _normal.dotProduct(ray.direction());

        // If nv is zero, the ray is parallel to the plane. No intersection unless the ray starts on the plane.
        // If the ray starts on the plane, we consider it as no unique intersection point.
        if (Util.isZero(nv) || ray.origin().equals(_point)) {
            return null;
        }

        Vector planeVec = _point.subtract(ray.origin());
        double parameter = _normal.dotProduct(planeVec) / nv;

        // If the parameter is non-positive, the intersection is behind or at the ray's origin.
        if (Util.alignZero(parameter) <= 0) {
            return null;
        }

        return getPoints(ray, maxDistance, parameter);
    }

    @Override
    public String toString() {
        return "point: " + _point + ", normal: " + _normal;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Plane plane = (Plane) obj;
        // Two planes are equal if they have the same reference point and normal vector.
        // A more robust check would verify if they are coplanar.
        return Objects.equals(_point, plane._point)
                && Objects.equals(_normal, plane._normal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_point, _normal);
    }
}
