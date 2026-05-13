package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource {
    protected Point _position;
    private double _kC = 1;
    private double _kL = 0;
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

    /**
     * setter for the narrow beam
     * @param beamWidth the width of the beam
     * @return the point light object
     */
    public DirectionalLight setNarrowBeam(double beamWidth){
        return null;
    }

    @Override
    public Color getIntensity(Point point){
        double d = _position.distance(point);
        return _intensity.scale(_kC + _kL * d + _kQ * d * d);
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(_position).normalize();
    }

}