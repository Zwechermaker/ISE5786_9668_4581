package renderer;

/**
 * Enumerates the available types of ray tracers.
 * <p>
 * This enum is used to select the desired ray tracing strategy when configuring the renderer.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public enum RayTracerType {
    /**
     * A basic, non-optimized ray tracer.
     */
    SIMPLE,

    /**
     * A ray tracer that uses a regular grid acceleration structure for improved performance.
     */
    GRID
}
