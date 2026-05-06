package lighting;

import primitives.Color;

/**
 * class of the ambient light
 */
public final class AmbientLight {
    /**
     * the intensity of the ambient light
     */
    private  Color intensity ;
    /**
     * a black ambient light - no ambient light
     */
    public final static AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * a constructor for the intensity
     * @param color the ambient light color
     */
    public AmbientLight(Color color) {
        intensity = color;
    }

    /**
     * getter fot intensity
     * @return the intensity
     */
    public Color getIntensity() {
        return intensity;
    }
}
