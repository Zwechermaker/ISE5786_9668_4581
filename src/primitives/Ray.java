package primitives;

/**
 * A class that describes a Ray, an infinite line in space that starts at a point.
 */
public class Ray {
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

    @Override
    public String toString() {
        return "_origin: " + _origin + ", _direction: " + _direction + "\n";
    }
}
