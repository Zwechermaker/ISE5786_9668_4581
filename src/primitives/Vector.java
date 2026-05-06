package primitives;

/**
 * A vector class that describes a geometric vector in space.
 */
public final class Vector extends Point{
    /**
     * A constant that describes a unit vector in the x direction
     */
    public static final Vector AXIS_X = new Vector(1,0,0);
    /**
     * A constant that describes a unit vector in the y direction
     */
    public static final Vector AXIS_Y = new Vector(0,1,0);
    /**
     * A constant that describes a unit vector in the z direction
     */
    public static final Vector AXIS_Z = new Vector(0,0,1);
    /**
     * constructor that gets a Double3 and puts it in the vector
     * @param xyz a Double3 that determines the vector head
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
     * @param vector a vector to add to the current vector
     * @return the addition  result
     */
    public Vector add(Vector vector){
        return new Vector(super.add(vector)._xyz);
    }

    /**
     * a function that scales a vector
     * @param scalar scalar to multiply by t
     * @return the scaled vector
     */
    public Vector scale(double scalar){
            return new Vector(super._xyz.scale(scalar));

    }

    /**
     * a function the calculates the dot product
     * @param vector vector in the calculation
     * @return the dot product result
     */
    public double dotProduct(Vector vector){
        return(_xyz._d1()*vector._xyz._d1()+_xyz._d2()*vector._xyz._d2()
                +_xyz._d3()*vector._xyz._d3());
    }

    /**
     * calculates the cross product between two vectors
     * @param vector vector to calculate he cross product with
     * @return the vector that is perpendicular to both of them
     */
    public Vector crossProduct(Vector vector) {
            return new Vector(
                    _xyz._d2() * vector._xyz._d3() - _xyz._d3() * vector._xyz._d2(),
                    _xyz._d3() * vector._xyz._d1() - _xyz._d1() * vector._xyz._d3(),
                    _xyz._d1() * vector._xyz._d2() - _xyz._d2() * vector._xyz._d1()
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

    /**
     * calculates the orthogonal component of a vector relative to an axis
     * @param axis a vector to find the orthogonal component relative to.
     * @return the orthogonal component of the vector relative to the axis
     */
    public Vector orthogonalComponent(Vector axis){
        double scaleFactor = this.dotProduct(axis) / axis.lengthSquared();
        Vector projection = null;

        //if zero vector is created, vectors are already orthogonal.
        try{
            projection = axis.scale(scaleFactor);
        } catch (IllegalArgumentException e){
            return this;
        }

        return this.subtract(projection);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        return super.equals(obj);
    }

}
