package renderer;

import primitives.*;
import scene.Scene;

import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

/**
 * A class representing a camera in a 3D scene, which defines the viewpoint and projection for rendering.
 * <p>
 * The camera is defined by its position, orientation (forward, up, and right vectors),
 * and a view plane through which the scene is projected. It is responsible for constructing
 * rays from the viewpoint through each pixel of the view plane.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class Camera implements Cloneable {
    /**
     * The default number of pixels for the resolution if not otherwise specified.
     */
    private static final int DEFAULT_PIXEL_NUM = 1;
    /**
     * The number of threads to spare when using all available cores for rendering.
     */
    private static final int SPARE_THREADS = 2;

    /**
     * The origin point of the camera.
     */
    private Point _p0 = null;
    /**
     * The forward vector, pointing towards the scene.
     */
    private Vector _vTo = null;
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
     * The distance from the camera to the view plane.
     */
    private double _distance;
    /**
     * The center point of the view plane.
     */
    private Point _vpCenter;
    /**
     * The width of a single pixel in the view plane.
     */
    private double _pixelWidth;
    /**
     * The height of a single pixel in the view plane.
     */
    private double _pixelHeight;

    /**
     * The number of pixels along the x-axis (width) of the image.
     */
    private int _nX = DEFAULT_PIXEL_NUM;
    /**
     * The number of pixels along the y-axis (height) of the image.
     */
    private int _nY = DEFAULT_PIXEL_NUM;
    /**
     * The image writer used to create the final image file.
     */
    private ImageWriter _imageWriter;
    /**
     * The ray tracer used to calculate the color of each pixel.
     */
    private RayTracerBase _rayTracer;

    /**
     * The number of threads to use for rendering.
     */
    private int threadsCount = 3;
    /**
     * The interval (in seconds) for printing debug progress messages.
     */
    private double printInterval = 0;
    /**
     * The pixel manager for handling multi-threaded rendering and progress reporting.
     */
    private PixelManager pixelManager;

    /**
     * Private constructor to be used by the {@link Builder}.
     */
    private Camera() {
    }

    /**
     * Constructs a ray from the camera's origin through a specific pixel on the view plane.
     * <p>
     * The ray is calculated by finding the center of the pixel on the view plane
     * and then creating a ray from the camera's origin through that point.
     * </p>
     *
     * @param column The column index of the pixel (0 to _nX-1).
     * @param row    The row index of the pixel (0 to _nY-1).
     * @return The constructed {@link Ray}.
     */
    public Ray constructRay(int column, int row) {
        // Calculate the center of the pixel on the view plane.
        Point pIJ = _vpCenter;
        double xJ = (column - ((double) (_nX - 1) / 2)) * _pixelWidth;
        double yI = -(row - ((double) (_nY - 1) / 2)) * _pixelHeight;

        // Adjust the pixel center based on the camera's right and up vectors.
        if (!Util.isZero(xJ)) pIJ = pIJ.add(_vRight.scale(xJ));
        if (!Util.isZero(yI)) pIJ = pIJ.add(_vUp.scale(yI));

        // Create a ray from the camera's origin through the adjusted pixel center.
        return new Ray(_p0, pIJ.subtract(_p0));
    }

    /**
     * Renders the image by casting rays through each pixel and writing the resulting colors.
     * This method supports multiple rendering strategies (single-threaded, multi-threaded, stream-based).
     *
     * @return This {@link Camera} object.
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
     * Traces a ray through a single pixel and writes the resulting color to the image writer.
     *
     * @param xIndex The x-coordinate (column) of the pixel.
     * @param yIndex The y-coordinate (row) of the pixel.
     */
    private void castRay(int xIndex, int yIndex) {
        Ray ray = constructRay(xIndex, yIndex);
        Color color = _rayTracer.traceRay(ray);
        _imageWriter.writePixel(xIndex, yIndex, color);
        pixelManager.pixelDone();
    }

    /**
     * Renders the image using parallel streams for multi-threading.
     *
     * @return This {@link Camera} object.
     */
    private Camera renderImageStream() {
        IntStream.range(0, _nY).parallel()
                .forEach(i -> IntStream.range(0, _nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }

    /**
     * Renders the image using a single thread.
     *
     * @return This {@link Camera} object.
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < _nY; ++i)
            for (int j = 0; j < _nX; ++j)
                castRay(j, i);
        return this;
    }

    /**
     * Renders the image using raw threads for multi-threading.
     *
     * @return This {@link Camera} object.
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
        } catch (InterruptedException ignored) {
        }
        return this;
    }

    /**
     * Overlays a grid on the rendered image.
     *
     * @param interval The size of each grid cell.
     * @param color    The color of the grid lines.
     * @return This {@link Camera} object.
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < _nX; i++) {
            for (int j = 0; j < _nY; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    _imageWriter.writePixel(i, j, color);
                }
            }
        }
        return this;
    }

    /**
     * Writes the rendered image to a file.
     *
     * @param fileName The name of the image file.
     */
    public void writeToImage(String fileName) {
        _imageWriter.writeToImage(fileName);
    }

    /**
     * Returns a new {@link Builder} for constructing a {@link Camera}.
     *
     * @return A new camera builder.
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * A builder class for constructing {@link Camera} objects.
     * This class follows the builder pattern to allow for flexible and readable camera configuration.
     */
    public static class Builder {
        /**
         * The camera object being built.
         */
        private final Camera _camera = new Camera();
        /**
         * A temporary variable for the camera's target point.
         */
        private Point _target = null;
        /**
         * A temporary variable for holding the up direction for the camera.
         */
        private Vector _vUpTemp = null;

        /**
         * Default constructor for the builder.
         */
        Builder() {
        }

        /**
         * Sets the position of the camera.
         *
         * @param p0 The origin point of the camera.
         * @return This {@link Builder} object for chaining.
         */
        public Builder setLocation(Point p0) {
            _camera._p0 = p0;
            return this;
        }

        /**
         * Sets the direction of the camera using forward and up vectors.
         * <p>
         * This method initializes the camera's orientation vectors.
         * </p>
         *
         * @param to The vector pointing forward (towards the scene).
         * @param up The vector pointing upwards.
         * @return This {@link Builder} object for chaining.
         */
        public Builder setDirection(Vector to, Vector up) {
            // Clear previous state.
            _camera._vUp = null;
            _camera._vRight = null;
            _target = null;

            _camera._vTo = to;
            _vUpTemp = up;
            return this;
        }

        /**
         * Sets the direction of the camera to point at a target, assuming a default up vector of (0, 1, 0).
         *
         * @param target The point the camera points to.
         * @return This {@link Builder} object for chaining.
         */
        public Builder setDirection(Point target) {
            return setDirection(target, Vector.AXIS_Y);
        }

        /**
         * Sets the direction of the camera to point at a target with a specified up vector.
         * <p>
         * This method sets temporary variables for the target and up vector, allowing for
         * different orders of initialization before the final camera build.
         * </p>
         *
         * @param target The point the camera points to.
         * @param up     The vector the camera points up.
         * @return This {@link Builder} object for chaining.
         */
        public Builder setDirection(Point target, Vector up) {
            // Clear previous state.
            _camera._vTo = null;
            _camera._vUp = null;
            _camera._vRight = null;

            _target = target;
            _vUpTemp = up;

            return this;
        }

        /**
         * Sets the size of the view plane.
         *
         * @param width  The width of the view plane.
         * @param height The height of the view plane.
         * @return This {@link Builder} object for chaining.
         */
        public Builder setVpSize(double width, double height) {
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance The distance of the view plane from the camera.
         * @return This {@link Builder} object for chaining.
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the output image.
         *
         * @param nX The number of pixels along the x-axis (width).
         * @param nY The number of pixels along the y-axis (height).
         * @return This {@link Builder} object for chaining.
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer for the camera.
         *
         * @param scene The scene to trace rays in.
         * @param type  The type of the ray tracer to use (e.g., SIMPLE, GRID).
         * @return This {@link Builder} object for chaining.
         * @throws IllegalArgumentException if an invalid ray tracer type is provided.
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            switch (type) {
                case SIMPLE:
                    _camera._rayTracer = new SimpleRayTracer(scene);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ray tracer type.");
            }
            return this;
        }

        /**
         * Sets the multi-threading configuration for rendering.
         * <p>
         * The parameter value has the following meanings:
         * <ul>
         *   <li>{@code -2}: The number of threads will be set to the number of logical processors minus {@link #SPARE_THREADS}.</li>
         *   <li>{@code -1}: Stream processing parallelization (implicit multi-threading) is used.</li>
         *   <li>{@code 0}: Multi-threading is not activated (single-threaded rendering).</li>
         *   <li>{@code 1} and greater: The literal number of threads specified will be used.</li>
         * </ul>
         *
         * @param threads The number of threads to use.
         * @return This {@link Builder} object for chaining.
         * @throws IllegalArgumentException if the threads parameter is less than -2.
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher.");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                _camera.threadsCount = cores <= 2 ? 1 : cores;
            } else
                _camera.threadsCount = threads;
            return this;
        }

        /**
         * Sets the debug printing interval for progress updates.
         * <p>
         * If the interval is zero, no progress output will be printed.
         * </p>
         *
         * @param interval The printing interval in seconds.
         * @return This {@link Builder} object for chaining.
         * @throws IllegalArgumentException if the interval parameter is negative.
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("Interval parameter must be non-negative.");
            _camera.printInterval = interval;
            return this;
        }

        /**
         * Validates the resolution settings and initializes the {@link ImageWriter}.
         *
         * @throws IllegalArgumentException if the horizontal or vertical resolution is not positive.
         */
        void checkResolution() {
            if (_camera._nX <= 0 || _camera._nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive.");
            }
            _camera._imageWriter = new ImageWriter(_camera._nX, _camera._nY);
        }

        /**
         * Rotates the camera around its vertical axis (Yaw / Pan).
         * <p>
         * This method applies a rotation to the camera's orientation vectors.
         * The rotation is performed around the camera's {@code _vUp} vector.
         * </p>
         *
         * @param angleDegrees The angle to rotate clockwise in degrees.
         * @return This {@link Builder} object for chaining.
         * @throws MissingResourceException if the camera's location or direction is not set before rotation.
         */
        public Builder rotate(double angleDegrees) {
            checkLocationAndDirection();

            double angleRad = Math.toRadians(angleDegrees);

            double cosTheta = Util.alignZero(Math.cos(angleRad));
            double sinTheta = Util.alignZero(Math.sin(angleRad));

            Vector oldVTo = _camera._vTo;
            Vector oldVRight = _camera._vRight;
            Vector newVTo = null;

            // Rotate _vTo around _vUp using the orthogonal basis vectors.
            // The rotation matrix for a 2D vector (x, y) rotated by theta is:
            // x' = x * cos(theta) - y * sin(theta)
            // y' = x * sin(theta) + y * cos(theta)
            // Here, _vTo acts as 'x' and _vRight acts as 'y' in the plane orthogonal to _vUp.
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

            // Recalculate _vRight based on the updated forward direction to maintain orthogonality.
            _camera._vRight = _camera._vTo.crossProduct(_camera._vUp).normalize();

            return this;
        }

        /**
         * Validates the camera's location and direction vectors and completes their initialization.
         * <p>
         * This method ensures that the camera's orthonormal basis vectors ({@code _vTo}, {@code _vUp}, {@code _vRight})
         * are correctly set up. If only a target point is provided, {@code _vTo} is derived from it.
         * If {@code _vUpTemp} is not explicitly set, it defaults to {@link Vector#AXIS_Y}.
         * </p>
         *
         * @throws MissingResourceException if the camera's location or direction is not set.
         */
        void checkLocationAndDirection() {
            if (_camera._p0 == null) {
                throw new MissingResourceException("Location must be set.", "Camera", "_p0");
            }
            if (_camera._vTo == null && _target == null) {
                throw new MissingResourceException("Direction must be set.", "Camera", "_vTo");
            }

            // If vectors are already initialized, no need to recalculate.
            if (_camera._vTo != null && _camera._vUp != null && _camera._vRight != null) {
                return;
            }

            // If _vTo was not directly set, derive it from the target point.
            if (_camera._vTo == null) {
                _camera._vTo = _target.subtract(_camera._p0);
            }
            _camera._vTo = _camera._vTo.normalize();

            // Default _vUpTemp to AXIS_Y if not set.
            if (_vUpTemp == null) {
                _vUpTemp = Vector.AXIS_Y;
            }

            // Calculate _vRight as the cross product of _vTo and _vUpTemp, then normalize.
            _camera._vRight = _camera._vTo.crossProduct(_vUpTemp).normalize();

            // Calculate _vUp to ensure it's orthogonal to both _vTo and _vRight.
            // No need for normalization here as the cross product of two normalized and orthogonal vectors is already normalized.
            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo);
        }

        /**
         * Validates the view plane parameters and calculates pixel dimensions.
         *
         * @throws IllegalArgumentException if the view plane width, height, or distance is not positive.
         */
        void checkViewPlane() {
            if (Util.alignZero(_camera._width) <= 0 || Util.alignZero(_camera._height) <= 0) {
                throw new IllegalArgumentException("View plane size must be positive.");
            }
            if (Util.alignZero(_camera._distance) <= 0) {
                throw new IllegalArgumentException("View plane distance must be positive.");
            }
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));
        }

        /**
         * Builds and returns the configured {@link Camera} object.
         *
         * @return The final {@link Camera} object.
         * @throws MissingResourceException   if essential camera parameters are not set.
         * @throws IllegalArgumentException   if view plane or resolution parameters are invalid.
         * @throws CloneNotSupportedException if the camera object cannot be cloned (should not happen).
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if (_camera._rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException _) {
                return null; // Should not happen as Camera is Cloneable.
            }
        }
    }
}
