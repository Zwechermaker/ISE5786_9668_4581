package renderer;

import primitives.*;
import scene.Scene;
import java.util.stream.*;
import java.util.LinkedList;
import java.util.MissingResourceException;

/**
 * a class that describes a camera and view plane for ray construction.
 */
public class Camera implements Cloneable {
    /**
     * default resolution for the camera
     */
    private static final int DEFAULT_PIXEL_NUM = 1;

    /**
     * The origin point of the camera.
     */
    private Point _p0 = null;

    /**
     * The forward vector (towards the scene).
     */
    private Vector _vTo = null;

    /**
     * The upward vector.
     */
    private Vector _vUp = null;

    /**
     * The rightward vector.
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
     * The distance from the camera to the view plane.
     */
    private double _distance;
    /**
     * center of the view plane.
     */
    private Point _vpCenter;
    /**
     * width of a pixel in the view plane.
     */
    private double _pixelWidth;
    /**
     * height of a pixel in the view plane.
     */
    private double _pixelHeight;

    /**
     * the amount of pixels on the x axis
     */
    private int _nX = DEFAULT_PIXEL_NUM;
    /**
     * the amount of pixels on the y axis
     */
    private int _nY = DEFAULT_PIXEL_NUM;
    /**
     * the image writer for the camera
     */
    private ImageWriter _imageWriter;
    /**
     * the ray tracer for the camera
     */
    private RayTracerBase _rayTracer;


    /**
     * default constructor for camera
     */
    private Camera() {

    }
    /** Amount of threads to use fore rendering image by the camera */
    private int threadsCount =5;
    /**
     * Amount of threads to spare for Java VM threads:<br>
     * Spare threads if trying to use all the cores
     */
    private static final int SPARE_THREADS = 2;
    /**
     * Debug print interval in seconds (for progress percentage)<br>
     * if it is zero - there is no progress output
     */
    private double printInterval = 0;
    /**
     * Pixel manager for supporting:
     * <ul>
     * <li>multi-threading</li>
     * <li>debug print of progress percentage in Console window/tab</li>
     * </ul>
     */
    private PixelManager pixelManager;

    /**
     * ray constructor through a pixel
     * @param column of the pixel
     * @param row of the pixel
     * @return the ray
     */
    public Ray constructRay(int column, int row){
        Point pIJ = _vpCenter;
        double xJ = (column - ((double) (_nX - 1) / 2)) * _pixelWidth;
        double yI = -(row - ((double) (_nY - 1) / 2)) * _pixelHeight;

        if (!Util.isZero(xJ)) pIJ = pIJ.add(_vRight.scale(xJ));
        if (!Util.isZero(yI)) pIJ = pIJ.add(_vUp.scale(yI));
        return new Ray(_p0, pIJ.subtract(_p0));
    }
     /** This function renders image's pixel color map from the scene
      * included in the ray tracer object
     * @return the camera object itself
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(_nY, _nX, printInterval);
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * A function that traces a ray through a pixel and colors
     * the pixel with the resulting color.
     * @param xIndex x coordinate in the view plane.
     * @param yIndex y coordinate in the view plane.
     */
    private void castRay(int xIndex, int yIndex){
        Ray ray = constructRay(xIndex,yIndex);
        Color color = _rayTracer.traceRay(ray);
        _imageWriter.writePixel(xIndex, yIndex, color);
        pixelManager.pixelDone();
    }
    /**
     * Render image using multi-threading by parallel streaming
     * @return the camera object itself
     */
    private Camera renderImageStream() {
        IntStream.range(0, _nY).parallel()
                .forEach(i -> IntStream.range(0, _nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }
    /**
     * Render image without multi-threading
     * @return the camera object itself
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < _nY; ++i)
            for (int j = 0; j < _nX; ++j)
                castRay(j, i);
        return this;
    }
    /**
     * Render image using multi-threading by creating and running raw threads
     * @return the camera object itself
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        while (threadsCount-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {}
        return this;
    }
    /**
     * A function that colors a specific image with a grid.
     * @param interval the size of each block in the grid
     * @param color the color of the grid
     * @return a camera object with the grid.
     */
    public Camera printGrid(int interval, Color color){
        for (int i = 0; i < _nX; i++) {
            for (int j = 0; j < _nY; j++) {
                if(i % interval == 0 || j % interval == 0){
                    _imageWriter.writePixel(i, j, color);
                }
            }
        }
        return this;
    }

    /**
     * Delegates the image writing to the imageWriter class.
     * @param fileName the name of the image file
     */
    public void writeToImage(String fileName){
        _imageWriter.writeToImage(fileName);
    }
    /**
     * a builder creator function for camera
     * @return a new builder for camera
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * a builder class for camera
     */
    public static class Builder {
        /** Default constructor to satisfy documentation tools. */
        Builder() { /* Default constructor to satisfy documentation tools */ }

        /**
         * camera object to build
         */
        private final Camera _camera = new Camera();

        /**
         * temporary variable for initialization of the direction
         */
        private Point _target = null;
        /**
         * temporary variable for holding the up direction for the camera
         */
        private Vector _vUpTemp = null;

        /**
         * setter for the origin point of the camera
         * @param p0 the point to initialize
         * @return a Builder for camera with the origin point
         */
        public Builder setLocation(Point p0) {
            _camera._p0 = p0;
            return this;
        }

        /**
         * setter for the direction of the camera
         * @param to vector pointing were the camera points to
         * @param up vector pointing were the camera points up
         * @return a Builder for camera with the set direction
         */
        public Builder setDirection(Vector to,Vector up) {
            //clear previous state.
            _camera._vUp = null;
            _camera._vRight = null;
            _target = null;

            _camera._vTo = to;
            _vUpTemp = up;
            return this;
        }

        /**
         * setter for the direction of the camera were the up vector is (0,1,0)
         * @param target the point the camera points to
         * @return a Builder for camera with the set direction
         */
        public Builder setDirection(Point target) {

            return setDirection(target, Vector.AXIS_Y);
        }

        /**
         * setter for the direction of the camera
         * (sets the temporary variable of target to allow for different orders of initialization)
         * @param target the point the camera points to
         * @param up the vector the camera points up
         * @return a Builder for camera with the set direction
         */
        public Builder setDirection(Point target, Vector up) {
            //clear previous state
            _camera._vTo = null;
            _camera._vUp = null;
            _camera._vRight = null;

            _target = target;
            _vUpTemp = up;

            return this;

        }

        /**
         * setter for the size of the view plane
         * @param width of the view plane
         * @param height of the view plane
         * @return a Builder for camera with the size of the view plane
         */
        public Builder setVpSize(double width, double height){
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * setter for the distance from the camera to the view plane
         * @param distance of the view plane
         * @return a Builder for camera with the distance from the camera to the view plane
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * setter for the resolution
         * @param nX amount of pixels on the x axis
         * @param nY amount of pixels on the y axis
         * @return a Builder for camera with the resolution
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * setter for the ray tracer.
         * @param scene the scene to trace rays in
         * @param type the type of the ray-tracer
         * @return a Builder for the camera with the ray tracer
         */
        public Builder setRayTracer(Scene scene, RayTracerType type){
            switch (type){
                case SIMPLE:
                    _camera._rayTracer = new SimpleRayTracer(scene);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ray tracer type");
            }

            return this;
        }
        /**
         * Set multi-threading <br>
         * Parameter value meaning:
         * <ul>
         * <li>-2 - number of threads is number of logical processors less 2</li>
         * <li>-1 - stream processing parallelization (implicit multi-threading) is used</li>
         * <li>0 - multi-threading is not activated</li>
         * <li>1 and more - literally number of threads</li>
         * </ul>
         * @param threads number of threads
         * @return builder object itself
         */
        public Builder setMultithreading(int threads) {
            if (threads < -3)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                _camera.threadsCount = cores <= 2 ? 1 : cores;
            } else
                _camera.threadsCount = threads;
            return this;
        }
        /**
         * Set debug printing interval. If it's zero - there won't be printing at all
         * @param interval printing interval in %
         * @return builder object itself
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("interval parameter must be non-negative");
            _camera.printInterval = interval;
            return this;
        }
        /**
         * checks the resolution of the camera and sets all relevant values.
         */
        void checkResolution(){
            if (_camera._nX <= 0 || _camera._nY <= 0){
                throw new IllegalArgumentException("Resolution must be positive");
            }
            _camera._imageWriter = new ImageWriter(_camera._nX, _camera._nY);
        }
        /**
         * Rotates the camera around its vertical axis (Yaw / Pan).
         * @param angleDegrees the angle to rotate clockwise in degrees
         * @return the Builder object
         */
        public Builder rotate(double angleDegrees) {
            checkLocationAndDirection();

            double angleRad = Math.toRadians(angleDegrees);

            double cosTheta = Util.alignZero(Math.cos(angleRad));
            double sinTheta = Util.alignZero(Math.sin(angleRad));

            Vector oldVTo = _camera._vTo;
            Vector oldVRight = _camera._vRight;
            Vector newVTo = null;

            // rotate _vTo around _vUp using the orthogonal basis vectors
            if (!Util.isZero(cosTheta) && !Util.isZero(sinTheta)) {
                newVTo = oldVTo.scale(cosTheta).add(oldVRight.scale(sinTheta));
            } else if (!Util.isZero(cosTheta)) {
                newVTo = oldVTo.scale(cosTheta);
            } else if (!Util.isZero(sinTheta)) {
                newVTo = oldVRight.scale(sinTheta);
            }

            if (newVTo != null) {
                _camera._vTo = newVTo.normalize();
            }

            // recalculate _vRight based on the updated forward direction
            _camera._vRight = _camera._vTo.crossProduct(_camera._vUp).normalize();

            return this;
        }
        /**
         * checks the location and direction of the camera and sets all relevant values.
         */
        void checkLocationAndDirection() {
            if (_camera._p0 == null) {
                throw new MissingResourceException("Location must be set", "Camera", "_p0");
            }
            if (_camera._vTo == null && _target == null) {
                throw new MissingResourceException("Direction must be set", "Camera", "_vTo");
            }

            if (_camera._vTo != null && _camera._vUp != null && _camera._vRight != null) {
                return;
            }

            if (_camera._vTo == null) {
                _camera._vTo = _target.subtract(_camera._p0);
            }
            _camera._vTo = _camera._vTo.normalize();

            if (_vUpTemp == null) {
                _vUpTemp = Vector.AXIS_Y;
            }
            _camera._vRight = _camera._vTo.crossProduct(_vUpTemp).normalize();

            //no need for normalization as the cross products of 2 normalized and orthogonal vectors.
            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo);
        }

        /**
         * checks the view plane and sets all relevant values.
         */
        void checkViewPlane(){
            if (Util.alignZero(_camera._width) <= 0 || Util.alignZero(_camera._height) <= 0){
                throw new IllegalArgumentException("View plane size must be positive");
            }
            if (Util.alignZero(_camera._distance) <= 0){
                throw new IllegalArgumentException("View plane distance must be positive");
            }
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));
        }
        /**
         * builds the camera
         * @return the camera
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if(_camera._rayTracer == null){
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }
            try {
                return (Camera)_camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }
    }
}
