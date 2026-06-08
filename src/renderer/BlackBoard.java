package renderer;

import primitives.Point;
import primitives.Vector;

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
    public List<Point>generatePoints(Sampler sampler){
        return null;
    }
}
