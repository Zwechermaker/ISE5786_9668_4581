package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * An abstract base class for ray tracers.
 * <p>
 * This class defines the fundamental contract for any ray tracer, which is to
 * calculate the color seen along a given ray in a scene.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
abstract class RayTracerBase {
    /**
     * The scene in which the rays are traced.
     */
    protected final Scene _scene;

    /**
     * Constructs a {@link RayTracerBase} with a specified scene.
     *
     * @param scene The scene to be rendered.
     */
    RayTracerBase(Scene scene) {
        this._scene = scene;
    }

    /**
     * Traces a ray through the scene and determines the color at the intersection point.
     *
     * @param ray The ray to trace.
     * @return The calculated {@link Color} for the ray.
     */
    abstract Color traceRay(Ray ray);

    /**
     * Returns the scene associated with the ray tracer.
     *
     * @return The scene.
     */
    public Scene getScene() {
        return _scene;
    }
}
