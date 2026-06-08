package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * A class representing a directional light source, which emits parallel light rays in a single direction.
 * <p>
 * Directional light simulates a light source that is infinitely far away, such as the sun.
 * It has a constant intensity and direction throughout the scene.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class DirectionalLight extends Light implements LightSource {
    /**
     * The direction vector of the light. All light rays are parallel to this vector.
     */
    private final Vector _direction;

    /**
     * Constructs a {@link DirectionalLight} with a specified intensity and direction.
     *
     * @param intensity The color representing the intensity of the light.
     * @param direction The direction in which the light is aimed. The vector will be normalized.
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this._direction = direction.normalize();
    }

    /**
     * Returns the intensity of the directional light.
     * For a directional light, the intensity is constant at every point in the scene.
     *
     * @param point The point at which to calculate the intensity (ignored).
     * @return The constant {@link Color} representing the light's intensity.
     */
    @Override
    public Color getIntensity(Point point) {
        return _intensity;
    }

    /**
     * Returns the direction vector of the light.
     * For a directional light, this vector is constant.
     *
     * @param point The point to which the light direction is calculated (ignored).
     * @return The constant, normalized direction vector of the light.
     */
    @Override
    public Vector getL(Point point) {
        return _direction;
    }

    /**
     * Returns the distance from the light source to a given point.
     * For a directional light, the distance is considered to be infinite.
     *
     * @param point The point to which the distance is calculated (ignored).
     * @return {@link Double#POSITIVE_INFINITY}.
     */
    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY;
    }
}
