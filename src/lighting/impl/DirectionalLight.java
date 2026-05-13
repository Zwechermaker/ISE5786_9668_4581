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
   private final Vector direction;

    /**
     * a constructor for directional light.
     * @param direction the direction the light points to
     * @param intensity the intensity of the light
     */
   public DirectionalLight(Vector direction , Color intensity) {
       super(intensity);
       this.direction = direction.normalize();
   }

   @Override
   public Color getIntensity(Point point){
       return null;
   }
}
