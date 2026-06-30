package geometries.impl;

import geometries.api.Intersectable;
import primitives.BoundingBox;
import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    private static final int MAX_OBJECTS_IN_LEAF = 2;

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
            this.geometries.addAll(List.of(geometries));
        }
    }

    @Override
    public void createBoundingBox() {
        if (geometries.isEmpty()) {
            return;
        }

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Intersectable geo : geometries) {
            geo.createBoundingBox();
            if (geo.box != null) {
                minX = Math.min(minX, geo.box.min.getX());
                minY = Math.min(minY, geo.box.min.getY());
                minZ = Math.min(minZ, geo.box.min.getZ());
                maxX = Math.max(maxX, geo.box.max.getX());
                maxY = Math.max(maxY, geo.box.max.getY());
                maxZ = Math.max(maxZ, geo.box.max.getZ());
            }
        }
        if (Double.isInfinite(minX)) {
            box = null;
        } else {
            box = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    public void buildBVH() {
        if (geometries.size() <= MAX_OBJECTS_IN_LEAF) {
            return;
        }

        List<Intersectable> finites = new ArrayList<>();
        List<Intersectable> infinites = new ArrayList<>();

        for (Intersectable geo : geometries) {
            if (geo.box != null) {
                finites.add(geo);
            } else {
                infinites.add(geo);
            }
        }

        geometries.clear();
        geometries.addAll(infinites);

        if (!finites.isEmpty()) {
            geometries.add(buildBVHRecursive(finites, 0));
        }
        createBoundingBox();
    }

    private Geometries buildBVHRecursive(List<Intersectable> objects, int depth) {
        if (objects.size() <= MAX_OBJECTS_IN_LEAF) {
            Geometries leaf = new Geometries();
            leaf.add(objects.toArray(new Intersectable[0]));
            leaf.createBoundingBox();
            return leaf;
        }

        int axis = depth % 3;
        objects.sort(Comparator.comparingDouble(o -> o.box.getCenter().getCoord(axis)));

        int mid = objects.size() / 2;
        List<Intersectable> left = objects.subList(0, mid);
        List<Intersectable> right = objects.subList(mid, objects.size());

        Geometries node = new Geometries();
        node.add(buildBVHRecursive(new ArrayList<>(left), depth + 1));
        node.add(buildBVHRecursive(new ArrayList<>(right), depth + 1));
        node.createBoundingBox();
        return node;
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
                    result = new LinkedList<>();
                }
                result.addAll(intersections);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "geometries: [\\n";

        for (Intersectable geo : geometries) {
            result += "  " + geo.toString().replace("\\n", "\\n  ") + "\\n";
        }
        result += "]";
        return result;
    }
}
