package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * an abstract class
 * that describes the required functionality of a ray tracer
 */
abstract class RayTracerBase {
    /**
     * a scene variable to trace rays in.
     */
    protected final Scene _scene;

    /**
     * a tracing function for a ray that returns the color its received.
     * @param ray the ray to trace in the scene.
     * @return the color the ray traced
     */
    abstract Color traceRay(Ray ray);

    /**
     * Constructor for RayTracerBase
     * @param scene the scene to trace rays in
     */
    RayTracerBase(Scene scene) {
         this._scene = scene;
     }


}
