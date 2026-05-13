package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * class of the ambient light
 */
public final class AmbientLight extends Light {

    /**
     * a black ambient light - no ambient light
     */
    public final static AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * a constructor for the intensity
     * @param color the ambient light color
     */
    public AmbientLight(Color color) {
        super(color);
    }
}
