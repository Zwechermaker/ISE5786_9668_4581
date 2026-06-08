package geometries.impl;

import geometries.api.Intersectable;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A composite class that represents a collection of {@link Intersectable} objects.
 * <p>
 * This class allows multiple geometric objects to be grouped and treated as a single
 * {@link Intersectable} entity. It is useful for managing complex scenes with many objects.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class Geometries extends Intersectable {
    /**
     * A list to hold the {@link Intersectable} objects in the collection.
     */
    private final List<Intersectable> geometries = new ArrayList<>();

    /**
     * Constructs a {@link Geometries} collection with an initial set of intersectable objects.
     *
     * @param geometries A variable number of {@link Intersectable} objects to add to the collection.
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more {@link Intersectable} objects to the collection.
     *
     * @param geometries A variable number of {@link Intersectable} objects to add.
     */
    public void add(Intersectable... geometries) {
        if (geometries != null) {
            this.geometries.addAll(Arrays.asList(geometries));
        }
    }

    /**
     * Calculates the intersections of a ray with all the geometries in the collection.
     * <p>
     * This method iterates through each {@link Intersectable} in the collection and
     * aggregates their individual intersection points into a single list.
     *
     * @param ray         The ray to intersect with the geometries.
     * @param maxDistance The maximum distance to consider for intersections.
     * @return A {@link List} of all {@link Intersection} points, or {@code null} if no intersections are found.
     */
    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        for (Intersectable geo : geometries) {
            List<Intersection> intersections = geo.calcIntersections(ray, maxDistance);
            if (intersections != null) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(intersections);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "geometries: [\n";

        for (Intersectable geo : geometries) {
            result += "  " + geo.toString().replace("\n", "\n  ") + "\n";
        }
        result += "]";
        return result;
    }
}
