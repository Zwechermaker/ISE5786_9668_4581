package renderer;

import static java.awt.Color.*;

import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.impl.AmbientLight;
import lighting.impl.PointLight;
import primitives.*;
import scene.Scene;

/**
 * Tests for Super-Sampled Anti-Aliasing (SSAA).
 * Demonstrates the smoothing of jagged edges on curves and diagonal lines.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
class AntiAliasingTests {

    /** Default constructor to satisfy JavaDoc generator */
    AntiAliasingTests() { /* to satisfy JavaDoc generator */ }

    /** Scene for the tests */
    private final Scene _scene = new Scene("Anti-Aliasing Test Scene");

    /** Camera builder for the tests */
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setLocation(new Point(0, 0, 1000))
            .setDirection(Point.ZERO, Vector.AXIS_Y)
            .setVpDistance(1000)
            .setVpSize(200, 200)
            .setResolution(500, 500)
            .setMultithreading(-2) // Use available cores
            .setDebugPrint(1.0);

    /**
     * Helper method to populate the scene with high-contrast shapes.
     */
    private void buildScene() {
        // Dark background to make the jagged edges of the bright shapes obvious
        _scene.setBackground(new Color(20, 20, 20));
        _scene.setAmbientLight(new AmbientLight(new Color(20, 20, 20)));

        _scene.geometries.add(
                // 1. A Sphere to test curved edges
                new Sphere(new Point(0, 0, -100), 50D).setEmission(new Color(RED))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30)),

                // 2. A Triangle cutting diagonally across the screen to test slanted lines
                new Triangle(new Point(-100, -100, -150), new Point(100, 100, -150), new Point(-100, 100, -150))
                        .setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(30))
        );

        // A single point light to give the shapes 3D volume
        _scene.lights.add(
                new PointLight(new Color(400, 400, 400), new Point(50, 50, 50))
                        .setKl(0.00001).setKq(0.000001)
        );
    }

    /**
     * Renders the shapes with Anti-Aliasing DISABLED.
     * Zoom in on the edges of the saved image to see the harsh "stair-step" pixels.
     */
    @Test
    @SuppressWarnings("java:S109")
    void testAntiAliasingDisabled() {
        buildScene();

        _cameraBuilder
                .setRayTracer(_scene, RayTracerType.SIMPLE)
                .setAntiAliasing(1) // 1 Ray per pixel
                .build()
                .renderImage()
                .writeToImage("AntiAliasing_Disabled");
    }

    /**
     * Renders the shapes with Anti-Aliasing ENABLED.
     * The edges should now appear smooth and naturally blended into the background.
     */
    @Test
    @SuppressWarnings("java:S109")
    void testAntiAliasingEnabled() {
        buildScene();

        _cameraBuilder
                .setRayTracer(_scene, RayTracerType.SIMPLE)
                .setAntiAliasing(3) // 3x3 Grid = 9 Rays per pixel
                .build()
                .renderImage()
                .writeToImage("AntiAliasing_Enabled");
    }
}