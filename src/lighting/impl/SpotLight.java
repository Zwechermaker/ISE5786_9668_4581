package lighting.impl;

import primitives.Color;
import primitives.Point;
import primitives.Util;
import primitives.Vector;

/**
 * A class representing a spotlight, a type of light source that emits light in a specific direction from a point.
 * <p>
 * A spotlight is a {@link PointLight} with a directional constraint. Its intensity is highest along its central
 * direction and diminishes as the angle from this direction increases.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class SpotLight extends PointLight {
    /**
     * The direction vector of the spotlight. This defines the central axis of the light cone.
     */
    private final Vector _direction;

    /**
     * A factor to control the narrowness of the spotlight's beam. A higher value results in a more focused beam.
     */
    private double _narrowBeam = 1;

    /**
     * Constructs a {@link SpotLight} with a specified intensity, position, and direction.
     *
     * @param intensity The color representing the intensity of the light.
     * @param position  The position of the spotlight in 3D space.
     * @param direction The direction in which the spotlight is aimed. The vector will be normalized.
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this._direction = direction.normalize();
    }

    /**
     * Sets the narrowness of the spotlight's beam.
     * <p>
     * The intensity of the spotlight is modulated by {@code (cos(alpha))^narrowBeam}, where {@code alpha} is the
     * angle between the light direction and the vector to the point. A higher {@code narrowBeam} value
     * concentrates the light into a smaller cone.
     *
     * @param narrowBeam The exponent for the beam concentration.
     * @return This {@link SpotLight} object, allowing for method chaining.
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this._narrowBeam = narrowBeam;
        return this;
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

    /**
     * Calculates the intensity of the spotlight at a specific point.
     * <p>
     * The intensity is determined by the base intensity of the point light, attenuated by distance,
     * and further modulated by the spotlight's directional cone. If the point is outside the
     * light cone, the intensity is black.
     *
     * @param p The point at which to calculate the light intensity.
     * @return The {@link Color} representing the spotlight's intensity at that point.
     */
    @Override
    public Color getIntensity(Point p) {
        // If the point is the same as the light's position, the behavior is inherited from PointLight.
        if (p.equals(_position)) {
            return super.getIntensity(p);
        }

        double projection = _direction.dotProduct(getL(p));

        // If the projection is non-positive, the point is behind the spotlight or at a 90-degree angle, so it receives no light.
        if (Util.alignZero(projection) <= 0) {
            return Color.BLACK;
        }

        // Apply the narrow beam effect if specified.
        if (_narrowBeam != 1) {
            projection = Math.pow(projection, _narrowBeam);
        }

        // The final intensity is the point light's intensity scaled by the directional factor.
        return super.getIntensity(p).scale(projection);
    }
}
