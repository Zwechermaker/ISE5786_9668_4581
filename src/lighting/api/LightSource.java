package lighting.api;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * An interface representing a light source in a 3D scene.
 * <p>
 * This interface defines the essential properties and behaviors of any light source,
 * such as its intensity at a given point, its direction, and its distance from a point.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public interface LightSource {
    /**
     * Calculates the intensity of the light at a specific point in the scene.
     * For some lights (like directional lights), this may be constant, while for others
     * (like point lights), it may vary with distance.
     *
     * @param point The point at which to calculate the light intensity.
     * @return The {@link Color} representing the light's intensity at that point.
     */
    Color getIntensity(Point point);

    /**
     * Calculates the direction vector from the light source to a given point.
     * This vector should be normalized.
     *
     * @param point The point to which the light direction is calculated.
     * @return The normalized direction vector {@code L} from the light to the point.
     */
    Vector getL(Point point);

    /**
     * Calculates the distance from the light source to a given point.
     * For some lights (like directional lights), this distance can be considered infinite.
     *
     * @param point The point to which the distance is calculated.
     * @return The distance from the light source to the point.
     */
    double getDistance(Point point);
}
