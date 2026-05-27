package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * a class that describes a point light.
 */
public class PointLight extends Light implements LightSource {
    /**
     * a point that keeps the location of the light.
     */
    protected Point _position;
    /**
     * constant attenuation factor based on distance.
     */
    private double _kC = 1;
    /**
     * linear attenuation factor based on distance.
     */
    private double _kL = 0;
    /**
     * quadrant attenuation factor based on distance.
     */
    private double _kQ = 0;

    /**
     * a constructor for point light.
     * @param intensity the intensity of the light
     * @param position  the position of the light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this._position = position;
    }

    /**
     * setter for kC
     * @param kC the constant attenuation factor
     * @return the point light object
     */
    public PointLight setKc(double kC) {
        this._kC = kC;
        return this;
    }

    /**
     * setter for kL
     * @param kL the linear attenuation factor
     * @return the point light object
     */
    public PointLight setKl(double kL) {
        this._kL = kL;
        return this;
    }

    /**
     * setter for kQ
     * @param kQ the quadratic attenuation factor
     * @return the point light object
     */
    public PointLight setKq(double kQ) {
        this._kQ = kQ;
        return this;
    }

    @Override
    public Color getIntensity(Point point){
        double d = _position.distance(point);
        return _intensity.scale(1 / (_kC + _kL * d + _kQ * d * d));
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }

    @Override
    public double getDistance(Point point) {
        return _position.distance(point);
    }
}