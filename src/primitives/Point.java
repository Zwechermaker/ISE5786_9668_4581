package primitives;

import java.util.Objects;

/**
 * A class representing a point in 3D Cartesian space.
 * <p>
 * The point is defined by its three coordinates (x, y, z), which are stored in a {@link Double3} object.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class Point {
    /**
     * The internal representation of the point's coordinates (x, y, z).
     */
    protected final Double3 _xyz;

    /**
     * A constant representing the origin point (0, 0, 0) in 3D space.
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a {@link Point} from three double values.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param z The z-coordinate of the point.
     */
    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a {@link Point} from a {@link Double3} object.
     *
     * @param xyz A {@link Double3} object representing the point's coordinates.
     */
    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    /**
     * Gets the x-coordinate of the point.
     * @return The x-coordinate.
     */
    public double getX() {
        return _xyz._d1();
    }

    /**
     * Gets the y-coordinate of the point.
     * @return The y-coordinate.
     */
    public double getY() {
        return _xyz._d2();
    }

    /**
     * Gets the z-coordinate of the point.
     * @return The z-coordinate.
     */
    public double getZ() {
        return _xyz._d3();
    }

    /**
     * Gets a coordinate by its index.
     * @param index The index of the coordinate (0 for X, 1 for Y, 2 for Z).
     * @return The coordinate value.
     * @throws IndexOutOfBoundsException if the index is not 0, 1, or 2.
     */
    public double getCoord(int index) {
        return switch (index) {
            case 0 -> getX();
            case 1 -> getY();
            case 2 -> getZ();
            default -> throw new IndexOutOfBoundsException("Index can be only 0, 1 or 2");
        };
    }

    /**
     * Calculates the vector from this point to another point.
     *
     * @param other The point to which the vector is directed.
     * @return A {@link Vector} representing the displacement from this point to the other point.
     */
    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }

    /**
     * Calculates a new point by adding a vector to this point.
     *
     * @param vector The vector to add to this point.
     * @return A new {@link Point} representing the translated point.
     */
    public Point add(Vector vector) {
        return new Point(_xyz.add(vector._xyz));
    }

    /**
     * Calculates the squared distance between this point and another point.
     * <p>
     * This method is often more efficient than {@link #distance(Point)} as it avoids
     * the computationally expensive square root operation. It is useful for distance comparisons.
     * The formula is: {@code (x2-x1)^2 + (y2-y1)^2 + (z2-z1)^2}.
     *
     * @param point The point to which the squared distance is calculated.
     * @return The squared distance between the two points.
     */
    public double distanceSquared(Point point) {
        // (x2-x1)^2 + (y2-y1)^2 + (z2-z1)^2
        return (_xyz._d1() - point._xyz._d1()) * (_xyz._d1() - point._xyz._d1())
                + (_xyz._d2() - point._xyz._d2()) * (_xyz._d2() - point._xyz._d2())
                + (_xyz._d3() - point._xyz._d3()) * (_xyz._d3() - point._xyz._d3());
    }

    /**
     * Calculates the Euclidean distance between this point and another point.
     * <p>
     * The formula is: {@code sqrt((x2-x1)^2 + (y2-y1)^2 + (z2-z1)^2)}.
     *
     * @param point The point to which the distance is calculated.
     * @return The distance between the two points.
     */
    public double distance(Point point) {
        return Math.sqrt(distanceSquared(point));
    }

    @Override
    public String toString() {
        return "xyz: " + _xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return Objects.equals(_xyz, point._xyz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_xyz);
    }
}
