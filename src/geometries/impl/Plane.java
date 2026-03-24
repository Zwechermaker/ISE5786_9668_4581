package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;
import java.util.Objects;

/**
 * A class that describes a plane in space.
 */
public class Plane extends Geometry {
    /**
     * A point on the plane.
     */
    private final Point _point;

    /**
     * The normal vector of the plane.
     */
    private final Vector _normal;

    /**
     * constructor that gets three point and creates the plane that they are on
     * @param p1 the first point on the plane
     * @param p2 the second point on the plane
     * @param p3 the third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3)
    {
        try{
            _normal = (p2.subtract(p1)).crossProduct(p3.subtract(p1)).normalize();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Points are collinear");
        }
        _point = p1;
    }

    /**
     * constructor that gets a vector and pint
     * @param point the point of the plane
     * @param normal the normal vector of the plane
     */
    public Plane(Point point,Vector normal){
        this._point = point;
        this._normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    public String toString() {
        return "Point: " + _point + ", Normal: " + _normal + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Plane plane = (Plane) obj;
        return Objects.equals(_point, plane._point)
                && Objects.equals(_normal, plane._normal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_point, _normal);
    }
}
