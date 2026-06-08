package lighting.impl;

import lighting.api.Light;
import primitives.Color;

/**
 * A class representing ambient light, which provides a uniform, non-directional illumination to a scene.
 * <p>
 * Ambient light simulates indirect lighting from the environment, ensuring that objects
 * are never in complete darkness. It has a constant intensity and color throughout the scene.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class AmbientLight extends Light {

    /**
     * A constant representing no ambient light (black color). This can be used as a default
     * or to disable ambient lighting in a scene.
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs an {@link AmbientLight} with a specified color and intensity.
     *
     * @param color The {@link Color} representing the ambient light's intensity.
     */
    public AmbientLight(Color color) {
        super(color);
    }
}
