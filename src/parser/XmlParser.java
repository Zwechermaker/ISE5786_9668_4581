package parser;

import geometries.api.Geometry;
import geometries.api.Intersectable;
import geometries.impl.*;
import lighting.impl.AmbientLight;
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
public class XmlParser implements Parser {
    /**
     * Default constructor to satisfy JavaDoc generator
     */
    XmlParser() { /* to satisfy JavaDoc generator */
    }

    @Override
    public Scene parse(String filePath, Scene scene) {
        Deque<Geometries> compositeStack = new ArrayDeque<>();
        Geometries rootGeometries = null;

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(filePath));

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String tagName = reader.getLocalName();
                    switch (tagName) {
                        case "scene":
                            parseScene(reader, scene);
                            break;
                        case "ambient-light":
                            parseAmbientLight(reader, scene);
                            break;
                        case "geometries":
                            rootGeometries = handleGeometriesStart(compositeStack, rootGeometries);
                            break;
                        case "sphere":
                            parseSphere(reader, compositeStack);
                            break;
                        case "triangle":
                            parseTriangle(reader, compositeStack);
                            break;
                        case "polygon":
                            parsePolygon(reader, compositeStack);
                            break;
                        case "plane":
                            parsePlane(reader, compositeStack);
                            break;
                        case "tube":
                            parseTube(reader, compositeStack);
                            break;
                        case "cylinder":
                            parseCylinder(reader, compositeStack);
                            break;
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if (reader.getLocalName().equals("geometries")) {
                        handleGeometriesEnd(compositeStack);
                    }
                }
            }
            reader.close();

            if (rootGeometries != null)
                scene.setGeometries(rootGeometries);
            return scene;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML file: " + filePath, e);
        }
    }

    /**
     * Parses the scene attributes from the XML.
     *
     * @param reader The XML stream reader.
     * @param scene  The scene to configure.
     */
    private void parseScene(XMLStreamReader reader, Scene scene) {
        String bgColorStr = reader.getAttributeValue(null, "background-color");
        if (bgColorStr != null) {
            scene.setBackground(parseColor(bgColorStr));
        }
    }

    /**
     * Parses the ambient light attributes from the XML.
     *
     * @param reader The XML stream reader.
     * @param scene  The scene to configure.
     */
    private void parseAmbientLight(XMLStreamReader reader, Scene scene) {
        String amColorStr = reader.getAttributeValue(null, "color");
        if (amColorStr != null) {
            scene.setAmbientLight(new AmbientLight(parseColor(amColorStr)));
        }
    }

    /**
     * Handles the start of a geometries block.
     *
     * @param compositeStack The stack of composite geometries.
     * @param rootGeometries The root geometries object.
     * @return The updated root geometries object.
     */
    private Geometries handleGeometriesStart(Deque<Geometries> compositeStack, Geometries rootGeometries) {
        Geometries newComposite = new Geometries();
        if (compositeStack.isEmpty()) {
            rootGeometries = newComposite;
        } else {
            compositeStack.peek().add(newComposite);
        }
        compositeStack.push(newComposite);
        return rootGeometries;
    }

    /**
     * Handles the end of a geometries block.
     *
     * @param compositeStack The stack of composite geometries.
     */
    private void handleGeometriesEnd(Deque<Geometries> compositeStack) {
        if (!compositeStack.isEmpty()) {
            compositeStack.pop();
        }
    }

    /**
     * Parses a sphere from the XML and adds it to the scene.
     *
     * @param reader         The XML stream reader.
     * @param compositeStack The stack of composite geometries.
     */
    private void parseSphere(XMLStreamReader reader, Deque<Geometries> compositeStack) {
        Point center = parsePoint(reader.getAttributeValue(null, "center"));
        double radius = Double.parseDouble(reader.getAttributeValue(null, "radius"));
        Sphere sphere = new Sphere(center, radius);
        addShapeToStack(compositeStack, applyGeometryAttributes(sphere, reader));
    }

    /**
     * Parses a triangle from the XML and adds it to the scene.
     *
     * @param reader         The XML stream reader.
     * @param compositeStack The stack of composite geometries.
     */
    private void parseTriangle(XMLStreamReader reader, Deque<Geometries> compositeStack) {
        Point tp0 = parsePoint(reader.getAttributeValue(null, "p0"));
        Point tp1 = parsePoint(reader.getAttributeValue(null, "p1"));
        Point tp2 = parsePoint(reader.getAttributeValue(null, "p2"));
        Triangle triangle = new Triangle(tp0, tp1, tp2);
        addShapeToStack(compositeStack, applyGeometryAttributes(triangle, reader));
    }

    /**
     * Parses a polygon from the XML and adds it to the scene.
     *
     * @param reader         The XML stream reader.
     * @param compositeStack The stack of composite geometries.
     */
    private void parsePolygon(XMLStreamReader reader, Deque<Geometries> compositeStack) {
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
    }

    /**
     * Parses a plane from the XML and adds it to the scene.
     *
     * @param reader         The XML stream reader.
     * @param compositeStack The stack of composite geometries.
     */
    private void parsePlane(XMLStreamReader reader, Deque<Geometries> compositeStack) {
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
    }

    /**
     * Parses a tube from the XML and adds it to the scene.
     *
     * @param reader         The XML stream reader.
     * @param compositeStack The stack of composite geometries.
     */
    private void parseTube(XMLStreamReader reader, Deque<Geometries> compositeStack) {
        Point tOrigin = parsePoint(reader.getAttributeValue(null, "axis-origin"));
        Vector tDir = parseVector(reader.getAttributeValue(null, "axis-direction"));
        double tRadius = Double.parseDouble(reader.getAttributeValue(null, "radius"));
        Tube tube = new Tube(tRadius, new Ray(tOrigin, tDir));
        addShapeToStack(compositeStack, applyGeometryAttributes(tube, reader));
    }

    /**
     * Parses a cylinder from the XML and adds it to the scene.
     *
     * @param reader         The XML stream reader.
     * @param compositeStack The stack of composite geometries.
     */
    private void parseCylinder(XMLStreamReader reader, Deque<Geometries> compositeStack) {
        Point cOrigin = parsePoint(reader.getAttributeValue(null, "axis-origin"));
        Vector cDir = parseVector(reader.getAttributeValue(null, "axis-direction"));
        double cRadius = Double.parseDouble(reader.getAttributeValue(null, "radius"));
        double cHeight = Double.parseDouble(reader.getAttributeValue(null, "height"));
        Cylinder cylinder = new Cylinder(cRadius, new Ray(cOrigin, cDir), cHeight);
        addShapeToStack(compositeStack, applyGeometryAttributes(cylinder, reader));
    }

    /**
     * Adds a shape to the current composite geometry.
     *
     * @param stack The stack of composite geometries.
     * @param shape The shape to add.
     */
    private void addShapeToStack(Deque<Geometries> stack, Intersectable shape) {
        if (!stack.isEmpty()) {
            stack.peek().add(shape);
        }
    }

    /**
     * Parses a string of three double values into a {@link Double3}.
     *
     * @param str The string to parse.
     * @return The parsed {@link Double3}.
     * @throws IllegalArgumentException if the string is null.
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
     * Applies geometry attributes (emission, material) to a {@link Geometry}.
     *
     * @param geom   The geometry to modify.
     * @param reader The XML stream reader.
     * @return The modified geometry.
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
                material.setKA(Double.parseDouble(parts[0]));
            } else if (parts.length == 3) {
                material.setKA(parseDouble3(kaStr));
            }
            geom.setMaterial(material);
        }

        return geom;
    }

    /**
     * Parses a string into a {@link Point}.
     *
     * @param str The string to parse.
     * @return The parsed {@link Point}.
     */
    private Point parsePoint(String str) {
        return new Point(parseDouble3(str));
    }

    /**
     * Parses a string into a {@link Vector}.
     *
     * @param str The string to parse.
     * @return The parsed {@link Vector}.
     */
    private Vector parseVector(String str) {
        return new Vector(parseDouble3(str));
    }

    /**
     * Parses a string into a {@link Color}.
     *
     * @param str The string to parse.
     * @return The parsed {@link Color}.
     */
    private Color parseColor(String str) {
        return new Color(parseDouble3(str));
    }
}
