package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * A class that represents a tube in space.
 */
public class Tube extends RadialGeometry{
    /**
     * The axis of the tube.
     */
    protected final Ray _axis;

    /**
     * A parameter constructor for a tube
     * @param radius the radius of the tube
     * @param axis the axis of the tube
     */
    public Tube(double radius,Ray axis){
        super(radius);
        _axis = axis;
    }

    @Override
    public Vector getNormal(Point point){
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "axis: " + _axis + "\n";
    }
}
