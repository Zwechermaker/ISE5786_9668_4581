package renderer;

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
        List<Point> intersections = _scene.geometries.findIntersections(ray);
        if (intersections == null) {
            return _scene.backGround;
        }
        return calcColor(ray.findClosestPoint(intersections));
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
    private Color calcColor(Point intersection){
        return _scene.ambient.intensity();
    }

}
