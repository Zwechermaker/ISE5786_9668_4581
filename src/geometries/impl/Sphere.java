package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

/**
 * A class representing a sphere in 3D space, defined by a center point and a radius.
 * <p>
 * A sphere is a type of {@link RadialGeometry} and is a common primitive in 3D graphics.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere.
     */
    private final Point _center;

    /**
     * Constructs a {@link Sphere} with a specified center point and radius.
     *
     * @param point  The center of the sphere.
     * @param radius The radius of the sphere. Must be a positive value.
     */
    public Sphere(Point point, double radius) {
        super(radius);
        _center = point;
    }

    /**
     * Calculates the normal vector to the sphere at a given point.
     * <p>
     * The normal at any point on the sphere's surface is the normalized vector
     * from the sphere's center to that point.
     *
     * @param point The point on the surface of the sphere.
     * @return The normal vector at the specified point.
     */
    @Override
    public Vector getNormal(Point point) {
        return point.subtract(_center).normalize();
    }

    /**
     * Calculates the intersection points of a ray with the sphere.
     * <p>
     * The method uses a geometric approach based on the Pythagorean theorem. It involves these steps:
     * <ol>
     *   <li>Handle the edge case where the ray starts at the center of the sphere.</li>
     *   <li>Calculate the projection of the vector from the ray's origin to the sphere's center onto the ray's direction.</li>
     *   <li>Find the shortest distance from the sphere's center to the ray.</li>
     *   <li>If this distance is greater than or equal to the radius, the ray does not intersect the sphere.</li>
     *   <li>Otherwise, use the Pythagorean theorem to find the distance from the projection point to the intersection points.</li>
     *   <li>Calculate the final distances {@code t1} and {@code t2} along the ray to the two intersection points.</li>
     * </ol>
     *
     * @param ray         The ray to intersect with the sphere.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} of {@link Intersection} points, or {@code null} if no intersections are found.
     */
    @Override
    public List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        // if the ray starts at the center of the sphere.
        if (_center.equals(ray.origin())) {
            return getPoints(ray, maxDistance, _radius);
        }

        Vector midCircVec = _center.subtract(ray.origin());
        double projMiddle = ray.direction().dotProduct(midCircVec);

        double oqDist = Math.sqrt(midCircVec.lengthSquared() - projMiddle * projMiddle);

        // if ray is tangent to the sphere or completely misses it.
        if (Util.alignZero(oqDist - _radius) >= 0) {
            return null;
        }

        double qpDist = Math.sqrt(_radiusSquared - oqDist * oqDist);

        double t1 = Util.alignZero(projMiddle - qpDist);
        double t2 = Util.alignZero(projMiddle + qpDist);

        return getPoints(ray, maxDistance, t1, t2);
    }

    @Override
    public String toString() {
        return "Sphere(center=" + _center + ", " + super.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Sphere sphere = (Sphere) obj;
        return Objects.equals(_center, sphere._center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _center);
    }
}
