package geometries.api;

import primitives.*;

import java.util.LinkedList;
import java.util.List;

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
     *a material variable that defines a geometry.
     */
    private Material _material = new Material();
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
    /**
     * a setter for material.
     * @param material a material to set
     * @return renewed object to allow for concatenation
     */
    public Geometry setMaterial(Material material){
        _material = material;
        return this;
    }

    /**
     * getter for the material
     * @return the material
     */
    public Material getMaterial(){
        return _material;
    }


    /**
     *  A function that takes parameters found in findIntersections function
     *  and calculates a list of points on the ray based on the parameters.
     * @param ray to find the point in.
     * @param maxDistance limits the distance we are looking for.
     * @param tValues the distances from the ray's origin to the intersection points
     * @return A list of intersection
     */
    protected List<Intersection> getPoints(Ray ray,double maxDistance,  double... tValues) {
        List<Intersection> result = null;
        for (double t : tValues) {
            if (Util.alignZero(t) > 0 && Util.alignZero(t - maxDistance) <= 0) {
                if (result == null) result = new LinkedList<>();
                result.add(new Intersection(this, ray.getPoint(t)));
            }
        }
        return result;
    }
}
