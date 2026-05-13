package parser;

import geometries.api.Geometry;
import geometries.api.Intersectable;
import geometries.impl.*;
import lighting.AmbientLight;
import primitives.*;
import scene.Scene;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * a parser for xml files
 */
public class XmlParser implements Parser{
    /** Default constructor to satisfy JavaDoc generator */
    XmlParser() { /* to satisfy JavaDoc generator */ }


    @Override
    public Scene parse(String filePath, Scene scene) {
        Deque<Geometries> compositeStack = new ArrayDeque<>();
        Geometries rootGeometries = null;

        try{
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(filePath));

            while(reader.hasNext()){
                //check whether the next text is a beginning tag.
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    //get the tag name
                    String tagName = reader.getLocalName();

                    switch(tagName){
                        case "scene":
                            String bgColorStr = reader.getAttributeValue(null, "background-color");
                            if (bgColorStr != null) {
                                scene.setBackground(parseColor(bgColorStr));
                            }
                            break;

                        case "ambient-light":
                            String amColorStr = reader.getAttributeValue(null, "color");
                            if (amColorStr != null) {
                                scene.setAmbientLight(new AmbientLight(parseColor(amColorStr)));
                            }
                            break;
                        case "geometries":
                            Geometries newComposite = new Geometries();
                            if (compositeStack.isEmpty()){
                                rootGeometries = newComposite;
                            } else{
                                compositeStack.peek().add(newComposite);
                            }
                            compositeStack.push(newComposite);
                            break;

                        case "sphere":
                            Point center = parsePoint(reader.getAttributeValue(null, "center"));
                            double radius = Double.parseDouble(reader.getAttributeValue(null, "radius"));
                            Sphere sphere = new Sphere(center, radius);
                            addShapeToStack(compositeStack, applyGeometryAttributes(sphere, reader));
                            break;

                        case "triangle":
                            Point tp0 = parsePoint(reader.getAttributeValue(null, "p0"));
                            Point tp1 = parsePoint(reader.getAttributeValue(null, "p1"));
                            Point tp2 = parsePoint(reader.getAttributeValue(null, "p2"));
                            Triangle triangle = new Triangle(tp0, tp1, tp2);
                            addShapeToStack(compositeStack, applyGeometryAttributes(triangle, reader));
                            break;

                        case "polygon":
                            List<Point> vertices = new ArrayList<>();
                            int i = 0;
                            while (true) {
                                String pStr = reader.getAttributeValue(null, "p" + i);
                                if (pStr == null) break;
                                vertices.add(parsePoint(pStr));
                                i++;
                            }
                            Polygon polygon = new Polygon(vertices.toArray(new Point[0]));
                            addShapeToStack(compositeStack, applyGeometryAttributes(polygon, reader));
                            break;

                        case "plane":
                            Point pp0 = parsePoint(reader.getAttributeValue(null, "p0"));
                            String normalStr = reader.getAttributeValue(null, "normal");
                            Plane plane;

                            if (normalStr != null) {
                                Vector normal = parseVector(normalStr);
                                plane = new Plane(pp0, normal);
                            } else {
                                Point pp1 = parsePoint(reader.getAttributeValue(null, "p1"));
                                Point pp2 = parsePoint(reader.getAttributeValue(null, "p2"));
                                plane = new Plane(pp0, pp1, pp2);
                            }
                            addShapeToStack(compositeStack, applyGeometryAttributes(plane, reader));
                            break;

                        case "tube":
                            Point tOrigin = parsePoint(reader.getAttributeValue(null, "axis-origin"));
                            Vector tDir = parseVector(reader.getAttributeValue(null, "axis-direction"));
                            double tRadius = Double.parseDouble(reader.getAttributeValue(null, "radius"));
                            Tube tube = new Tube(tRadius, new Ray(tOrigin, tDir));
                            addShapeToStack(compositeStack, applyGeometryAttributes(tube, reader));
                            break;

                        case "cylinder":
                            Point cOrigin = parsePoint(reader.getAttributeValue(null, "axis-origin"));
                            Vector cDir = parseVector(reader.getAttributeValue(null, "axis-direction"));
                            double cRadius = Double.parseDouble(reader.getAttributeValue(null, "radius"));
                            double cHeight = Double.parseDouble(reader.getAttributeValue(null, "height"));
                            Cylinder cylinder = new Cylinder(cRadius, new Ray(cOrigin, cDir), cHeight);
                            addShapeToStack(compositeStack, applyGeometryAttributes(cylinder, reader));
                            break;
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT){ // Now correctly attached to the 'if'
                    if (reader.getLocalName().equals("geometries")) {
                        if (!compositeStack.isEmpty()) {
                            compositeStack.pop();
                        }
                    }
                }
            }
            reader.close();

            if (rootGeometries != null)
                scene.setGeometries(rootGeometries);
            return scene;
        } catch (Exception e){
            throw new RuntimeException("Failed to parse XML file: " + filePath, e);
        }
    }

    /**
     * a function that inserts a geometry to the top of the stack.
     * @param stack the geometries stack
     * @param shape shape to insert into the stack
     */
    private void addShapeToStack(Deque<Geometries> stack, Intersectable shape) {
        if (!stack.isEmpty()) {
            stack.peek().add(shape);
        }
    }
    /**
     * a parser of 3 double values.
     * @param str the string to parse
     * @return the parsed double3
     */
    private Double3 parseDouble3(String str) {
        if (str == null)
            throw new IllegalArgumentException("Missing coordinate attribute in XML");
        //split according to the spaces
        String[] parts = str.trim().split("\\s+");
        return new Double3(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }
    /**
     * get the extra optional geometrical properties.
     * @param geom the geometry to apply attributes to
     * @param reader the reader in order to read the relevant attributes.
     * @return the updated geometry
     */
    private Geometry applyGeometryAttributes(Geometry geom, XMLStreamReader reader) {
        String emissionStr = reader.getAttributeValue(null, "emission");
        if (emissionStr != null) {
            geom.setEmission(parseColor(emissionStr));
        }

        String kaStr = reader.getAttributeValue(null, "kA");
        if (kaStr != null) {
            Material material = new Material();
            String[] parts = kaStr.trim().split("\\s+");

            // option for 2 constructors, with 1 parameters and 3 parameters.
            if (parts.length == 1) {
                material.setKa(Double.parseDouble(parts[0]));
            } else if (parts.length == 3) {
                material.setKa(parseDouble3(kaStr));
            }
            geom.setMaterial(material);
        }

        return geom;
    }

    /**
     * construct a point from string
     * @param str the string to parse
     * @return the parsed point
     */
    private Point parsePoint(String str) {
        return new Point(parseDouble3(str));
    }

    /**
     * construct a point from string
     * @param str the string to parse
     * @return the parsed vector
     */
    private Vector parseVector(String str) {
        return new Vector(parseDouble3(str));
    }
    /**
     * construct a color from string
     * @param str the string to parse
     * @return the parsed color
     */
    private Color parseColor(String str) {
        return new Color(parseDouble3(str));
    }

}
