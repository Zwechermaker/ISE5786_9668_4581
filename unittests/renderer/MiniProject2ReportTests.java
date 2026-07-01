package renderer;

import geometries.impl.Geometries;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
import lighting.impl.AmbientLight;
import lighting.impl.PointLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

import java.util.Random;

/**
 * Benchmark test class for the Mini-Project 2 Report.
 * Compares rendering times with and without Multithreading and BVH Acceleration.
 * Now featuring Anti-Aliasing and Diffuse (Matt) Glass!
 */
public class MiniProject2ReportTests {

    /** Default constructor to satisfy JavaDoc generator */
    MiniProject2ReportTests() { /* to satisfy JavaDoc generator */ }

    /**
     * A method that generates an asteroid belt around a planet, viewed through a spaceship window.
     *
     * @return The generated scene.
     */
    private Scene buildScene() {
        Scene scene = new Scene("Asteroid Belt through Window")
                .setBackground(new Color(5, 5, 10)); // Deep space dark blue

        // ================= BACKGROUND: PLANET & ASTEROIDS =================

        // 1. The Central Planet
        Sphere planet = new Sphere(new Point(0, 0, -200), 45);
        planet.setEmission(new Color(10, 20, 60))
                .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(20));
        scene.geometries.add(planet);

        // 2. The Asteroid Ring (2,500 Spheres)
        Geometries asteroidRing = new Geometries();
        Random rand = new Random(42); // Fixed seed so all 4 tests render the exact same rocks

        for (int i = 0; i < 2500; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = 60 + rand.nextDouble() * 50; // Orbit radius between 60 and 110
            double height = (rand.nextDouble() - 0.5) * 10; // Slight vertical variation in the ring

            double x = distance * Math.cos(angle);
            double z = distance * Math.sin(angle) - 200; // Centered around the planet at Z = -200
            double y = height;

            double radius = 0.5 + rand.nextDouble() * 2.5; // Random sizes for asteroids

            // Give them a dusty, rocky color
            Color rockColor = new Color(
                    80 + rand.nextInt(40),
                    70 + rand.nextInt(40),
                    60 + rand.nextInt(40)
            );

            Sphere asteroid = new Sphere(new Point(x, y, z), radius);
            asteroid.setEmission(rockColor)
                    .setMaterial(new Material().setKD(0.8).setKS(0.1).setShininess(10));

            asteroidRing.add(asteroid);
        }
        scene.geometries.add(asteroidRing);


        // ================= FOREGROUND: THE SPACESHIP INTERIOR =================

        Material wallMaterial = new Material().setKD(0.6).setKS(0.1).setShininess(5);
        Color wallColor = new Color(30, 35, 40); // Dark grey interior

        // 3. The Wall (Built out of 4 polygons to leave a hole for the window)
        // Window opening boundaries: X from -40 to 40, Y from -15 to 40 at Z = 0
        Polygon wallLeft = new Polygon(
                new Point(-150, -150, 0), new Point(-40, -150, 0),
                new Point(-40, 150, 0), new Point(-150, 150, 0)
        );
        Polygon wallRight = new Polygon(
                new Point(40, -150, 0), new Point(150, -150, 0),
                new Point(150, 150, 0), new Point(40, 150, 0)
        );
        Polygon wallTop = new Polygon(
                new Point(-40, 40, 0), new Point(40, 40, 0),
                new Point(40, 150, 0), new Point(-40, 150, 0)
        );
        Polygon wallBottom = new Polygon(
                new Point(-40, -150, 0), new Point(40, -150, 0),
                new Point(40, -15, 0), new Point(-40, -15, 0)
        );

        wallLeft.setEmission(wallColor).setMaterial(wallMaterial);
        wallRight.setEmission(wallColor).setMaterial(wallMaterial);
        wallTop.setEmission(wallColor).setMaterial(wallMaterial);
        wallBottom.setEmission(wallColor).setMaterial(wallMaterial);
        scene.geometries.add(wallLeft, wallRight, wallTop, wallBottom);

        // 4. The Window Frame (Slightly extruded towards the camera at Z = 2)
        Material frameMaterial = new Material().setKD(0.3).setKS(0.8).setShininess(50);
        Color frameColor = new Color(15, 15, 15); // Black metallic frame

        Polygon frameLeft = new Polygon(
                new Point(-42, -17, 2), new Point(-40, -17, 2),
                new Point(-40, 42, 2), new Point(-42, 42, 2)
        );
        Polygon frameRight = new Polygon(
                new Point(40, -17, 2), new Point(42, -17, 2),
                new Point(42, 42, 2), new Point(40, 42, 2)
        );
        Polygon frameTop = new Polygon(
                new Point(-40, 40, 2), new Point(40, 40, 2),
                new Point(40, 42, 2), new Point(-40, 42, 2)
        );
        Polygon frameBottom = new Polygon(
                new Point(-40, -17, 2), new Point(40, -17, 2),
                new Point(40, -15, 2), new Point(-40, -15, 2)
        );

        frameLeft.setEmission(frameColor).setMaterial(frameMaterial);
        frameRight.setEmission(frameColor).setMaterial(frameMaterial);
        frameTop.setEmission(frameColor).setMaterial(frameMaterial);
        frameBottom.setEmission(frameColor).setMaterial(frameMaterial);
        scene.geometries.add(frameLeft, frameRight, frameTop, frameBottom);

        // 5. The Diffused Glass Window
        Polygon frostedWindow = new Polygon(
                new Point(-40, -15, 0.5), new Point(40, -15, 0.5),
                new Point(40, 40, 0.5), new Point(-40, 40, 0.5)
        );
        // Added a TINY bit of mattness (0.005) so the distant planet/asteroids are just slightly blurred
        Material windowMaterial = new Material().setKD(0.1).setKS(0.1).setKT(0.85).setShininess(20).setMattness(0.005);
        frostedWindow.setEmission(new Color(0, 5, 10)).setMaterial(windowMaterial);
        scene.geometries.add(frostedWindow);

        // 6. The Table (Placed in front of the wall, inside the room)
        Polygon tableTop = new Polygon(
                new Point(-30, -25, 10), new Point(30, -25, 10),
                new Point(30, -25, 40), new Point(-30, -25, 40)
        );
        Material tableMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(40);
        tableTop.setEmission(new Color(40, 20, 10)).setMaterial(tableMaterial); // Mahogany wood color
        scene.geometries.add(tableTop);

        // Add a few trinkets to the table

        // Shiny Reflective Ball
        Sphere trinket1 = new Sphere(new Point(-15, -21, 25), 4);
        trinket1.setEmission(new Color(50, 10, 10))
                .setMaterial(new Material().setKD(0.2).setKS(0.8).setKR(0.3).setShininess(60));

        // Matt Glass Ball (No reflectance, heavily diffused transparency)
        Sphere trinket2 = new Sphere(new Point(10, -22, 18), 3);
        trinket2.setEmission(new Color(0, 0, 0)) // Clear glass
                .setMaterial(new Material().setKD(0.1).setKS(0.1).setKT(0.85).setKR(0.0).setMattness(0.05).setShininess(30));

        scene.geometries.add(trinket1, trinket2);

        // ================= LIGHTING =================

        // Light 1: Inside the spaceship (illuminates the interior wall and table)
        scene.lights.add(new PointLight(new Color(600, 600, 600), new Point(50, 50, 50))
                .setKl(0.0001).setKq(0.00001));

        // Light 2: A distant space sun (Main Key Light) from the top right
        scene.lights.add(new PointLight(new Color(900, 800, 700), new Point(-200, 200, -50))
                .setKl(0.00001).setKq(0.000001));

        // Light 3: A soft space fill light from the bottom left to fix the "hole" illusion
        scene.lights.add(new PointLight(new Color(150, 150, 250), new Point(200, -200, -50))
                .setKl(0.00001).setKq(0.000001));

        // Optional: Add a very faint ambient light to ensure absolutely nothing is pitch black
        scene.setAmbientLight(new AmbientLight(new Color(10, 10, 15)));

        return scene;
    }

    /**
     * A method that builds the camera with the correct parameters, including anti-aliasing and super-sampling.
     * @param scene The scene to render.
     * @return The camera builder.
     */
    private Camera.Builder prepareCamera(Scene scene) {
        // Injecting the SimpleRayTracer manually to set the SuperSamplingResolution for the Mattness (Blur) effect
        SimpleRayTracer tracer = new SimpleRayTracer(scene).setSuperSamplingResolution(3);

        return Camera.getBuilder()
                .setRayTracer(tracer)
                .setAntiAliasing(3)  // 3x3 Anti-Aliasing to smooth out the jagged edges
                .setLocation(new Point(0, 0, 100))
                .setDirection(new Point(0, 0, -200)) // Looking through the window at the planet
                .setVpDistance(100)
                .setVpSize(150, 150)
                .setResolution(500, 500); // Bump this up if you want higher quality testing!
    }

    /**
     * A method to render the asteroid belt scene with no multi-threading and no BVH.
     */
    @Test
    public void testA_NoThreads_NoBVH() {
        Scene scene = buildScene();
        prepareCamera(scene)
                .setMultithreading(0)
                .setDebugPrint(10)
                .build()
                .renderImage()
                .writeToImage("Report_A_NoThreads_NoBVH");
    }

    /**
     * A method to render the asteroid belt scene with multi-threading and no BVH.
     */
    @Test
    public void testB_Threads_NoBVH() {
        Scene scene = buildScene();
        prepareCamera(scene)
                .setMultithreading(-2)
                .setDebugPrint(10)
                .build()
                .renderImage()
                .writeToImage("Report_B_Threads_NoBVH");
    }

    /**
     * A method to render the asteroid belt scene with no multi-threading and BVH.
     */
    @Test
    public void testC_NoThreads_BVH() {
        Scene scene = buildScene();
        prepareCamera(scene)
                .enableBVH()
                .setMultithreading(0)
                .setDebugPrint(5)
                .build()
                .renderImage()
                .writeToImage("Report_C_NoThreads_BVH");
    }

    /**
     * A method to render the asteroid belt scene with multi-threading and BVH.
     */
    @Test
    public void testD_Threads_BVH() {
        Scene scene = buildScene();
        prepareCamera(scene)
                .enableBVH()
                .setMultithreading(-2)
                .setDebugPrint(5)
                .build()
                .renderImage()
                .writeToImage("Report_D_Threads_BVH");
    }
}