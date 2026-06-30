package geometries.impl;

import geometries.api.Geometry;
import primitives.Util;

import java.util.Objects;

/**
 * An abstract class representing a geometry with a radial property, such as a sphere or a tube.
 * <p>
 * This class provides a common base for geometries that are defined by a radius.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public abstract class RadialGeometry extends Geometry {
    /**
     * The radius of the radial geometry.
     */
    protected final double _radius;

    /**
     * The squared radius of the radial geometry, pre-calculated for performance optimization in intersection calculations.
     */
    protected final double _radiusSquared;

    /**
     * Constructs a {@link RadialGeometry} with a specified radius.
     *
     * @param radius The radius of the geometry. Must be a positive value.
     * @throws IllegalArgumentException if the radius is not a positive number.
     */
    public RadialGeometry(double radius) {
        if (Util.alignZero(radius) <= 0) {
            throw new IllegalArgumentException("Radius must be a positive number.");
        }
        this._radius = radius;
        _radiusSquared = radius * radius;
    }

    /**
     * Returns the radius of the geometry.
     *
     * @return The radius.
     */
    public double getRadius() {
        return _radius;
    }

    @Override
    public String toString() {
        return "radius: " + _radius;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RadialGeometry that = (RadialGeometry) obj;
        return Util.isZero(_radius - that._radius);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_radius);
    }
}
