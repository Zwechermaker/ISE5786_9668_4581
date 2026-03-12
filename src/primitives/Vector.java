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
    public Vector add(Vector other){
        return new Vector(super.add(other).get_xyz());
    }

    /**
     * a function that scales a vector
     * @param scalar scalar to multiply by t
     * @return the scaled vector
     */
    public Vector scale(double scalar){
        return new Vector(super.get_xyz().scale(scalar));
    }

    /**
     * a function the calculates the dot product
     * @param other vector in the calculation
     * @return the dot product result
     */
    public double dotProduct(Vector other){
        return(get_xyz()._d1()*other.get_xyz()._d1()+get_xyz()._d2()*other.get_xyz()._d2()
                +get_xyz()._d3()*other.get_xyz()._d3());
    }

    /**
     * calculates the cross product between two vectors
     * @param other vector to calculate he cross product with
     * @return the vector that is perpendicular to both of them
     */
    public Vector crossProduct(Vector other) {
        return new Vector(
                get_xyz()._d2() * other.get_xyz()._d3() - get_xyz()._d3() * other.get_xyz()._d2(),
                get_xyz()._d3() * other.get_xyz()._d1() - get_xyz()._d1() * other.get_xyz()._d3(),
                get_xyz()._d1() * other.get_xyz()._d2() - get_xyz()._d2() * other.get_xyz()._d1()
        );
    }
    /**
     * a function that calculates the length of the vector squared
     * @return return the vectors length squared
     */
    public double lengthSquared(){
        return this.dotProduct(this);
    }

    /**
     * calculates the length of the vector
     * @return the length of the vector
     */
    public double length(){
        return Math.sqrt(lengthSquared());
    }

    /**
     * normalizes a vector
     * @return the normalized vector
     */
    public Vector normalize(){
        return this.scale(1/length());
    }


}
