package primitives;

/**
 * A class representing a vector in 3D Cartesian space.
 * <p>
 * A vector represents a direction and magnitude, but not a position. It is distinct
 * from a {@link Point}. This class extends {@link Point} for implementation reasons
 * but enforces that a zero vector cannot be created.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class Vector extends Point {
    /**
     * A constant representing a unit vector along the positive X-axis.
     */
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    /**
     * A constant representing a unit vector along the positive Y-axis.
     */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    /**
     * A constant representing a unit vector along the positive Z-axis.
     */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructs a {@link Vector} from a {@link Double3} object.
     *
     * @param xyz A {@link Double3} object representing the vector's components.
     * @throws IllegalArgumentException if a zero vector is created.
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (_xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("A zero vector cannot be created.");
        }
    }

    /**
     * Constructs a {@link Vector} from three double values.
     *
     * @param x The x-component of the vector.
     * @param y The y-component of the vector.
     * @param z The z-component of the vector.
     * @throws IllegalArgumentException if a zero vector is created.
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (Util.isZero(x) && Util.isZero(y) && Util.isZero(z)) {
            throw new IllegalArgumentException("A zero vector cannot be created.");
        }
    }

    /**
     * Subtracts another vector from this vector.
     *
     * @param other The vector to subtract.
     * @return A new {@link Vector} representing the difference between the two vectors.
     */
    public Vector subtract(Vector other) {
        return new Vector(this._xyz.subtract(other._xyz));
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other The vector to add.
     * @return A new {@link Vector} representing the sum of the two vectors.
     */
    public Vector add(Vector other) {
        return new Vector(super.add(other)._xyz);
    }

    /**
     * Scales this vector by a scalar value.
     *
     * @param scalar The scalar value to multiply the vector by.
     * @return A new {@link Vector} representing the scaled vector.
     */
    public Vector scale(double scalar) {
        return new Vector(super._xyz.scale(scalar));
    }

    /**
     * Calculates the dot product of this vector with another vector.
     * <p>
     * The dot product is a scalar value equal to {@code |a| |b| cos(theta)}, where {@code theta}
     * is the angle between the vectors.
     *
     * @param other The other vector.
     * @return The dot product of the two vectors.
     */
    public double dotProduct(Vector other) {
        return (_xyz._d1() * other._xyz._d1() +
                _xyz._d2() * other._xyz._d2() +
                _xyz._d3() * other._xyz._d3());
    }

    /**
     * Calculates the cross product of this vector with another vector.
     * <p>
     * The cross product results in a new vector that is perpendicular to both original vectors.
     * Its magnitude is equal to {@code |a| |b| sin(theta)}.
     *6
     * @param other The other vector.
     * @return A new {@link Vector} that is the cross product of the two vectors.
     */
    public Vector crossProduct(Vector other) {
        return new Vector(
                _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2(),
                _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3(),
                _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1()
        );
    }

    /**
     * Calculates the squared length (magnitude) of the vector.
     * <p>
     * This is equivalent to the dot product of the vector with itself and is more efficient
     * to compute than the length.
     *
     * @return The squared length of the vector.
     */
    public double lengthSquared() {
        return this.dotProduct(this);
    }

    /**
     * Calculates the length (magnitude) of the vector.
     *
     * @return The length of the vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes the vector, creating a new unit vector with the same direction.
     *
     * @return A new {@link Vector} with a length of 1.
     */
    public Vector normalize() {
        return this.scale(1 / length());
    }

    /**
     * Calculates the component of this vector that is orthogonal to a given axis vector.
     *
     * @param axis The axis vector.
     * @return The orthogonal component of this vector relative to the axis.
     */
    public Vector orthogonalComponent(Vector axis) {
        double scaleFactor = this.dotProduct(axis) / axis.lengthSquared();
        Vector projection;

        // If the projection is a zero vector, the vectors are already orthogonal.
        try {
            projection = axis.scale(scaleFactor);
        } catch (IllegalArgumentException e) {
            return this;
        }

        return this.subtract(projection);
    }

    /**
     * Checks if this vector is parallel to another vector using Lagrange's identity.
     * <p>
     * Two vectors are parallel if their cross product is a zero vector. This method uses
     * the identity {@code |a x b|^2 = |a|^2 |b|^2 - (a · b)^2} to check for parallelism
     * without performing a cross product.
     *
     * @param other The other vector to compare against.
     * @return {@code true} if the vectors are parallel, otherwise {@code false}.
     */
    public boolean areParallel(Vector other) {
        double dot = this.dotProduct(other);
        double lengthsSquaredProduct = this.lengthSquared() * other.lengthSquared();
        return Util.alignZero(dot * dot - lengthsSquaredProduct) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return super.equals(obj);
    }
}
