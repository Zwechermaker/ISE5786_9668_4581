package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

/**
 * A class representing an infinite tube in 3D space, defined by a central axis and a radius.
 * <p>
 * A tube is a type of {@link RadialGeometry}. It is essentially an infinite cylinder.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class Tube extends RadialGeometry {
    /**
     * The central axis of the tube, represented by a {@link Ray}.
     */
    protected final Ray _axis;

    /**
     * Constructs a {@link Tube} with a specified radius and axis.
     *
     * @param radius The radius of the tube. Must be a positive value.
     * @param axis   The central axis of the tube.
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    /**
     * Calculates the normal vector to the tube at a given point.
     * <p>
     * The normal is calculated by finding the projection of the vector from the tube's axis origin
     * to the given point onto the axis direction. The normal is the normalized vector from this
     * projected point on the axis to the given point on the tube's surface.
     * <p>
     * Mathematically, if {@code P} is the point on the tube, {@code P0} is the axis origin,
     * and {@code V} is the axis direction, the projection length {@code t} is:
     * <pre>
     * t = (P - P0) · V
     * </pre>
     * The point on the axis is {@code O = P0 + tV}. The normal is then {@code normalize(P - O)}.
     *
     * @param point The point on the surface of the tube.
     * @return The normal vector at the specified point.
     */
    @Override
    public Vector getNormal(Point point) {
        double projection = _axis.direction().dotProduct(point.subtract(_axis.origin()));
        // If the point is on the plane orthogonal to the axis and passing through the axis origin,
        // the projection is zero.
        if (Util.isZero(projection)) {
            return point.subtract(_axis.origin()).normalize();
        }
        return getNormal(point, projection); // Pass it to the helper to avoid recalculation.
    }

    /**
     * A helper method for calculating the normal of the tube, optimized to reuse a pre-calculated projection.
     *
     * @param point      The point on the surface of the tube.
     * @param projection The pre-calculated projection length {@code t}.
     * @return The normal vector at the specified point.
     */
    protected Vector getNormal(Point point, double projection) {
        Vector scaledDirection = _axis.direction().scale(projection);
        Point projectionPoint = _axis.origin().add(scaledDirection);
        return point.subtract(projectionPoint).normalize();
    }

    /**
     * Calculates the intersection points of a ray with the tube.
     * <p>
     * The calculation is based on solving a quadratic equation {@code at^2 + bt + c = 0}, where {@code t}
     * is the distance along the ray from its origin. The equation is derived from the geometric condition
     * that for any intersection point {@code P} on the ray, its distance to the tube's axis must equal the tube's radius.
     * <p>
     * The method involves these steps:
     * <ol>
     *   <li>Handle the edge case where the ray is parallel to the tube's axis (no unique intersection).</li>
     *   <li>Decompose the ray's direction and the vector between the ray and axis origins into components
     *       parallel and orthogonal to the tube's axis.</li>
     *   <li>Use these orthogonal components to build the coefficients {@code a}, {@code b}, and {@code c} of the quadratic equation.</li>
     *   <li>Solve for {@code t} using the discriminant. If real solutions exist, they represent the distances to the intersection points.</li>
     * </ol>
     *
     * @param ray         The ray to intersect with the tube.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} of {@link Intersection} points, or {@code null} if no intersections are found.
     */
    @Override
    public List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        // Edge Case: If the ray is parallel to the axis, no unique intersection exists.
        if (ray.direction().areParallel(_axis.direction())) {
            return null;
        }

        Vector vOrthogonal = ray.direction().orthogonalComponent(_axis.direction());

        // Calculate the coefficients of the quadratic equation: at^2 + bt + c = 0.
        double a = vOrthogonal.lengthSquared();
        double b = 0;
        double c = -_radiusSquared;

        // If the ray origin is not the same as the axis origin, calculate more complex b and c.
        if (!ray.origin().equals(_axis.origin())) {
            Vector deltaOrigin = ray.origin().subtract(_axis.origin());

            // If deltaOrigin is not parallel to the axis, it has an orthogonal component.
            if (!deltaOrigin.areParallel(_axis.direction())) {
                Vector deltaOriginOrthogonal = deltaOrigin.orthogonalComponent(_axis.direction());
                b = 2 * vOrthogonal.dotProduct(deltaOriginOrthogonal);
                c = deltaOriginOrthogonal.lengthSquared() - _radiusSquared;
            }
        }

        double discriminant = b * b - 4 * a * c;

        // If the discriminant is non-positive, there are no real intersection points.
        if (Util.alignZero(discriminant) <= 0) {
            return null;
        }

        double discriminantSquareRoot = Math.sqrt(discriminant);

        // Calculate the two potential solutions for t.
        double t1 = (-b - discriminantSquareRoot) / (2 * a);
        double t2 = (-b + discriminantSquareRoot) / (2 * a);

        return this.getPoints(ray, maxDistance, t1, t2);
    }

    @Override
    public void createBoundingBox() {
        // Infinite geometry, no bounding box
        this.box = null;
    }
    @Override
    public String toString() {
        return super.toString() + ", axis: " + _axis;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Tube tube = (Tube) obj;
        return Objects.equals(_axis, tube._axis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _axis);
    }
}
