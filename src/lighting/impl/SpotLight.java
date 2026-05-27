package lighting.impl;

import primitives.Color;
import primitives.Point;
import primitives.Util;
import primitives.Vector;

/**
 * class of the spot light
 */
public class SpotLight extends PointLight {
    /** a vector that defines the direction of light */
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
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        if (p.equals(_position)) {
            return super.getIntensity(p);
        }
        double projection = _direction.dotProduct(getL(p));
        if (Util.alignZero(projection) <= 0) {
            return Color.BLACK;
        }
        return super.getIntensity(p).scale(projection);
    }
}