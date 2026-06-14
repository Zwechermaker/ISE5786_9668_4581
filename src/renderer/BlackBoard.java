package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import primitives.Util;
import primitives.Point2D;

import java.util.ArrayList;
import java.util.List;

public class BlackBoard {
    /**
     * The upward vector, defining the camera's orientation.
     */
    private Vector _vUp = null;
    /**
     * The rightward vector, defining the camera's orientation.
     */
    private Vector _vRight = null;
    /**
     * The width of the view plane.
     */
    private double _width;
    /**
     * The height of the view plane.
     */
    private double _height;
    /**
     * The center point of the view plane.
     */
    private Point _center;

    public BlackBoard(Vector vUp, Vector vRight, double width, double height, Point center){
        _vUp = vUp;
        _vRight = vRight;
        _width = width;
        _height = height;
        _center = center;
    }

    public Vector getVUp() {
        return _vUp;
    }

    public Vector getVRight() {
        return _vRight;
    }

    public double getWidth() {
        return _width;
    }

    public double getHeight() {
        return _height;
    }

    public Point getCenter() {
        return _center;
    }

    public Point mapToBoard(Point2D offset){
        Point target = _center;

        if (!Util.isZero(offset.x())) target = target.add(_vRight.scale(offset.x() * _width));
        if (!Util.isZero(offset.y())) target = target.add(_vUp.scale(offset.y() * _height));

        return target;
    }

    public List<Ray> generateBeam(Point origin, Sampler sampler) {
        List<Ray> beam = new ArrayList<>();
        for (Point2D offset : sampler.generateAll()) {
            beam.add(new Ray(origin, mapToBoard(offset).subtract(origin)));
        }
        return beam;
    }
}
