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
        Vector vOrthogonal;
        try {
            vOrthogonal = ray.direction().orthogonalComponent(_axis.direction());
        } catch (IllegalArgumentException e){
            //a zero vector is created only if the ray is parallel to the axis
            return null;
        }

        //calculate the coefficients of the quadratic equation:
        double a = vOrthogonal.lengthSquared();
        double b = 0;
        double c = -_radiusSquared;



            try {
                // if ray origin is axis origin it will throw an error.
                Vector deltaOrigin = ray.origin().subtract(_axis.origin());

                Vector deltaOriginOrthogonal = deltaOrigin.orthogonalComponent(_axis.direction());

                b = 2 * vOrthogonal.dotProduct(deltaOriginOrthogonal);
                c = deltaOriginOrthogonal.lengthSquared() - _radiusSquared;

            } catch (IllegalArgumentException e) {
                // if delta origin is parallel to the axis or starts at the center base
                // we claim that b = 0 and c is -radiusSquared.
            }
        double discriminant = b * b - 4 * a * c;

        if (Util.alignZero(discriminant) <= 0){
            return null;
        }

        double discriminantSquareRoot = Math.sqrt(discriminant);

        double t1 = (-b - discriminantSquareRoot) / (2 * a);
        double t2 = (-b + discriminantSquareRoot) / (2 * a);

        return ray.getPoints(t1, t2);
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
