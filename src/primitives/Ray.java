package primitives;

import java.util.List;
import java.util.Objects;

/**
 * A class that describes a Ray, an infinite line in space that starts at a point.
 */
public final class Ray {
    /**
     * the origin point of the ray
     */
    private final Point _origin ;

    /**
     * the direction vector of the ray
     */
    private final Vector _direction;

    /**
     * a constructor for Ray
     * @param point the origin point of the ray
     * @param vector the direction vector of the ray
     */
    public Ray(Point point, Vector vector) {
        _origin = point;
        _direction = vector.normalize();
    }

    /**
     * a getter for the direction of the ray
     * @return the direction of the ray
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * a getter for the origin of the ray
     * @return the origin of the ray
     */
    public Point origin() {
        return _origin;
    }

    /**
     * getting the point p_0+t*v
     * @param parameter for p_0+parameter*v
     * @return the point p_0+parameter*v
     */
    public Point getPoint(double parameter){
        try {
            return _origin.add(_direction.scale(parameter));
        } catch(IllegalArgumentException e){
            return _origin;
        }
    }

    /**
     * a function that receives 2 parameters and gets the points using the parameters by order.
     * only refers to positive parameters (ray intersects).
     * @param t1 a parameter to getPoint
     * @param t2 a parameter to getPoint
     * @return a list of points created by getPoint according to the order
     */
    public List<Point> getPoints(double t1, double t2){
        if (t2 < t1){
            //swap values.
            t1 += t2;
            t2 = t1 - t2;
            t1 -= t2;
        }
        if (t1 <= 0 && t2 <= 0) {
            return null;
        }

        if (t1 <= 0) {
            return List.of(getPoint(t2));
        }
        if (t2 <= 0) {
            return List.of(getPoint(t1));
        }

        //return the points, closest first.
        return List.of(getPoint(t1), getPoint(t2));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Ray ray = (Ray) obj;
        return Objects.equals(_origin, ray._origin)
                && Objects.equals(_direction, ray._direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }

    @Override
    public String toString() {
        return "_origin: " + _origin + ", _direction: " + _direction + "\n";
    }
}
