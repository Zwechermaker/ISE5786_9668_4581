package lighting.api;

import primitives.Color;

/**
 * an abstract class that describes
 */
public abstract class Light {
    /**
     * the intensity of the light
     */
    protected final Color _intensity;

    /**
     * a builder for Light
     * @param color of the light
     */
    protected Light(Color color){
        _intensity = color;
    }
    /**
     * getter for intensity
     * @return the intensity
     */
    public Color getIntensity() {
        return _intensity;
    }

}
