package geometries.impl;

/**
 * A class that represents a cylinder in space.
 */
public class Cylinder extends Tube{
    /**
     * The height of the cylinder.
     */
    private final double height;

    /**
     * A parameter constructor for a cylinder
     * @param radius the radius of the cylinder
     * @param axis   the axis of the cylinder
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, primitives.Ray axis, double height) {
        super(radius, axis);
        if (primitives.Util.alignZero(height) <= 0) {
            throw new IllegalArgumentException("Cylinder height must be greater than 0");
        }
        this.height = height;
    }

    @Override
    public String toString() {
        return super.toString() + ", height: " + height + "\n";
    }
}
