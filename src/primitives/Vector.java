package primitives;

public class Vector extends Point{
    public Vector(Double3 xyz){
        if(xyz.equals(Double3.ZERO)){
            throw new IllegalArgumentException("Zero vector cannot be created");
        }
        super(xyz);
    }

    public Vector(double x, double y, double z){
        Double3 temp = new Double3(x,y,z);
        Vector(temp);
    }



    public Vector length(){
        return distance(Point.ZERO);
    }

}
