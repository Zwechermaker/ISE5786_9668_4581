package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

/**
 * A class that represents a sphere in space.
 */
public final class Sphere extends RadialGeometry{
    /**
     * The center of the sphere.
     */
    private final Point _center;

    /**
     * A parameter constructor for a sphere
     * @param point the center of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point point, double radius) {
        super(radius);
        _center = point;
    }

    @Override
    public Vector getNormal(Point point){
        return point.subtract(_center).normalize();
    }

    @Override
    public String toString() {
        return super.toString() + "center: " + _center + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        if (!super.equals(obj)) return false; // if there is inheritance
        Sphere sphere = (Sphere) obj;
        return Objects.equals(_center, sphere._center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _center);
    }

    @Override
    public List<Point> findIntersections(Ray ray){
        //if the ray starts at the center of the sphere.
        if (_center.equals(ray.origin())) {
            return List.of(ray.getPoint(_radius));
        }
        Vector midCircVec = _center.subtract(ray.origin());
        double projMiddle = ray.direction().dotProduct(midCircVec);

        double OQdist = Math.sqrt(midCircVec.lengthSquared()-projMiddle*projMiddle);

        //if ray is tangent to the sphere or completely misses it.
        if(Util.alignZero(OQdist-_radius) >= 0){
            return null;
        }

        double QPdist = Math.sqrt(_radiusSquared- OQdist*OQdist);


        double t1 = Util.alignZero(projMiddle + QPdist);
        double t2 = Util.alignZero(projMiddle - QPdist);

        if(t1 <= 0 && t2 <= 0){
            return null;
        }

        if(t1 <= 0){
            return List.of(ray.getPoint(t2));
        }
        if(t2 <= 0){
            return List.of(ray.getPoint(t1));
        }

        return List.of(ray.getPoint(t1), ray.getPoint(t2));
    }
}
