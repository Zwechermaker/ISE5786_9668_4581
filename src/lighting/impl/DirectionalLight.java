package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * class of the directional light
 */
public class DirectionalLight extends Light implements LightSource {
   private final Vector _direction;

    /**
     * a constructor for directional light.
     * @param intensity the intensity of the light
     * @param direction the direction the light points to
     */
   public DirectionalLight(Color intensity, Vector direction) {
       super(intensity);
       this._direction = direction.normalize();
   }

   @Override
   public Color getIntensity(Point point){
       return _intensity;
   }

    @Override
    public Vector getL(Point point) {
        return _direction;
    }
}
