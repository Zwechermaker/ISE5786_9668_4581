package geometries.impl;

import geometries.api.Intersectable;
import primitives.BoundingBox;
import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A composite class that represents a collection of {@link Intersectable} objects.
 * This class can also manage a Bounding Volume Hierarchy (BVH) for acceleration.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class Geometries extends Intersectable {

    private final List<Intersectable> geometries = new ArrayList<>();
    private static final int MAX_OBJECTS_IN_LEAF = 2;

    private boolean cbrEnabled = false;
    private boolean bvhEnabled = false;

    /**
     * Constructs a {@link Geometries} collection.
     *
     * @param geometries A variable number of {@link Intersectable} objects to add.
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Enables or disables Conservative Bounding Region (CBR).
     *
     * @param enabled true to enable CBR.
     * @return This {@link Geometries} object for chaining.
     */
    public Geometries setCbrEnabled(boolean enabled) {
        this.cbrEnabled = enabled;
        return this;
    }

    /**
     * Enables or disables Bounding Volume Hierarchy (BVH).
     * Enabling BVH will also implicitly enable CBR.
     *
     * @param enabled true to enable BVH.
     * @return This {@link Geometries} object for chaining.
     */
    public Geometries setBvhEnabled(boolean enabled) {
        this.bvhEnabled = enabled;
        if (enabled) {
            this.cbrEnabled = true;
        }
        return this;
    }

    /**
     * Adds intersectable objects to the collection.
     *
     * @param geometries A variable number of objects to add.
     */
    public void add(Intersectable... geometries) {
        if (geometries != null) {
            this.geometries.addAll(List.of(geometries));
        }
    }

    /**
     * Builds the acceleration structures (CBR or BVH) based on the flags set.
     * This method should be called after all geometries have been added and before rendering.
     */
    public void buildAccelerationStructures() {
        if (bvhEnabled) {
            buildBVH();
        } else if (cbrEnabled) {
            createBoundingBox();
        }
    }

    @Override
    public void createBoundingBox() {
        if (geometries.isEmpty()) {
            box = null;
            return;
        }

        for (Intersectable geo : geometries) {
            geo.createBoundingBox();
        }

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Intersectable geo : geometries) {
            if (geo.box != null) {
                minX = Math.min(minX, geo.box.min.getX());
                minY = Math.min(minY, geo.box.min.getY());
                minZ = Math.min(minZ, geo.box.min.getZ());
                maxX = Math.max(maxX, geo.box.max.getX());
                maxY = Math.max(maxY, geo.box.max.getY());
                maxZ = Math.max(maxZ, geo.box.max.getZ());
            }
        }

        box = Double.isInfinite(minX) ? null : new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Builds the Bounding Volume Hierarchy tree.
     */
    public void buildBVH() {
        if (geometries.isEmpty()) return;

        createBoundingBox(); // Ensure all children have boxes first

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
        createBoundingBox(); // Recalculate the root box
    }

    private Geometries buildBVHRecursive(List<Intersectable> objects, int depth) {
        // This is the critical fix: The new node must be configured for BVH.
        Geometries node = new Geometries().setBvhEnabled(true);

        if (objects.size() <= MAX_OBJECTS_IN_LEAF) {
            node.add(objects.toArray(new Intersectable[0]));
            node.createBoundingBox();
            return node;
        }

        int axis = depth % 3;
        objects.sort(Comparator.comparingDouble(o -> o.box.getCenter().getCoord(axis)));

        int mid = objects.size() / 2;
        List<Intersectable> left = new ArrayList<>(objects.subList(0, mid));
        List<Intersectable> right = new ArrayList<>(objects.subList(mid, objects.size()));

        node.add(buildBVHRecursive(left, depth + 1));
        node.add(buildBVHRecursive(right, depth + 1));
        node.createBoundingBox();
        return node;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        // This check is now sufficient. For BVH, it will be called recursively.
        // For CBR, it will be called only once at the top level.
        if (cbrEnabled && box != null && !box.intersects(ray)) {
            return null;
        }

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
        return "Geometries{" + "geometries=" + geometries + '}';
    }
}
