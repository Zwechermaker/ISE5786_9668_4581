package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.MissingResourceException;


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
         * temporary variable for holding the to direction for the camera
         */
        private Vector _vToTemp = null;



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
            _vToTemp = to;
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

            return setDirection(target, Vector.AXIS_y);
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
            _vToTemp = null;
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
         * checks the resolution of the camera and sets all relevant values.
         */
        void checkResolution(){
            if (_camera._nX <= 0 || _camera._nY <= 0){
                throw new IllegalArgumentException("Resolution must be positive");
            }
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
                _vUpTemp = Vector.AXIS_y;
            }
            _camera._vUp = _vUpTemp.orthogonalComponent(_camera._vTo).normalize();

            _camera._vRight = _camera._vTo.crossProduct(_camera._vUp);
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

            try {
                return (Camera)_camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }
    }
}