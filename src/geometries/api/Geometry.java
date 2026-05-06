package geometries.api;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * an abstract class that represents a geometry in space
 */
public abstract class Geometry extends Intersectable {
    /**
     * A constructor for a geometry (for javadoc purposes)
     */
    public Geometry(){}

    /**
     * emission color of a geometry.
     */
    private Color _emission = Color.BLACK;
    /**
     * a function that calculates the normal of a geometry
     * @return a normal for the geometry
     * @param point a point to find the normal to.
     */
    public abstract Vector getNormal(Point point);

    /**
     * a setter for emission color.
     * @param emission color of emission
     * @return renewed object to allow for concatenation
     */
    public Geometry setEmission(Color emission){
        _emission = emission;
        return this;
    }
    /**
     * getter for emission color.
     * @return emission color
     */
    public Color getEmission(){
        return _emission;
    }
}
