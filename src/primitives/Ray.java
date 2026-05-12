package primitives;

import geometries.api.Intersectable;

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
        return "Ray(origin=" + _origin + ", direction=" + _direction + ")";
    }

    /**
     * find the closest intersection.
     * @param intersections a list of intersections
     * @return the closest intersection
     */
    public Intersectable.Intersection findClosestIntersection(List<Intersectable.Intersection> intersections){
        if(intersections == null){
            return null;
        }
        Intersectable.Intersection currentClosestIntersection = null;
        double closestCurentDistance = Double.POSITIVE_INFINITY;
        for(Intersectable.Intersection currentIntersection : intersections){
            if(currentIntersection.point.distanceSquared(_origin) < closestCurentDistance){
                closestCurentDistance = currentIntersection.point.distanceSquared(_origin);
                currentClosestIntersection = currentIntersection;
            }
        }
        return currentClosestIntersection;
    }
    /**
     * finds the closest point in the list
     * @param points the list of points
     * @return the closest point
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(
                points.stream()
                        .map(point -> new Intersectable.Intersection(null, point)).toList()
        ).point;
    }
}
