package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;

/**
 * A class that represents a triangle in space.
 */
public final class Triangle extends Polygon{
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
    public List<Point> findIntersections(Ray ray) {
        Vector E1 = _vertices.get(1).subtract(_vertices.get(0));
        Vector E2 = _vertices.get(2).subtract(_vertices.get(0));

        Vector T = ray.origin().subtract(_vertices.get(0));

        Vector P = ray.direction().crossProduct(E2);
        Vector Q = T.crossProduct(E1);

        double det = E1.dotProduct(P);

        //if ray is parallel to the triangle
        if (Util.alignZero(det) == 0){
            return null;
        }
        double u = P.dotProduct(T) / det;
        //if the point is outside or on the edge
        if (Util.alignZero(u) <= 0 || Util.alignZero(u - 1) >= 0){
            return null;
        }
        double v = Q.dotProduct(ray.direction()) / det;
        //if the point is outside or on the edge
        if (Util.alignZero(v) <= 0 || Util.alignZero(u+v-1) >= 0){
            return null;
        }
        double t = Q.dotProduct(E2) / det;

        //if the triangle is behind the ray
        if (Util.alignZero(t) <= 0){
            return null;
        }
        //return the point
        return List.of(ray.getPoint(t));
    }
}
