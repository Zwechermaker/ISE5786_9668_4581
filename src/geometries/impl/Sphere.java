package geometries.impl;

import primitives.Point;

public class Sphere extends RadialGeometry{
    private final Point center;
    public Sphere(Point point, double radius) {
        super(radius);
        center = point;
    }

}
