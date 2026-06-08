package primitives;

import java.util.Objects;

/**
 * An immutable record representing a 2D point with double-precision x and y coordinates.
 * <p>
 * This class is a simple data structure for holding two-dimensional coordinates.
 * It leverages Java's record feature for conciseness and automatic generation of
 * constructor, accessors, {@code equals()}, {@code hashCode()}, and {@code toString()}.
 * </p>
 *
 * @param x The x-coordinate of the 2D point.
 * @param y The y-coordinate of the 2D point.
 * @author Elad Zwecher and Benjamin Godfrey
 */
public record Point2D(double x, double y) {
    /**
     * A constant representing the origin point (0, 0) in 2D space.
     */
    public static final Point2D ZERO = new Point2D(0, 0);

    /**
     * Returns a string representation of this 2D point.
     *
     * @return A string in the format "(x, y)".
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Compares this 2D point to the specified object. The result is {@code true} if and only if
     * the argument is not {@code null} and is a {@code Point2D} object that represents the same
     * sequence of x and y coordinates. Floating-point comparisons are done using {@link Util#isZero(double)}.
     *
     * @param obj The object to compare this {@code Point2D} against.
     * @return {@code true} if the given object represents a {@code Point2D} equivalent to this point, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point2D other = (Point2D) obj;
        return Util.isZero(x - other.x) && Util.isZero(y - other.y);
    }

    /**
     * Returns a hash code for this 2D point.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
