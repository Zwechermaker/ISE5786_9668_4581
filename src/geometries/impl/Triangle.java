package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * A class that represents a triangle in space.
 */
public class Triangle extends Polygon{
    /**
     *A parameter constructor for a triangle
     * @param p1 a point in the triangle
     * @param p2 a point in the triangle
     * @param p3 a point in the triangle
     */
    public Triangle(Point p1,Point p2,Point p3) {
        super(p1,p2,p3);
    }

    @Override
    public Vector getNormal(Point point){
        return null;
    }
}
