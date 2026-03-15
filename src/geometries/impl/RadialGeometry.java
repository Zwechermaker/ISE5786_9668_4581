package geometries.impl;

import geometries.api.Geometry;
import primitives.Util;

/**
 * An abstract class that represents a radial geometry in space.
 */
public abstract class RadialGeometry extends Geometry {
    /**
     * The radius of the radial geometry.
     */
    protected final double _radius ;

    /**
     * The radius squared of the radial geometry.
     */
    protected final double _radiusSquared;

    /**
     * constructor for radial geometry's
     * @param radius the radius of a radial geometry
     */
    public RadialGeometry(double radius){
        if(Util.alignZero(radius)<=0)
        {
            throw new IllegalArgumentException("Radius cannot be less or equal to 0.");
        }
        this._radius = radius;
        _radiusSquared = radius * radius;
    }

    @Override
    public String toString() {
        return "_radius: " + _radius + ", ";
    }
}
