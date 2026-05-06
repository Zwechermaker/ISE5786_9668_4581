package geometries.api;
import primitives.Point;
import primitives.Ray;
import java.util.List;
import java.util.Objects;

/**
 * an abstract class that defines the properties of  intersectable objects
 */
public abstract class Intersectable {
    /**
     * a function that converts intersection lists back to the old API
     * (backward compatibility)
     * @param ray the ray that intersects the object
     * @return a list of intersection points (without the geometry)
     */
    public List<Point> findIntersections(Ray ray){
        var intersections = calcIntersections(ray);

        //use lambda expression in order convert
        //each intersection into a point element.

        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point).toList();
    }

    /**
     * a function that calculates ray and object intersections
     * @param ray the ray that intersects the object
     * @return a list of intersections (point and geometry)
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }
    /**
     * a function that calculates ray and object intersections
     * @param ray the ray that intersects the object
     * @return a list of intersections (point and geometry)
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);


    /**
     * a class that describes an intersection with geometry intersected and point.
     */
    public static class Intersection {
        /**
         * geometry intersected
         */
        public final Geometry geometry;
        /**
         * point of intersection
         */
        public final Point point;

        /**
         * a constructor for an intersection
         * @param geometry
         * @param point
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public String toString() {
            return geometry.toString() + " " + point.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Intersection intersection = (Intersection) obj;

            return Objects.equals(geometry, intersection.geometry) &&
                    Objects.equals(point, intersection.point);
        }
    }
    /** Default constructor to satisfy JavaDoc generator */
   public Intersectable() { /* to satisfy JavaDoc generator */ }
}
