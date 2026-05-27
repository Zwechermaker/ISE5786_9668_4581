package lighting.api;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * an interface that defines a light source
 */
public interface LightSource {
    /**
     * an intensity calculator method
     * @param point of the intensity
     * @return the color of the light
     */
     public abstract Color getIntensity(Point point);

    /**
     * calculates the direction vector
     * @param point the point of the light source
     * @return the normalized direction vector
     */
     public abstract Vector getL(Point point);

    /**
     * calculates the distance between a light source and a point.
     * @param point the point to find the distance to.
     * @return distance
     */
     public abstract double getDistance(Point point);
}
