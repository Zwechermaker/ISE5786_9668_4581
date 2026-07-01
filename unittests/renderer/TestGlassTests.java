package renderer;

import static java.awt.Color.*;

import org.junit.jupiter.api.Test;

import geometries.impl.Polygon;
import geometries.impl.Sphere;
import geometries.impl.Cylinder;
import geometries.impl.Triangle;
import lighting.impl.AmbientLight;
import lighting.impl.SpotLight;
import primitives.*;
import scene.Scene;

/**
 * Tests for Diffuse (Blurry) Glass functionality using super-sampling.
 * Demonstrates 4 glass panes with increasing mattness values revealing
 * objects behind them, arranged on a table with legs.
 * * @author Elad Zwecher and Benjamin Godfrey
 */
class DiffuseGlassTests {
    /** Default constructor to satisfy JavaDoc generator */
    DiffuseGlassTests() { /* to satisfy JavaDoc generator */ }

    /** Scene for the tests */
    private final Scene _scene = new Scene("Diffuse Glass Test Scene on Table");

    /** Camera builder for the tests */
    private final Camera.Builder _cameraBuilder = Camera.getBuilder()
            .setLocation(new Point(0, 50, 1000)) // Raised camera slightly to look down at the table
            .setDirection(new Point(0, 0, 0), Vector.AXIS_Y) // Pointing at the center
            .setVpDistance(1000)
            .setVpSize(1000, 500)
            .setResolution(1000, 500)
            .setMultithreading(-2)
            .setDebugPrint(1.0);

    /**
     * Helper method to populate the scene with the table, room, 4 balls, and 4 glasses.
     */
    private void buildScene() {
        // --- The Room (Floor and Wall) ---
        _scene.geometries.add(
                // Floor (Under the table, Y = -150)
                new Polygon(new Point(-1000, -150, 1000), new Point(1000, -150, 1000),
                        new Point(1000, -150, -1000), new Point(-1000, -150, -1000))
                        .setEmission(new Color(30, 30, 30))
                        .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
                // Back Wall
                new Polygon(new Point(-1000, -150, -400), new Point(1000, -150, -400),
                        new Point(1000, 1000, -400), new Point(-1000, 1000, -400))
                        .setEmission(new Color(45, 55, 65)) // Soft blue-grey wall
                        .setMaterial(new Material().setKD(0.6))
        );

        // --- The Table ---
        _scene.geometries.add(
                // Table Top (Y = -50, so objects sit exactly on it)
                new Polygon(new Point(-450, -50, 100), new Point(450, -50, 100),
                        new Point(450, -50, -250), new Point(-450, -50, -250))
                        .setEmission(new Color(80, 50, 30)) // Mahogany wood color
                        .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),

                // Table Front Edge (Gives the table 3D thickness)
                new Polygon(new Point(-450, -70, 100), new Point(450, -70, 100),
                        new Point(450, -50, 100), new Point(-450, -50, 100))
                        .setEmission(new Color(50, 30, 15)) // Darker wood for the shadowed edge
                        .setMaterial(new Material().setKD(0.6).setKS(0.1).setShininess(20))
        );

        // --- The Table Legs (Cylinders & Triangles) ---
        Material legMaterial = new Material().setKD(0.4).setKS(0.6).setShininess(30);
        Color legColor = new Color(20, 20, 22); // Dark metallic look
        double legRadius = 6.0;
        double legHeight = 100.0; // From floor (-150) to bottom of table (-50)
        Vector upVector = new Vector(0, 1, 0);

        // X and Z coordinates for the 4 table legs (inset slightly from the edges)
        double[][] legPositions = {
                {-420, 70},   // Front-Left
                {420, 70},    // Front-Right
                {420, -220},  // Back-Right
                {-420, -220}  // Back-Left
        };

        for (double[] pos : legPositions) {
            double lx = pos[0];
            double lz = pos[1];

            // 1. The Core Pillar (Cylinder)
            Cylinder legCylinder = new Cylinder(legRadius, new Ray(new Point(lx, -150, lz), upVector), legHeight);
            legCylinder.setEmission(legColor).setMaterial(legMaterial);
            _scene.geometries.add(legCylinder);

            // 2. The Reinforcement Bracket (Triangle)
            // Extends from the top of the leg inward toward the center of the table
            double bracketOffsetX = (lx < 0) ? 25.0 : -25.0; // Point inward
            Triangle bracketTriangle = new Triangle(
                    new Point(lx, -50.1, lz),                            // Top corner joint (touching table)
                    new Point(lx + bracketOffsetX, -50.1, lz),           // Anchored to table top underneath
                    new Point(lx, -80, lz)                               // Extended down the side of the leg cylinder
            );
            bracketTriangle.setEmission(legColor).setMaterial(legMaterial);
            _scene.geometries.add(bracketTriangle);
        }

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
                .writeToImage("DiffuseGlass_Disabled_Table");
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
        SimpleRayTracer tracer = new SimpleRayTracer(_scene).setSuperSamplingResolution(3);

        _cameraBuilder
                .setRayTracer(tracer)
                .build()
                .renderImage()
                .writeToImage("DiffuseGlass_Enabled_Table");
    }
}