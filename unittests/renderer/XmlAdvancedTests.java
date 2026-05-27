package renderer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import parser.Parser;
import parser.ParserFactory;
import parser.ParserType;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

/**
 * Tests for rendering scenes built entirely from XML files,
 * including shadows, transparency, and reflection.
 */
class XmlAdvancedTests {

    /** The directory path where the XML files for tests are stored. */
    private static final String XML_DIR = "structured scenes/xmlFiles/";

    /** Default camera location for standard scenes. */
    private static final Point DEFAULT_CAMERA_LOC = new Point(0, 0, 1000);

    /** Default view plane distance for standard scenes. */
    private static final int DEFAULT_VP_DISTANCE = 1000;

    /** Standard view plane size (width and height). */
    private static final int DEFAULT_VP_SIZE = 200;

    /** Standard image resolution (width and height). */
    private static final int DEFAULT_RESOLUTION = 600;

    /** Alternative lower resolution used for specific tests. */
    private static final int ALT_RESOLUTION = 500;

    /** Default constructor to satisfy JavaDoc generator. */
    XmlAdvancedTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Helper method to parse the XML, build the camera, and render the image.
     *
     * @param xmlFileName the name of the XML file to parse
     * @param imageName   the name of the output image file
     * @param cameraLoc   the location of the camera in the scene
     * @param vpDistance  the distance from the camera to the view plane
     * @param vpSizeX     the width of the view plane
     * @param vpSizeY     the height of the view plane
     * @param resX        the X resolution (width) of the generated image
     * @param resY        the Y resolution (height) of the generated image
     */
    private void renderXmlScene(String xmlFileName, String imageName, Point cameraLoc,
                                int vpDistance, int vpSizeX, int vpSizeY, int resX, int resY) {

        Scene scene = new Scene("XML Scene: " + imageName);
        Parser parser = ParserFactory.getParser(ParserType.XML);
        parser.parse(XML_DIR + xmlFileName, scene);

        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(cameraLoc)
                .setDirection(Point.ZERO, Vector.AXIS_Y)
                .setVpDistance(vpDistance)
                .setVpSize(vpSizeX, vpSizeY)
                .setResolution(resX, resY)
                .build()
                .renderImage()
                .writeToImage(imageName);
    }

    /**
     * Test generating a scene with a sphere and a triangle to check basic shadowing
     * loaded via XML.
     */
    @Test
    void testXmlShadowSphereTriangleInitial() {
        renderXmlScene("shadowSphereTriangleInitial.xml", "xmlShadowSphereTriangleInitial",
                DEFAULT_CAMERA_LOC, DEFAULT_VP_DISTANCE, DEFAULT_VP_SIZE, DEFAULT_VP_SIZE, DEFAULT_RESOLUTION, DEFAULT_RESOLUTION);
    }

    /**
     * Test generating a scene with multiple triangles and a sphere to check
     * complex shadowing loaded via XML.
     */
    @Test
    void testXmlShadowTrianglesSphere() {
        renderXmlScene("shadowTrianglesSphere.xml", "xmlShadowTrianglesSphere",
                DEFAULT_CAMERA_LOC, DEFAULT_VP_DISTANCE, DEFAULT_VP_SIZE, DEFAULT_VP_SIZE, DEFAULT_RESOLUTION, DEFAULT_RESOLUTION);
    }

    /**
     * Test generating a scene with two spheres to verify refraction and transparency
     * properties loaded via XML.
     */
    @Test
    void testXmlRefractionTwoSpheres() {
        renderXmlScene("refractionTwoSpheres.xml", "xmlRefractionTwoSpheres",
                DEFAULT_CAMERA_LOC, DEFAULT_VP_DISTANCE, 150, 150, ALT_RESOLUTION, ALT_RESOLUTION);
    }

    /**
     * Test generating a scene with mirrored triangles and spheres to verify
     * reflection calculations loaded via XML. Uses a distant camera configuration.
     */
    @Test
    void testXmlReflectionTwoSpheresMirrored() {
        renderXmlScene("reflectionTwoSpheresMirrored.xml", "xmlReflectionTwoSpheresMirrored",
                new Point(0, 0, 10000), 10000, 2500, 2500, ALT_RESOLUTION, ALT_RESOLUTION);
    }

    /**
     * Test generating a scene combining transparency and shadowing to ensure
     * light attenuates correctly through partially transparent objects via XML.
     */
    @Test
    void testXmlRefractionShadow() {
        renderXmlScene("refractionShadow.xml", "xmlRefractionShadow",
                DEFAULT_CAMERA_LOC, DEFAULT_VP_DISTANCE, DEFAULT_VP_SIZE, DEFAULT_VP_SIZE, DEFAULT_RESOLUTION, DEFAULT_RESOLUTION);
    }
    /**
     * Renders the custom Snow Globe scene to fulfill Bonus 1 requirements.
     */
    @Disabled
    @Test
    void testXmlSnowGlobe() {
        // Camera pulled back further and elevated for a perfect viewing angle
        Point cameraLoc = new Point(0, -300, 45);
        Vector cameraVUp = new Vector(0, 0, 1);
        Vector cameraVTo = Point.ZERO.subtract(cameraLoc).normalize();

        Scene scene = new Scene("XML Scene: Snow Globe");
        Parser parser = ParserFactory.getParser(ParserType.XML);
        parser.parse("structured scenes/xmlFiles/snowGlobe.xml", scene);

        Camera.getBuilder()
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setLocation(cameraLoc)
                .setDirection(cameraVTo, cameraVUp)
                .setVpDistance(1000)
                .setVpSize(200, 200)
                .setResolution(800, 800) // High res for the final image!
                .build()
                .renderImage()
                .writeToImage("snowGlobeBonus");
    }
}