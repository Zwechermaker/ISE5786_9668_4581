package geometries.impl;

import primitives.Point;
import primitives.Vector;

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
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "center: " + _center + "\n";
    }
}
