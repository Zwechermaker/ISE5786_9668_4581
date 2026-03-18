package geometries.impl;

import geometries.api.Geometry;
import primitives.Util;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RadialGeometry radialGeometry = (RadialGeometry) obj;
        return Util.isZero(_radius - radialGeometry._radius);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_radius);
    }
}
