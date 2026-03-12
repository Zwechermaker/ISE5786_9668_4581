package geometries.impl;

import primitives.Ray;
import primitives.Vector;

public class Tube extends RadialGeometry{
    private final Ray exis;

    /**
     * A parameter constructor for a tube
     * @param radius the radius of the tube
     * @param axis the axis of the tube
     */
    public Tube(double radius,Ray axis){
        super(radius);
        exis=axis;
    }

    @Override
    public Vector getNormal(){
        return null;
    }
}
