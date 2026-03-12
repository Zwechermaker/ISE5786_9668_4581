package primitives;

public class Ray {
    private final Point origin ;
    private final Vector direction;
    public Ray(Point point, Vector vector) {
        origin = point;
        direction = vector.normalize();
    }

}
