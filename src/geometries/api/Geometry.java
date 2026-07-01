package geometries.api;

import primitives.*;

import java.util.LinkedList;
import java.util.List;

/**
 * An abstract class representing a geometric shape in 3D space.
 * It extends {@link Intersectable} and adds properties common to all geometries,
 * such as emission color and material.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public abstract class Geometry extends Intersectable {
    /**
     * The emission color of the geometry, representing light emitted from the surface.
     * Defaults to black (no emission).
     */
    private Color _emission = Color.BLACK;

    /**
     * The material properties of the geometry, defining how it interacts with light.
     */
    private Material _material = new Material();

    /**
     * Default constructor for a geometry.
     */
    public Geometry() {
    }

    /**
     * Calculates the normal vector to the surface of the geometry at a given point.
     * The normal is essential for lighting and shading calculations.
     *
     * @param point A point on the surface of the geometry.
     * @return The normal vector at the specified point.
     */
    public abstract Vector getNormal(Point point);

    /**
     * Sets the emission color of the geometry.
     *
     * @param emission The emission color.
     * @return This {@link Geometry} object, allowing for method chaining.
     */
    public Geometry setEmission(Color emission) {
        _emission = emission;
        return this;
    }

    /**
     * Gets the emission color of the geometry.
     *
     * @return The emission color.
     */
    public Color getEmission() {
        return _emission;
    }

    /**
     * Sets the material of the geometry.
     *
     * @param material The material to set.
     * @return This {@link Geometry} object, allowing for method chaining.
     */
    public Geometry setMaterial(Material material) {
        _material = material;
        return this;
    }

    /**
     * Gets the material of the geometry.
     *
     * @return The material.
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * A helper function that constructs a list of {@link Intersection} points from distances along a ray.
     * It filters out intersections that are behind the ray's origin or beyond a specified maximum distance.
     *
     * @param ray         The ray for which to calculate the intersection points.
     * @param maxDistance The maximum distance to consider for valid intersections.
     * @param tValues     The distances (parameters) from the ray's origin to the intersection points.
     * @return A {@link List} of valid {@link Intersection} objects, or {@code null} if no valid intersections are found.
     */
    protected List<Intersection> getPoints(Ray ray, double maxDistance, double... tValues) {
        List<Intersection> result = null;
        for (double t : tValues) {
            if (Util.alignZero(t) > 0 && Util.alignZero(t - maxDistance) <= 0) {
                if (result == null) result = new LinkedList<>();
                result.add(new Intersection(this, ray.getPoint(t)));
            }
        }
        return result;
    }
}
