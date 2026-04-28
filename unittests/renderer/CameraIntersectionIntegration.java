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
 * Integration tests for Camera ray construction and Geometries intersections.
 */
public class CameraIntersectionIntegration {

    /**
     * Helper method to count intersections of camera rays with an intersectable body.
     *
     * @param camera the camera
     * @param body   the intersectable object
     * @param nX     resolution X
     * @param nY     resolution Y
     * @return total number of intersections
     */
    private int countIntersections(Camera camera, Intersectable body, int nX, int nY) {
        int count = 0;
        for (int i = 0; i < nY; ++i) {
            for (int j = 0; j < nX; ++j) {
                List<Point> intersections = body.findIntersections(camera.constructRay(nX, nY));
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }
        return count;
    }

    @Test
    void testCameraRaySphereIntegration() {
        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .build();

        // TC01: Sphere r=1 (2 points)
        assertEquals(2, countIntersections(camera1, new Sphere(new Point(0, 0, -3), 1), 3, 3),
                "Bad amount of intersections");

        // TC02: Sphere r=2.5 (18 points)
        Camera camera2 = Camera.getBuilder()
                .setLocation(new Point(0, 0, 0.5))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .build();
        assertEquals(18, countIntersections(camera2, new Sphere(new Point(0, 0, -2.5), 2.5), 3, 3),
                "Bad amount of intersections");
    }

    @Test
    void testCameraRayPlaneIntegration() {
        Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .build();

        // TC01: Plane parallel to VP (9 points)
        assertEquals(9, countIntersections(camera, new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), 3, 3),
                "Bad amount of intersections");

        // TC02: Plane tilted (6 points)
        assertEquals(6, countIntersections(camera, new Plane(new Point(0, 0, -2), new Vector(0, 1, 1)), 3, 3),
                "Bad amount of intersections");
    }

    @Test
    void testCameraRayTriangleIntegration() {
        Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(1)
                .setVpSize(3, 3)
                .setResolution(3, 3)
                .build();

        // TC01: Small triangle (1 point)
        assertEquals(1, countIntersections(camera, new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 3, 3),
                "Bad amount of intersections");

        // TC02: Large triangle (2 points)
        assertEquals(2, countIntersections(camera, new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 3, 3),
                "Bad amount of intersections");
    }
}

