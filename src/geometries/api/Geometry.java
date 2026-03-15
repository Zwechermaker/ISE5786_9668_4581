package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * an abstract class that represents a geometry in space
 */
public abstract class Geometry {
    /**
     * a function that calculates the normal of a geometry
     * @return a normal for the geometry
     * @param point a point to find the normal to.
     */
    public abstract Vector getNormal(Point point);
}
