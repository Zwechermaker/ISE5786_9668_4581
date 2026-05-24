package renderer;

import geometries.api.Intersectable;
import lighting.api.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import geometries.api.Intersectable.Intersection;
import primitives.Vector;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * a class that describes a basic ray tracer.
 */
public class SimpleRayTracer extends RayTracerBase {

    @Override
    public Color traceRay(Ray ray) {
        //does not violate the law of demeter because scene is a passive data structure
        var intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null
                ? _scene.background
                : calcColor(ray.findClosestIntersection(intersections), ray.direction());
    }

    /**
     * Constructor for SimpleRayTracer
     * @param scene the scene to trace rays in
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * a function that returns the color received in an intersection
     * @param intersection a point the intersection occurred at and calculate the color the intersection holds.
     * @return the color the intersection holds
     */
    private Color calcColor(Intersection intersection, Vector v){
        return !preprocessIntersection(intersection, v)?
                Color.BLACK :
                _scene.ambientLight.getIntensity().scale(intersection.material._kA)
                .add(calcLocalEffects(intersection));
    }

    private Color calcLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                color = color.add(
                    lightSource.getIntensity(intersection.point)
                        .scale(
                            calcDiffuse(intersection)
                                .add(calcSpecular(intersection))
                        )
                );
            }
        }
        return color;
    }
    /**
     * a function that checks if the intersection isn't 90 degrees from the normal.
     * @param intersection
     * @param v the direction of the ray
     * @return whether the intersection is valid or not
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    /**
     * a function that checks whether the light source emits light onto the intersection.
     * @param intersection the intersection to check
     * @param light the light source to check
     * @return whether the light source emits light onto the intersection or not
     */
    private boolean preprocessLightSource(Intersection intersection, LightSource light) {intersection.light = light;
        intersection.l = light.getL(intersection.point);
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }

    /**
     * calculates the diffusive component of the color
     * @param intersection of the light with the object
     * @return the diffuse factor
     */
    private Double3 calcDiffuse(Intersection intersection){
        return intersection.material._kD.
                scale(Math.abs(intersection.lNormal));
    }

    /**
     * calculates the specular component of the color
     * @param intersection of the light with the object
     * @return the color
     */
    private Double3 calcSpecular(Intersection intersection){
        Vector r = intersection.l.subtract(intersection.normal.scale(2 * intersection.lNormal));
        double vr = -intersection.v.dotProduct(r);

        return intersection.material._kS.scale(Math.pow(Math.max(0, vr), intersection.material._nShininess));
    }
}
