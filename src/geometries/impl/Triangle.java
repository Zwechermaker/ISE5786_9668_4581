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
    public List<Intersection> calcIntersectionsHelper(Ray ray) {
        //calculating two edge vectors (containing the first vertex)
        Vector edge1 = _vertices.get(1).subtract(_vertices.get(0));
        Vector edge2 = _vertices.get(2).subtract(_vertices.get(0));

        //calculate "result" vector.
        Vector result = ray.origin().subtract(_vertices.get(0));

        //precalculate vectors used in cramer's rule
        Vector P = ray.direction().crossProduct(edge2);
        Vector Q = result.crossProduct(edge1);

        //calculate determinant of the matrix
        double det = edge1.dotProduct(P);

        //if ray is parallel to the triangle
        if (Util.alignZero(det) == 0){
            return null;
        }
        //calculate u using cramer's rule (u is the edge 1 barycentric component)
        double u = P.dotProduct(result) / det;
        //if the point is outside or on the edge
        if (Util.alignZero(u) <= 0 || Util.alignZero(u - 1) >= 0){
            return null;
        }

        //calculate v using cramer's rule (v is the edge 2 barycentric component)
        double v = Q.dotProduct(ray.direction()) / det;
        //if the point is outside or on the edge
        if (Util.alignZero(v) <= 0 || Util.alignZero(u+v-1) >= 0){
            return null;
        }

        //calculating t using cramer's rule
        double t = Q.dotProduct(edge2) / det;

        //if the triangle is behind the ray, return null
        return getPoints(ray, t);
    }

    @Override
    public String toString() {
        return "Triangle(\n" +
               "  p1=" + _vertices.get(0) + ",\n" +
               "  p2=" + _vertices.get(1) + ",\n" +
               "  p3=" + _vertices.get(2) + "\n" +
               ")";
    }
}
