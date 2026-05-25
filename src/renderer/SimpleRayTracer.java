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

    /**
     * a slight shifting variable in order to prevent leopard skin bug
     */
    private static final double DELTA = 0.1;
    /**
     * the maximum depth of calculations
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    /**
     * the minimum depth of calculations
     */
    private static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * the initial multiplication for the reflectance and reflection
     */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * a function that calculates if an  intersection is shaded
     * @param intersection that we need to check
     * @return true of its shaded and false if it isn't
     */
    private boolean unshaded(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);
        Vector delta = intersection.normal.scale(intersection.lNormal < 0 ? DELTA : -DELTA);
        Ray shadowRay = new Ray(intersection.point.add(delta), pointToLight);
        return _scene.geometries.findIntersections(shadowRay) == null;
    }

    @Override
    public Color traceRay(Ray ray) {
        //does not violate the law of demeter because scene is a passive data structure
        var intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null
                ? _scene.background : calcColor(ray.findClosestIntersection(intersections), ray.direction());
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
    private Color calcColor(Intersection intersection, Vector v) {
        return preprocessIntersection(intersection, v)
                ? _scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial()._kA)
                  .add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K)) : Color.BLACK;
    }
    // Recursive color calculation (without Ambient Light)
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, k);
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
    }
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        return calcColorGLobalEffect(constructTransparencyRay(intersection), level, k, intersection.material._kT)
            .add(calcColorGLobalEffect(constructReflectionRay(intersection),
                    level, k, intersection.material._kR));
    }
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {Double3 kkx = k.product(kx);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;
        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null) return scene.background.scale(kx);
        return preprocessIntersection(intersection, ray.getDirection())
                ? calcColor(intersection, level – 1, kkx).scale(kx)
                : Color.BLACK;
    }
    /**
     * calculates the color at a point
     * @param intersection that we need to check its color
     * @return the color
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                Double3 ktr = transparency(intersection);
                if(ktr.multiply(k).isGreaterThan(MIN_CALC_COLOR_K)){
                    color = color.add(
                            lightSource.getIntensity(intersection.point)
                                    .scale(ktr)
                                    .scale(
                                            calcDiffuse(intersection)
                                                    .add(calcSpecular(intersection))
                                    )
                    );
                }
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
