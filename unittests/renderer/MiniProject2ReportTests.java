package renderer;

import geometries.impl.Geometries;
import geometries.impl.Polygon;
import geometries.impl.Sphere;
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
 */
public class MiniProject2ReportTests {

    private Scene buildScene() {
        Scene scene = new Scene("Asteroid Belt")
                .setBackground(new Color(5, 5, 10)); // Deep space dark blue

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

        // 3. The Spaceship Frosted Window (Left half of the screen only)
        Polygon frostedWindow = new Polygon(
                new Point(-150, -150, -50),
                new Point(0, -150, -50),     // Stops in the middle of the screen (X=0)
                new Point(0, 150, -50),
                new Point(-150, 150, -50)
        );

        Material windowMaterial = new Material().setKD(0.1).setKS(0.1).setKT(0.85).setShininess(20);
        frostedWindow.setEmission(new Color(0, 5, 10)).setMaterial(windowMaterial);
        scene.geometries.add(frostedWindow);

        // 5. Lighting: A bright distant sun coming from the top right
        scene.lights.add(new PointLight(new Color(800, 800, 800), new Point(150, 100, 50))
                .setKl(0.00001).setKq(0.000001));

        return scene;
    }

    private Camera.Builder prepareCamera(Scene scene) {
        return Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(new Point(0, 0, 100))
                .setDirection(new Point(0, 0, -200))
                .setVpDistance(100)
                .setVpSize(150, 150)
                .setResolution(400, 400);
    }

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
