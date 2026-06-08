package primitives;

import geometries.api.Intersectable.Intersection;

import java.util.List;
import java.util.Objects;

/**
 * A class representing a ray in 3D space, defined by an origin point and a direction vector.
 * <p>
 * A ray is a fundamental concept in ray tracing, representing the path of light.
 * The direction vector is always normalized.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class Ray {
    /**
     * The origin point of the ray.
     */
    private final Point _origin;

    /**
     * The direction vector of the ray. This vector is always normalized.
     */
    private final Vector _direction;

    /**
     * Constructs a {@link Ray} with a specified origin point and direction vector.
     *
     * @param point  The origin point of the ray.
     * @param vector The direction vector of the ray. It will be normalized.
     */
    public Ray(Point point, Vector vector) {
        _origin = point;
        _direction = vector.normalize();
    }

    /**
     * Retrieves the direction vector of the ray.
     *
     * @return The normalized direction vector.
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Retrieves the origin point of the ray.
     *
     * @return The origin point.
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Calculates a point along the ray at a specified distance from the origin.
     * <p>
     * The point is calculated using the formula: {@code P(t) = P0 + t * V}, where {@code P0} is the origin,
     * {@code V} is the direction vector, and {@code t} is the distance (parameter).
     *
     * @param parameter The distance along the ray from the origin.
     * @return The calculated {@link Point}. If scaling results in an invalid vector, returns the origin.
     */
    public Point getPoint(double parameter) {
        try {
            return _origin.add(_direction.scale(parameter));
        } catch (IllegalArgumentException e) {
            return _origin;
        }
    }

    /**
     * Finds the closest intersection point from a list of intersections to the ray's origin.
     *
     * @param intersections A list of {@link Intersection} objects.
     * @return The closest {@link Intersection} object, or {@code null} if the list is empty.
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null) {
            return null;
        }
        Intersection currentClosestIntersection = null;
        double closestCurentDistance = Double.POSITIVE_INFINITY;
        for (Intersection currentIntersection : intersections) {
            if (currentIntersection.point.distanceSquared(_origin) < closestCurentDistance) {
                closestCurentDistance = currentIntersection.point.distanceSquared(_origin);
                currentClosestIntersection = currentIntersection;
            }
        }
        return currentClosestIntersection;
    }

    /**
     * Finds the closest point from a list of points to the ray's origin.
     *
     * @param points A list of {@link Point} objects.
     * @return The closest {@link Point}, or {@code null} if the list is empty.
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(
                points.stream()
                        .map(point -> new Intersection(null, point)).toList()
        ).point;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ray ray = (Ray) obj;
        return Objects.equals(_origin, ray._origin) && Objects.equals(_direction, ray._direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }

    @Override
    public String toString() {
        return "Ray(origin=" + _origin + ", direction=" + _direction + ")";
    }
}
