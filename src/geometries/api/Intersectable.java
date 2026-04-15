package geometries.api;
import primitives.Point;
import primitives.Ray;
import java.util.List;

/**
 * an abstract class that defines the properties of  intersectable objects
 */
public abstract class Intersectable {
    /**
     * a function that calculates ray and object intersections
     * @param ray the ray that intersects the object
     * @return a list of intersection points (null if there isn't any)
     */
    public abstract List<Point> findIntersections(Ray ray);

    /** Default constructor to satisfy JavaDoc generator */
   public Intersectable() { /* to satisfy JavaDoc generator */ }
}
