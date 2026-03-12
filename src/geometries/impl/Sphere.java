package geometries.impl;

import primitives.Point;

public class Sphere extends RadialGeometry{
    private final Point center;

    /**
     * A parameter constructor for a sphere
     * @param point the center of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point point, double radius) {
        super(radius);
        center = point;
    }

}
