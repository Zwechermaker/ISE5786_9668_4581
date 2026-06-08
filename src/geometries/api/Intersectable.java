package geometries.api;

import lighting.api.LightSource;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;
import java.util.Objects;

/**
 * An abstract class representing any object that can be intersected by a {@link Ray}.
 * <p>
 * This class provides a unified interface for calculating intersections with geometric objects.
 * It includes methods for finding intersection points and returning detailed {@link Intersection} data,
 * which is essential for rendering processes like ray tracing.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public abstract class Intersectable {
    /**
     * Default constructor for the {@link Intersectable} class.
     * This constructor is provided to satisfy the JavaDoc generator and for basic initialization.
     */
    public Intersectable() { /* to satisfy JavaDoc generator */ }

    /**
     * Finds the intersection points of a ray with the object.
     * <p>
     * This method is provided for backward compatibility with older APIs that only required the points of intersection.
     * It delegates to the more detailed intersection calculation, discarding extra information.
     *
     * @param ray The ray to intersect with the object.
     * @return A {@link List} of intersection points, or {@code null} if there are no intersections.
     */
    public final List<Point> findIntersections(Ray ray) {
        return findIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Finds the intersection points of a ray with the object, up to a maximum distance.
     * <p>
     * This method is provided for backward compatibility and is useful for scenarios like shadow calculation,
     * where intersections beyond a certain distance (e.g., to the light source) are irrelevant.
     *
     * @param ray         The ray to intersect with the object.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} of intersection points within the given distance, or {@code null} if there are no such intersections.
     */
    public final List<Point> findIntersections(Ray ray, double maxDistance) {
        var intersections = calcIntersections(ray, maxDistance);

        // Use a lambda expression to map each Intersection object to its Point component.
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point).toList();
    }

    /**
     * Calculates the detailed intersections of a ray with the object.
     * This method returns a list of {@link Intersection} objects, which include both the intersection point
     * and a reference to the geometry that was hit.
     *
     * @param ray The ray to intersect with the object.
     * @return A {@link List} of {@link Intersection} objects, or {@code null} if there are no intersections.
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Calculates the detailed intersections of a ray with the object, up to a maximum distance.
     *
     * @param ray         The ray to intersect with the object.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} of {@link Intersection} objects within the given distance, or {@code null} if there are no such intersections.
     */
    public final List<Intersection> calcIntersections(Ray ray, double maxDistance) {
        return calcIntersectionsHelper(ray, maxDistance);
    }

    /**
     * An abstract helper method for calculating ray-object intersections.
     * Subclasses must implement this method to provide their specific intersection logic.
     *
     * @param ray         The ray to intersect with the object.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} of {@link Intersection} objects, or {@code null} if no intersections are found.
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance);

    /**
     * A static final inner class representing the intersection of a ray with a geometry.
     * <p>
     * This class is a passive data structure (struct-like) that holds all relevant information
     * about an intersection point, which is crucial for shading and other rendering calculations.
     * It includes:
     * <ul>
     *   <li>The intersected geometry.</li>
     *   <li>The point of intersection.</li>
     *   <li>The material of the geometry.</li>
     *   <li>Pre-calculated vectors and values for lighting calculations.</li>
     * </ul>
     *
     * @author Elad Zwecher and Benjamin Godfrey
     */
    public static final class Intersection {
        /**
         * The geometry that was intersected.
         */
        public final Geometry geometry;
        /**
         * The point where the intersection occurred.
         */
        public final Point point;

        /**
         * The material of the intersected geometry, extracted for convenience.
         */
        public final Material material;

        /**
         * The normal vector at the point of intersection.
         * This vector is perpendicular to the surface at the intersection point.
         */
        public Vector normal;
        /**
         * The direction vector of the ray that caused the intersection.
         */
        public Vector v;
        /**
         * The dot product of the normal and the ray's direction vector (v).
         * This value is pre-calculated for lighting efficiency.
         */
        public double vNormal;
        /**
         * The light source being considered for shading at this intersection.
         */
        public LightSource light;
        /**
         * The direction vector from the intersection point to the light source.
         */
        public Vector l;
        /**
         * The dot product of the normal and the light's direction vector (l).
         * This value is pre-calculated for lighting efficiency.
         */
        public double lNormal;

        /**
         * Constructs an {@link Intersection} object.
         *
         * @param geometry The geometry that the ray intersected with.
         * @param point    The point where the ray intersected the geometry.
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = geometry == null ? new Material() : geometry.getMaterial();
        }

        @Override
        public String toString() {
            return geometry.toString() + " " + point.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Intersection that = (Intersection) obj;

            return Objects.equals(geometry, that.geometry) &&
                    Objects.equals(point, that.point);
        }
    }
}
