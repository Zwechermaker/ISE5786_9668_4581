package renderer;

import geometries.api.Intersectable;
import primitives.Color;
import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * a class that describes a basic ray tracer.
 */
public class SimpleRayTracer extends RayTracerBase {

    @Override
    public Color traceRay(Ray ray) {
        //does not violate the law of demeter because scene is a passive data structure
        List<Point> intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null
                ? _scene.background
                : calcColor(ray.findClosestIntersection(intersections));
    }

    /**
     * Constructor for SimpleRayTracer
     * @param scene the scene to trace rays in
     */
    public SimpleRayTracer(scene.Scene scene) {
        super(scene);
    }

    /**
     * a function that returns the color received in an intersection
     * @param intersection a point the intersection occurred at and calculate the color the intersection holds.
     * @return the color the intersection holds
     */
    private Color calcColor(Intersectable.Intersection intersection){
        return _scene.ambientLight.getIntensity()
                .add(intersection.geometry.getEmission());
    }

}
