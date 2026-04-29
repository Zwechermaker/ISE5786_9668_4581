package renderer;

import primitives.Color;
import primitives.Ray;

public class SimpleRayTracer extends RayTracerBase {

    @Override
    public Color traceRay(Ray ray) {
        return Color.BLACK;
    }

    /**
     * Constructor for SimpleRayTracer
     * @param scene the scene to trace rays in
     */
    public SimpleRayTracer(scene.Scene scene) {
        super(scene);
    }


}
