package renderer;

import primitives.*;
import scene.Scene;

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
    /**
     *A function that casts a ray through each pixed
     * and colors the grid of pixels with the resulting colors.
     * @return the renewed camera with the rendered image.
     */
    public Camera renderImage(){
        for (int i = 0; i < _nX; i++){
            for (int j = 0; j < _nY; j++){
                castRay(i,j);
            }
        }
        return this;
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
            _camera._vTo = to;
            _vUpTemp = up;
            _target = null;
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
        Builder setRayTracer(Scene scene, RayTracerType type){
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
         * checks the resolution of the camera and sets all relevant values.
         */
        void checkResolution(){
            if (_camera._nX <= 0 || _camera._nY <= 0){
                throw new IllegalArgumentException("Resolution must be positive");
            }
            _camera._imageWriter = new ImageWriter(_camera._nX, _camera._nY);
        }

        /**
         * checks the location and direction of the camera and sets all relevant values.
         */
        void checkLocationAndDirection(){
            if (_camera._p0 == null){
                throw new MissingResourceException("Location must be set", "Camera", "_p0");
            }
            if (_camera._vTo == null && _target == null) {
                throw new MissingResourceException("Direction must be set", "Camera", "_vTo");
            }
            if (_camera._vTo == null){
                _camera._vTo = _target.subtract(_camera._p0);
            }
            _camera._vTo = _camera._vTo.normalize();
            if (_vUpTemp == null) {
                _vUpTemp = Vector.AXIS_Y;
            }
            _camera._vRight = _camera._vTo.crossProduct(_vUpTemp).normalize();

            // no need for normilization (as the cross product of 2 orthogonal normalized vectors(
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
