package lighting.api;

import primitives.Color;

public abstract class Light {
    /**
     * the intensity of the light
     */
    protected Color _intensity = Color.BLACK;

    /**
     * a builder for Light
     * @param color of the light
     */
    public Light(Color color){
        _intensity = color;
    }
    /**
     * getter for intensity
     * @return the intensity
     */
    public primitives.Color getIntensity() {
        return _intensity;
    }

}
