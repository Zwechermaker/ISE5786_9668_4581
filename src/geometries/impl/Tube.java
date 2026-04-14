package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;
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
    public Vector getNormal(Point point) {
        double projection = _axis.direction().dotProduct(point.subtract(_axis.origin()));
        if (Util.isZero(projection)) {
            return point.subtract(_axis.origin()).normalize();
        }
        return getNormal(point, projection); // Pass it to the helper
    }

    /**
     * A helper method for getting the normal of the tube
     * (saves time by reducing number of projection calculations)
     * @param point the point to calculate the normal for
     * @param projection the length of the projection of the point on the axis
     * @return the normal of the tube
     */
    protected Vector getNormal(Point point, double projection) {
        Vector scaledDirection = _axis.direction().scale(projection);
        Point projectionPoint = _axis.origin().add(scaledDirection);
        return point.subtract(projectionPoint).normalize();
    }
    @Override
    public List<Point> findIntersections(Ray ray){
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
