package geometries.impl;

import geometries.api.Geometry;
import primitives.Util;

public class RadialGeometry extends Geometry {
    protected final double radius ;
    protected final double radiusSquared;

    /**
     * constructor for radial geometry's
     * @param radius the radius of a radial geometry
     */
    public RadialGeometry(double radius){
        if(Util.alignZero(radius)<=0)
        {
            throw new IllegalArgumentException("Radius cannot be less or equal to 0.");
        }
        this.radius=radius;
        radiusSquared=Math.pow(radius,2);
    }

}
