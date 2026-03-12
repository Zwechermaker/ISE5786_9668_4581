package primitives;

public class Vector extends Point{
    /**
     * constructor that gets a Double3 and puts it in the vector
     * @param xyz a Double3  that dertermines the vector head
     */
    public Vector(Double3 xyz){
        if(xyz.equals(Double3.ZERO)){
            throw new IllegalArgumentException("Zero vector cannot be created");
        }
        super(xyz);
    }

    /**
     * constructor that gets 3 doubles and puts them in the vector
     * @param x represents the x value to initialize
     * @param y represents the y value to initialize
     * @param z represents the z value to initialize
     */
    public Vector(double x, double y, double z){
        if (Util.isZero(x) && Util.isZero(y) && Util.isZero(z))
        {
            throw new IllegalArgumentException("Zero vector cannot be created");
        }
        super(x,y,z);
    }




    /**
     * a function that adds two vectors
     * @param other a vector to add to the current vector
     * @return the addition  result
     */
    public Vector add(Vector other){}

    /**
     * a function that scales a vector
     * @param scalar scalar to multiply by t
     * @return the scaled vector
     */
    public Vector scale(double scalar){}

    /**
     * a function the calculates the dot product
     * @param other vector in the calculation
     * @return the dot product result
     */
    public double dotProduct(Vector other){}

    /**
     * a function that calculates the length of the vector squared
     * @return return the vectors length squared
     */
    public double lengthSquared(){}

    /**
     * calculates the length of the vector
     * @return the length of the vector
     */
    public double length(){
        return super.distance(Point.ZERO);
    }

    /**
     * normalizes a vector
     * @return the normalized vector
     */
    public Vector normalize(){}
}
