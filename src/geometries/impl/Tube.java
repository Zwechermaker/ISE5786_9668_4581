package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        if (!super.equals(obj)) return false; // if there is inheritance
        Tube tube = (Tube) obj;
        return Objects.equals(_axis, tube._axis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _axis);
    }
}
