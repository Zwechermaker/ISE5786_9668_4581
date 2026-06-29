package renderer;

import static java.awt.Color.*;

import org.junit.jupiter.api.Test;

import geometries.impl.Polygon;
import geometries.impl.Sphere;
import lighting.impl.AmbientLight;
import lighting.impl.SpotLight;
import primitives.*;
import scene.Scene;

/**
 * Tests for Diffuse (Blurry) Glass functionality using super-sampling.
 * Demonstrates 4 glass panes with increasing mattness values revealing
 * objects behind them.
 * * @author Elad Zwecher and Benjamin Godfrey
 */
class DiffuseGlassTests {
    /** Default constructor to satisfy JavaDoc generator */
    DiffuseGlassTests() { /* to satisfy JavaDoc generator */ }

    /** Scene for the tests */
    private final Scene _scene = new Scene("Diffuse Glass Test Scene");

    /** Camera builder for the tests */
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setLocation(new Point(0, 0, 1000))
            .setDirection(Point.ZERO, Vector.AXIS_Y)
            .setVpDistance(1000)
            .setVpSize(1000, 500)
            .setResolution(1000, 500)
            .setMultithreading(-2)
            .setDebugPrint(1.0);

    /**
     * Helper method to populate the scene with the 4 balls and 4 glasses.
     */
    private void buildScene() {
        // --- The Background / Floor ---
        _scene.geometries.add(
                new Polygon(new Point(-1000, -60, 1000), new Point(1000, -60, 1000),
                        new Point(1000, -60, -1000), new Point(-1000, -60, -1000))
                        .setEmission(new Color(40, 40, 40))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60))
        );

        // --- The 4 Balls (Behind the glass) ---
        _scene.geometries.add(
                new Sphere(new Point(-300, 0, -150), 50D).setEmission(new Color(RED))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100)),
                new Sphere(new Point(-100, 0, -150), 50D).setEmission(new Color(GREEN))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100)),
                new Sphere(new Point(100, 0, -150), 50D).setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100)),
                new Sphere(new Point(300, 0, -150), 50D).setEmission(new Color(YELLOW))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))
        );

        // --- The 4 Glass Panes (Increasing Diffuse Properties) ---
        double kd = 0.1, ks = 0.2, kt = 0.8;
        int shininess = 40;
        Color glassColor = new Color(0, 0, 10);

        _scene.geometries.add(
                // 1. Perfectly Clear Glass (mattness = 0.0)
                new Polygon(new Point(-360, -50, 0), new Point(-240, -50, 0),
                        new Point(-240, 150, 0), new Point(-360, 150, 0))
                        .setEmission(glassColor)
                        .setMaterial(new Material().setKD(kd).setKS(ks).setShininess(shininess).setKT(kt)
                                .setMattness(0.0)),

                // 2. Slightly Blurry Glass (mattness = 0.02)
                new Polygon(new Point(-160, -50, 0), new Point(-40, -50, 0),
                        new Point(-40, 150, 0), new Point(-160, 150, 0))
                        .setEmission(glassColor)
                        .setMaterial(new Material().setKD(kd).setKS(ks).setShininess(shininess).setKT(kt)
                                .setMattness(0.02)),

                // 3. Medium Blurry Glass (mattness = 0.05)
                new Polygon(new Point(40, -50, 0), new Point(160, -50, 0),
                        new Point(160, 150, 0), new Point(40, 150, 0))
                        .setEmission(glassColor)
                        .setMaterial(new Material().setKD(kd).setKS(ks).setShininess(shininess).setKT(kt)
                                .setMattness(0.05)),

                // 4. Heavily Blurry Glass (mattness = 0.1)
                new Polygon(new Point(240, -50, 0), new Point(360, -50, 0),
                        new Point(360, 150, 0), new Point(240, 150, 0))
                        .setEmission(glassColor)
                        .setMaterial(new Material().setKD(kd).setKS(ks).setShininess(shininess).setKT(kt)
                                .setMattness(0.1))
        );

        // --- Lighting ---
        _scene.setAmbientLight(new AmbientLight(new Color(25, 25, 25)));
        _scene.lights.add(
                new SpotLight(new Color(800, 800, 800), new Point(0, 500, 500), new Vector(0, -1, -1))
                        .setKl(0.00001).setKq(0.000005)
        );
    }

    /**
     * Test case for rendering a scene with four balls and four glass elements,
     * with super-sampling anti-aliasing disabled (resolution set to 1).
     */
    @Test
    @SuppressWarnings("java:S109")
    void test4Balls4GlassesDisabled() {
        buildScene();

        // Inject RayTracer with 1 ray (Disabled)
        SimpleRayTracer tracer = new SimpleRayTracer(_scene).setSuperSamplingResolution(1);

        _cameraBuilder
                .setRayTracer(tracer)
                .build()
                .renderImage()
                .writeToImage("DiffuseGlass_Disabled");
    }

    /**
     * Test case for rendering a scene with four balls and four glass elements,
     * with super-sampling anti-aliasing enabled (9x9 grid, 81 rays per beam).
     */
    @Test
    @SuppressWarnings("java:S109")
    void test4Balls4GlassesEnabled() {
        buildScene();

        // Inject RayTracer with 9x9 = 81 rays per beam (Enabled)
        SimpleRayTracer tracer = new SimpleRayTracer(_scene).setSuperSamplingResolution(9);

        _cameraBuilder
                .setRayTracer(tracer)
                .build()
                .renderImage()
                .writeToImage("DiffuseGlass_Enabled");
    }
}