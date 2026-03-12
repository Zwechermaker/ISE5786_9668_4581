package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

public class Plane extends Geometry {
    private final Point point;
    private final Vector normal;

    /**
     * constructor that gets three point and creates the plane that they are on
     * @param p1 the first point on the plane
     * @param p2 the second point on the plane
     * @param p3 the third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3)
    {
        point=p1;
        //TODO: complete constructor
    }

    /**
     * constructor that gets a vector and pint
     * @param point the point of the plane
     * @param normal the normal vector of the plane
     */
    public Plane(Point point,Vector normal){
        this.point=point;
        this.normal=normal.normalize();
    }
}
