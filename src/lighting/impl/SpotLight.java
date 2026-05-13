package lighting.impl;

import primitives.Color;
import primitives.Point;
import primitives.Util;
import primitives.Vector;

/**
 * class of the spot light
 */
public class SpotLight extends AmbientLight.PointLight {
    private final Vector _direction;

    /**
     * a constructor for spot light.
     * @param intensity the intensity of the light
     * @param position  the position of the light source
     * @param direction the direction of the light
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this._direction = direction.normalize();
    }

    @Override
    public Color getIntesity(Point p) {
        double projection = _direction.dotProduct(getL(p));
        if (Util.isZero(projection) || projection < 0) {
            return Color.BLACK;
        }
        return super.getIntesity(p).scale(projection);
    }

}
