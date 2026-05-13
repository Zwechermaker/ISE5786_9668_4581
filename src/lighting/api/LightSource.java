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
     * calculatea the direction vector
     * @param point the point of the light source
     * @return the vector distance
     */
     public abstract Vector getL(Point point);
}
