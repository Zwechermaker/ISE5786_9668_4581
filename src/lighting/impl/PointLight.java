package lighting.impl;

import lighting.api.Light;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * A class representing a point light source, which emits light equally in all directions from a single point.
 * <p>
 * The intensity of a point light attenuates with distance, following a quadratic model.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class PointLight extends Light implements LightSource {
    /**
     * The position of the light source in 3D space.
     */
    protected Point _position;
    /**
     * The constant attenuation factor. This is the default factor that is always applied.
     */
    private double _kC = 1;
    /**
     * The linear attenuation factor. This factor is multiplied by the distance.
     */
    private double _kL = 0;
    /**
     * The quadratic attenuation factor. This factor is multiplied by the square of the distance.
     */
    private double _kQ = 0;

    /**
     * Constructs a {@link PointLight} with a specified intensity and position.
     *
     * @param intensity The color representing the intensity of the light.
     * @param position  The position of the light source in 3D space.
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this._position = position;
    }

    /**
     * Sets the constant attenuation factor {@code kC}.
     *
     * @param kC The constant attenuation factor.
     * @return This {@link PointLight} object, allowing for method chaining.
     */
    public PointLight setKc(double kC) {
        this._kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation factor {@code kL}.
     *
     * @param kL The linear attenuation factor.
     * @return This {@link PointLight} object, allowing for method chaining.
     */
    public PointLight setKl(double kL) {
        this._kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor {@code kQ}.
     *
     * @param kQ The quadratic attenuation factor.
     * @return This {@link PointLight} object, allowing for method chaining.
     */
    public PointLight setKq(double kQ) {
        this._kQ = kQ;
        return this;
    }

    /**
     * Calculates the intensity of the point light at a specific point.
     * <p>
     * The intensity is attenuated based on the distance {@code d} from the light source,
     * according to the formula:
     * <pre>
     * I = I0 / (kC + kL*d + kQ*d^2)
     * </pre>
     * where {@code I0} is the base intensity, and {@code kC, kL, kQ} are the attenuation factors.
     *
     * @param point The point at which to calculate the light intensity.
     * @return The attenuated {@link Color} representing the light's intensity at that point.
     */
    @Override
    public Color getIntensity(Point point) {
        double d = _position.distance(point);
        double attenuation = 1 / (_kC + _kL * d + _kQ * d * d);
        return _intensity.scale(attenuation);
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
