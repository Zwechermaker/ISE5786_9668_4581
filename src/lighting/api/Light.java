package lighting.api;

import primitives.Color;

/**
 * An abstract class representing a light source in a scene.
 * <p>
 * This class serves as a base for all types of lights, providing the fundamental
 * property of light intensity, represented by a {@link Color}.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public abstract class Light {
    /**
     * The intensity of the light, represented as a {@link Color}.
     * The RGB components of the color determine the strength and hue of the light.
     */
    protected final Color _intensity;

    /**
     * Constructs a {@link Light} with a specified intensity.
     *
     * @param intensity The color representing the intensity of the light.
     */
    protected Light(Color intensity) {
        _intensity = intensity;
    }

    /**
     * Retrieves the intensity of the light.
     *
     * @return The {@link Color} representing the light's intensity.
     */
    public Color getIntensity() {
        return _intensity;
    }
}
