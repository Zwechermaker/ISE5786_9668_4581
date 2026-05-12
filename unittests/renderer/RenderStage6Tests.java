package renderer;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import primitives.*;
import scene.Scene;

import parser.Parser;
import parser.ParserFactory;
import parser.ParserType;

/**
 * Test rendering a basic image
 * @author Dan
 */
@SuppressWarnings("java:S109")
class RenderStage6Tests {
   /** Default constructor to satisfy JavaDoc generator */
   RenderStage6Tests() { /* to satisfy JavaDoc generator */ }

   /** Z axis location of triangles */
   private static final double Z          = -100D;
   /** Left, Top point */
   private static final Point  P_LT       = new Point(-100, 100, Z);
   /** Left, Middle point */
   private static final Point  P_LM       = new Point(-100, 0, Z);
   /** Left, Bottom point */
   private static final Point  P_LB       = new Point(-100, -100, Z);
   /** Middle, Top point */
   private static final Point  P_MT       = new Point(0, 100, Z);
   /** Middle, Bottom point */
   private static final Point  P_MB       = new Point(0, -100, Z);
   /** Right, Middle point */
   private static final Point  P_RM       = new Point(100, 0, Z);
   /** Right, Bottom point */
   private static final Point  P_RB       = new Point(100, -100, Z);
   /** Sphere center point */
   private static final Point  O          = new Point(0, 0, Z);
   /** Sphere radius */
   private static final double RADIUS     = 50D;
   /** The resolution */
   private static int          RESOLUTION = 1000;

   /**
    * Build camera and render image with grid
    * @param scene    the scene to be used for the image
    * @param fileName the name of the image file
    */
   private static void createImage(Scene scene, String fileName) {
      Camera.getBuilder() //
         .setResolution(RESOLUTION, RESOLUTION) //
         .setLocation(Point.ZERO).setDirection(new Point(0, 0, -1), Vector.AXIS_Y) //
         .setVpDistance(100).setVpSize(500, 500) //
         .setRayTracer(scene, RayTracerType.SIMPLE) //
         .build() //
         .renderImage() //
         .printGrid(100, new Color(WHITE)) //
         .writeToImage(fileName);
   }

   /**
    * Produce a scene with basic 3D model - including individual emission lights of
    * the
    * bodies and render it into a png image with a grid
    */
   @Test
   void testRenderEmissionColor() {
      Scene scene = new Scene("Emission color").setAmbientLight(new AmbientLight(new Color(51, 51, 51)));
      scene.geometries //
         .add(// center
              new Sphere(O, RADIUS), // no emission
              // up left
              new Triangle(P_LM, P_MT, P_LT).setEmission(new Color(GREEN)),
              // down left
              new Triangle(P_LM, P_MB, P_LB).setEmission(new Color(RED)),
              // down right
              new Triangle(P_RM, P_MB, P_RB).setEmission(new Color(BLUE)));
      createImage(scene, "emission render test");
   }

   /**
    * Produce a scene with basic 3D model - including ambient light attenuation
    * factors of the
    * bodies and render it into a png image with a grid
    */
   @Test
   void testRenderAmbientColor() {
      Scene scene = new Scene("Ambient colors").setAmbientLight(new AmbientLight(new Color(WHITE)));; // TODO by students
      scene.geometries //
         .add(// center
              new Sphere(O, RADIUS)
                      .setMaterial(new Material().setKa(new Double3(0.4))),
              // up left
              new Triangle(P_LM, P_MT, P_LT)
                      .setMaterial(new Material().setKa(new Double3(0,0.8,0)))
                      .setEmission(new Color(GREEN)),
              // down left
              new Triangle(P_LM, P_MB, P_LB)
                      .setMaterial(new Material().setKa(new Double3(0.8,0,0)))
                      .setEmission(new Color(RED)),
                 // down right
              new Triangle(P_RM, P_MB, P_RB)
                      .setMaterial(new Material().setKa(new Double3(0,0,0.8)))
                      .setEmission(new Color(BLUE))
         );
      createImage(scene, "ambient render test");
   }
   /**
    * Produce a scene with basic 3D model - including ambient light attenuation
    * factors and emission colors loaded entirely from an XML file.
    */
   @Test
   void testRenderAmbientColorXml() {
      Scene scene = new Scene("XML Emission colors");
      String fullPath = "structured scenes/xmlFiles/RenderTestEmission.xml";

      Parser myParser = ParserFactory.getParser(ParserType.XML);
      myParser.parse(fullPath, scene);

      createImage(scene, "emission render test xml");
   }
}
