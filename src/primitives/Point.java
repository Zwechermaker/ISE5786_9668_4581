package primitives;

/**
 * A class that describes a point in space.
 */
public class Point {
    /**
     * A double3 that represents a point in space
     */
    protected final Double3 _xyz;

    /**
     * A constant value that represents the origin in space.
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * a constructor defined by 3 double values.
     *
     * @param x represents the x value to initialize
     * @param y represents the y value to initialize
     * @param z represents the z value to initialize
     */
    public Point(double x, double y , double z){
        _xyz = new Double3(x,y,z);
    }

    /**
     * A constructor that takes a Double 3 as an argument.
     *
     * @param xyz A Double3 that turns to a geometric point in space
     */
    public Point(Double3 xyz){
        _xyz = xyz;
    }


    /**
     * calculate the vector between 2 points.
     *
     * @param point the point we are supposed to find the vector to.
     * @return The vector between 2 points
     */
    public Vector subtract(Point point){
        return new Vector(_xyz.subtract(point._xyz));
    }

    /**
     * calculates the point moved by a vector.
     *
     * @param vector a vector to add to the current point
     * @return the point that was moved by a vector
     */
    public Point add(Vector vector){
        return new Point(_xyz.add(vector._xyz));
    }

    /**
     * calculates the squared distance between 2 points.
     *
     * @param point the point we are supposed to find the distance to.
     * @return the distance between 2 points squared.
     */
    public double distanceSquared(Point point) {
        // (x2-x1)^2 + (y2-y1)^2 + (z2-z1)^2

        return (_xyz._d1() - point._xyz._d1()) * (_xyz._d1() - point._xyz._d1())
                + (_xyz._d2() - point._xyz._d2()) * (_xyz._d2() - point._xyz._d2())
                + (_xyz._d3() - point._xyz._d3()) * (_xyz._d3() - point._xyz._d3());
    }

    /**
     * calculates the distance between 2 points.
     *
     * @param point the point we are supposed to find the distance to.
     * @return the distance between 2 points.
     */
    public double distance(Point point){
        // \sqrt{(x2-x1)^2+(y2-y1)^2+(z2-z1)^2}
        return Math.sqrt(distanceSquared(point));
    }


    @Override
    public String toString() {
        return _xyz.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Point point = (Point) obj;

        return _xyz.equals(point._xyz);
    }
}
