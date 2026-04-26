package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.Locale;

public class Camera implements  Cloneable {

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
        return null;
    }

    /**
     * a builder creator function for camera
     * @return a new builder for camera
     */
    public static Builder getBuilder() {
        return null;
    }

    /**
     * a builder class for camera
     */
    public static class Builder {
        /**
         * camera object to build
         */
        private final Camera _camera = new Camera();

        private Point _target = null;

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
            return null;
        }

        /**
         * setter for the direction of the camera were the up vector is (0,1,0)
         * @param target the point the camera points to
         * @return a Builder for camera with the set direction
         */
        public Builder setDirection(Point target) {
            _target = target;
            return this;
        }

        /**
         * setter for the direction of the camera
         * @param target the point the camera points to
         * @param up the vector the camera points up
         * @return a Builder for camera with the set direction
         */
        public Builder setDirection(Point target, Vector up) {
            return null;
        }

        /**
         * setter for the size of the view plane
         * @param width of the view plane
         * @param height of the view plane
         * @return a Builder for camera with the size of the view plane
         */
        public Builder setVpSize(int width, int height){
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
         * @param pixelWidth of a pixel
         * @param pixelHeight of a pixel
         * @return a Builder for camera with the resolution
         */
        public Builder setResolution(int pixelWidth, int pixelHeight) {
            _camera._pixelWidth = pixelWidth;
            _camera._pixelHeight = pixelHeight;
            return this;
        }

        void checkResolution(){

        }
        void checkLocationAndDirection(){

        }
        void checkViewPlane(){

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