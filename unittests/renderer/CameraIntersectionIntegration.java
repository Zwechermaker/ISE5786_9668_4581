package renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import primitives.Point;
import primitives.Vector;

/**
 * Integration test class for the camera and ray-geometric bodies intersections.
 */
class CameraIntersectionIntegration{

    /** Default constructor to satisfy documentation tools. */
    CameraIntersectionIntegration() { /* Default constructor to satisfy documentation tools */ }

    /** Forward direction used in cameras. */
    private static final Vector V_TO = new Vector(0, 0, -1);

    /** Up direction used in cameras. */
    private static final Vector V_UP = new Vector(0, 1, 0);

    /** First camera location used in tests. */
    private static final Point LOCATION_1 = Point.ZERO;

    /** Second camera location used in tests. */
    private static final Point LOCATION_2 = new Point(0, 0, 0.5);

    /** Default view-plane distance used in tests. */
    private static final double VP_DISTANCE = 1d;

    /** Default view-plane size used in tests. */
    private static final double VP_SIZE = 3d;

    /** Default resolution for both X and Y axis. */
    private static final int RESOLUTION = 3;

    /** Common point used for Plane generation in tests. */
    private static final Point PLANE_POINT = new Point(0, 0, -2);

    /** Common second vertex for Triangles in tests. */
    private static final Point TRIANGLE_P2 = new Point(1, -1, -2);

    /** Common third vertex for Triangles in tests. */
    private static final Point TRIANGLE_P3 = new Point(-1, -1, -2);

    /** Error message for incorrect amount of intersections. */
    private static final String ERROR_INTERSECTIONS = "Bad amount of intersections";

    /**
     * camera used across different tests cases
     */
    private final Camera camera1 = Camera.getBuilder()
            .setLocation(LOCATION_1)
            .setDirection(V_TO, V_UP)
            .setVpDistance(VP_DISTANCE)
            .setVpSize(VP_SIZE, VP_SIZE)
            .setResolution(RESOLUTION, RESOLUTION)
            .build();
    /**
     * camera used across different tests cases
     */
    private final Camera camera2 = Camera.getBuilder()
            .setLocation(LOCATION_2)
            .setDirection(V_TO, V_UP)
            .setVpDistance(VP_DISTANCE)
            .setVpSize(VP_SIZE, VP_SIZE)
            .setResolution(RESOLUTION, RESOLUTION)
            .build();

    /**
     * helper assertion method
     * @param camera   the camera generating the rays
     * @param body     the intersectable geometry
     * @param expected the expected number of total intersections
     * @param errorMsg the error message to output upon failure
     */
    private void assertIntersectionsCount(Camera camera, Intersectable body, int expected, String errorMsg) {
        int count = 0;

        // Iterate over all pixels according to the resolution
        for (int i = 0; i < RESOLUTION; ++i) {
            for (int j = 0; j < RESOLUTION; ++j) {
                // Generates a ray and calculates intersections
                List<Point> intersections = body.findIntersections(camera.constructRay(i,j));
                if (intersections != null) {
                    count += intersections.size(); // Sums the intersection quantities
                }
            }
        }

        // assert equality using the expected amount and the amount of intersections
        assertEquals(expected, count, errorMsg);
    }
    /**
     * Integration test method for sphere-ray intersections and camera ray-construction
     */
    @Test
    void testCameraRaySphereIntegration() {
        // TC01: Sphere r=1 (2 points)
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -3), 1), 2, ERROR_INTERSECTIONS);

        // TC02: Sphere r=2.5 (18 points)
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2.5), 2.5), 18, ERROR_INTERSECTIONS);

        // TC03: Sphere r=2 (10 points)
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2), 2), 10, ERROR_INTERSECTIONS);

        // TC04: Sphere r=4 (9 points) - Camera is completely inside the sphere
        assertIntersectionsCount(camera1, new Sphere(LOCATION_1, 4), 9, ERROR_INTERSECTIONS);

        // TC05: Sphere r=0.5 (0 points) - Sphere is located behind the camera
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, 1), 0.5), 0, ERROR_INTERSECTIONS);
    }

    /**
     * Integration test method for plane-ray intersections and camera ray-construction
     */
    @Test
    void testCameraRayPlaneIntegration() {
        // TC06: Plane is orthogonal to the camera (9 points)
        assertIntersectionsCount(camera1, new Plane(PLANE_POINT, new Vector(0, 0, 1)), 9, ERROR_INTERSECTIONS);

        // TC07: Plane is tilted slightly, intersecting all rays (9 points)
        assertIntersectionsCount(camera1, new Plane(PLANE_POINT, new Vector(0, 1, 2)), 9, ERROR_INTERSECTIONS);

        // TC08: Plane is tilted sharply, missing the bottom row of pixels (6 points)
        assertIntersectionsCount(camera1, new Plane(PLANE_POINT, new Vector(0, 1, 1)), 6, ERROR_INTERSECTIONS);
    }

    /**
     * Integration test method for triangle-ray intersections and camera ray-construction
     */
    @Test
    void testCameraRayTriangleIntegration() {
        // TC09: Small triangle, only intersecting the center pixel (1 point)
        assertIntersectionsCount(camera1, new Triangle(new Point(0, 1, -2), TRIANGLE_P2, TRIANGLE_P3), 1, ERROR_INTERSECTIONS);

        // TC10: Tall triangle, intersecting the center and top-center pixels (2 points)
        assertIntersectionsCount(camera1, new Triangle(new Point(0, 10, -2), TRIANGLE_P2, TRIANGLE_P3), 2, ERROR_INTERSECTIONS);
    }
}