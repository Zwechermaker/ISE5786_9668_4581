package geometries.impl;

import geometries.api.Intersectable;
import primitives.Ray;
import primitives.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A composite class that represents a collection of intersectables.
 */
public class Geometries extends Intersectable {
    /**
     * A list of intersectables.
     */
    private final List<Intersectable> geometries = new ArrayList<>();

    /**
     * A constructor for the composite.
     * @param geometries a list of geometries to initialize the composite with.
     */
    public Geometries(Intersectable... geometries){
                add(geometries);
    }

    /**
     * A method to add intersectables to the composite
     * @param geometries a list of geometries to add to the composite
     */
    public void add(Intersectable... geometries){
        if (geometries != null) {
            this.geometries.addAll(java.util.Arrays.asList(geometries));
        }
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {        List<Intersection> result = null;

        for (Intersectable geo : geometries) {
            List<Intersection> intersections = geo.calcIntersections(ray);
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
