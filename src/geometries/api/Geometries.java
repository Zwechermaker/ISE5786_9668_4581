package geometries.api;

import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A composite class that represents a collection of intersectables.
 */
public class Geometries extends Intersectable{
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
    public List<Point> findIntersections(Ray ray){
        List<Point> totalList = null;
        for (Intersectable geometry : geometries) {
            var list = geometry.findIntersections(ray);
            if (list != null)
                if (totalList == null)
                    totalList = new LinkedList<>(list);
                else
                    totalList.addAll(list);
        }
        return totalList;
    }
}
