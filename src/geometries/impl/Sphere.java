package geometries.impl;

import primitives.Point;
import primitives.Vector;
import java.util.Objects;

/**
 * A class that represents a sphere in space.
 */
public class Sphere extends RadialGeometry{
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
}
