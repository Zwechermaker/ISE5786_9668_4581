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

    // ================= Fields =================

    /**
     * The origin point of the camera (the "eye").
     */
    private Point _p0 = null;
    /**
     * The number of pixels along the x-axis (width) of the image.
     */
    private int _nX = DEFAULT_PIXEL_NUM;
    /**
     * The number of pixels along the y-axis (height) of the image.
     */
    private int _nY = DEFAULT_PIXEL_NUM;

    /**
     * The 3D target area representing the physical view plane.
     * <i>(Replaces width, height, distance, and center variables)</i>
     */
    private BlackBoard _viewPlane;
    /**
     * The sampling strategy used to distribute pixels across the view plane.
     */
    private Sampler _viewPlaneSampler;

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
     * Private constructor to enforce instantiation strictly via the {@link Builder}.
     */
    private Camera() {
    }

    // ================= Ray Construction =================

    /**
     * Constructs a ray from the camera's origin through a specific pixel on the view plane.
     * <p>
     * <b>Process:</b>
     * <ol>
     * <li>Requests a normalized 2D offset from the {@link Sampler}.</li>
     * <li>Maps the 2D offset to a physical 3D coordinate on the {@link BlackBoard}.</li>
     * <li>Draws a vector from the camera origin to the mapped coordinate.</li>
     * </ol>
     * </p>
     *
     * @param column The column index of the pixel (0 to _nX - 1).
     * @param row    The row index of the pixel (0 to _nY - 1).
     * @return The constructed {@link Ray}.
     */
    public Ray constructRay(int column, int row) {
        Point2D offset = _viewPlaneSampler.getOffset(column, row);
        Point target = _viewPlane.mapToBoard(offset);
        return new Ray(_p0, target.subtract(_p0));
    }

    // ================= Rendering Pipeline =================

    /**
     * Renders the image by casting rays through each pixel and writing the resulting colors.
     * <p>
     * This method automatically selects the optimal rendering strategy based on the
     * <code>threadsCount</code> configuration.
     * </p>
     *
     * @return This {@link Camera} object for method chaining.
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
     * Renders the image utilizing implicit multi-threading via parallel streams.
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
     * Renders the image sequentially using a single main thread.
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
     * Renders the image utilizing explicit, raw multi-threading managed by the {@link PixelManager}.
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

    // ================= Post-Processing & Output =================

    /**
     * Overlays a distinct grid over the rendered image to assist with alignment and debugging.
     *
     * @param interval The size of each grid cell in pixels.
     * @param color    The designated {@link Color} of the grid lines.
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
     * Commits the buffered image data to a physical file on the disk.
     *
     * @param fileName The desired name of the output file.
     */
    public void writeToImage(String fileName) {
        _imageWriter.writeToImage(fileName);
    }

    // ================= Builder Factory =================

    /**
     * Factory method to instantiate a new {@link Builder}.
     *
     * @return A fresh camera builder instance.
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * <b>Builder</b>
     * <p>
     * A flexible configuration builder for the {@link Camera}.
     * It manages complex temporary state (vectors, distances, aspect ratios) and performs
     * the necessary mathematical projections to securely construct the final Camera object.
     * </p>
     */
    public static class Builder {
        // --- Temporary Geometric State ---
        private Point _p0 = null;
        private Vector _vTo = null;
        private Vector _vUp = null;
        private Vector _vRight = null;
        private Vector _vUpTemp = null;
        private Point _target = null;

        private double _width;
        private double _height;
        private double _distance;

        // --- Output State ---
        private int _nX = DEFAULT_PIXEL_NUM;
        private int _nY = DEFAULT_PIXEL_NUM;
        private ImageWriter _imageWriter;
        private RayTracerBase _rayTracer;

        // --- Engine Configuration ---
        private int _threadsCount = 3;
        private double _printInterval = 0;

        /**
         * Default constructor.
         */
        Builder() {
        }

        /**
         * Sets the spatial origin position of the camera.
         *
         * @param p0 The absolute 3D coordinate point.
         * @return This {@link Builder} object.
         */
        public Builder setLocation(Point p0) {
            this._p0 = p0;
            return this;
        }

        /**
         * Orientates the camera explicitly using forward and upward vectors.
         *
         * @param to The vector pointing directly at the scene.
         * @param up The vector dictating the vertical orientation.
         * @return This {@link Builder} object.
         */
        public Builder setDirection(Vector to, Vector up) {
            this._vUp = null;
            this._vRight = null;
            this._target = null;

            this._vTo = to;
            this._vUpTemp = up;
            return this;
        }

        /**
         * Orientates the camera to look at a specific point, defaulting "up" to the Y-axis.
         *
         * @param target The point of interest.
         * @return This {@link Builder} object.
         */
        public Builder setDirection(Point target) {
            return setDirection(target, Vector.AXIS_Y);
        }

        /**
         * Orientates the camera to look at a specific point with a custom vertical axis.
         *
         * @param target The point of interest.
         * @param up     The upward orientation vector.
         * @return This {@link Builder} object.
         */
        public Builder setDirection(Point target, Vector up) {
            this._vTo = null;
            this._vUp = null;
            this._vRight = null;

            this._target = target;
            this._vUpTemp = up;
            return this;
        }

        /**
         * Defines the physical dimensions of the view plane.
         *
         * @param width  The width of the projection plane.
         * @param height The height of the projection plane.
         * @return This {@link Builder} object.
         */
        public Builder setVpSize(double width, double height) {
            this._width = width;
            this._height = height;
            return this;
        }

        /**
         * Sets the focal length distance to the view plane.
         *
         * @param distance The distance from the camera origin.
         * @return This {@link Builder} object.
         */
        public Builder setVpDistance(double distance) {
            this._distance = distance;
            return this;
        }

        /**
         * Configures the pixel resolution of the final image.
         *
         * @param nX Number of horizontal pixels.
         * @param nY Number of vertical pixels.
         * @return This {@link Builder} object.
         */
        public Builder setResolution(int nX, int nY) {
            this._nX = nX;
            this._nY = nY;
            return this;
        }

        /**
         * Injects the designated RayTracer implementation.
         *
         * @param scene The populated scene environment.
         * @param type  The requested algorithm type (e.g., SIMPLE).
         * @return This {@link Builder} object.
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            switch (type) {
                case SIMPLE:
                    this._rayTracer = new SimpleRayTracer(scene);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ray tracer type.");
            }
            return this;
        }

        /**
         * Configures the multi-threading profile for the render engine.
         * <ul>
         * <li><b>-2</b>: Utilizes all logical cores minus {@link #SPARE_THREADS}.</li>
         * <li><b>-1</b>: Implements parallel streams.</li>
         * <li><b>0</b>: Disables multi-threading.</li>
         * <li><b>>0</b>: Explicitly sets the thread pool size.</li>
         * </ul>
         *
         * @param threads Thread count configuration.
         * @return This {@link Builder} object.
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher.");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                this._threadsCount = cores <= 2 ? 1 : cores;
            } else {
                this._threadsCount = threads;
            }
            return this;
        }

        /**
         * Sets the terminal output interval for the {@link PixelManager}.
         *
         * @param interval Time in seconds between updates.
         * @return This {@link Builder} object.
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("Interval parameter must be non-negative.");
            this._printInterval = interval;
            return this;
        }

        /**
         * Applies a Yaw (Pan) rotation around the camera's local vertical axis.
         *
         * @param angleDegrees Angle of rotation in degrees.
         * @return This {@link Builder} object.
         */
        public Builder rotate(double angleDegrees) {
            checkLocationAndDirection();

            double angleRad = Math.toRadians(angleDegrees);
            double cosTheta = Util.alignZero(Math.cos(angleRad));
            double sinTheta = Util.alignZero(Math.sin(angleRad));

            Vector oldVTo = this._vTo;
            Vector oldVRight = this._vRight;
            Vector newVTo = null;

            if (!Util.isZero(cosTheta) && !Util.isZero(sinTheta)) {
                newVTo = oldVTo.scale(cosTheta).add(oldVRight.scale(sinTheta));
            } else if (!Util.isZero(cosTheta)) {
                newVTo = oldVTo.scale(cosTheta);
            } else if (!Util.isZero(sinTheta)) {
                newVTo = oldVRight.scale(sinTheta);
            }

            if (newVTo != null) {
                this._vTo = newVTo.normalize();
            }

            this._vRight = this._vTo.crossProduct(this._vUp).normalize();
            return this;
        }

        /**
         * Safely initializes the image writer.
         */
        private void checkResolution() {
            if (this._nX <= 0 || this._nY <= 0) {
                throw new IllegalArgumentException("Resolution must be positive.");
            }
            this._imageWriter = new ImageWriter(this._nX, this._nY);
        }

        /**
         * Mathematically establishes the orthonormal basis vectors.
         */
        private void checkLocationAndDirection() {
            if (this._p0 == null) {
                throw new MissingResourceException("Location must be set.", "Camera", "_p0");
            }
            if (this._vTo == null && this._target == null) {
                throw new MissingResourceException("Direction must be set.", "Camera", "_vTo");
            }

            if (this._vTo != null && this._vUp != null && this._vRight != null) return;

            if (this._vTo == null) {
                this._vTo = this._target.subtract(this._p0);
            }
            this._vTo = this._vTo.normalize();

            if (this._vUpTemp == null) {
                this._vUpTemp = Vector.AXIS_Y;
            }

            this._vRight = this._vTo.crossProduct(this._vUpTemp).normalize();
            this._vUp = this._vRight.crossProduct(this._vTo);
        }

        /**
         * Validates constraints on the projection plane dimensions.
         */
        private void checkViewPlane() {
            if (Util.alignZero(this._width) <= 0 || Util.alignZero(this._height) <= 0) {
                throw new IllegalArgumentException("View plane size must be positive.");
            }
            if (Util.alignZero(this._distance) <= 0) {
                throw new IllegalArgumentException("View plane distance must be positive.");
            }
        }

        /**
         * Assembles the parameters, maps the 3D plane geometry, and constructs the final Camera.
         *
         * @return A fully initialized, immutable {@link Camera} instance.
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if (this._rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }

            // 1. Calculate the exact center of the physical view plane
            Point center = this._p0.add(this._vTo.scale(this._distance));

            // 2. Initialize the lightweight Camera
            Camera camera = new Camera();
            camera._p0 = this._p0;
            camera._nX = this._nX;
            camera._nY = this._nY;

            // 3. Construct and inject the core architecture dependencies
            camera._viewPlane = new BlackBoard(this._vUp, this._vRight, this._width, this._height, center);
            camera._viewPlaneSampler = new RegularGrid(this._nX, this._nY);

            // 4. Inject runtime dependencies
            camera._imageWriter = this._imageWriter;
            camera._rayTracer = this._rayTracer;
            camera.threadsCount = this._threadsCount;
            camera.printInterval = this._printInterval;

            return camera;
        }
    }
}
