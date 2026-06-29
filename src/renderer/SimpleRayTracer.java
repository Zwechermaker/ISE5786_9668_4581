package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.api.LightSource;
import primitives.*;
import renderer.sampler.Jittered;
import renderer.sampler.Sampler;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * A basic implementation of a ray tracer.
 * <p>
 * This class implements the Whitted-style ray tracing algorithm, which includes
 * calculations for local illumination (diffuse and specular) as well as global
 * effects like reflection and transparency.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * A small delta value used for shifting rays to avoid self-intersection issues (shadow acne).
     */
    private static final double DELTA = 0.1;
    /**
     * The maximum recursion depth for calculating global effects (reflection and transparency).
     */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    /**
     * The minimum attenuation factor for a ray's contribution to be considered significant.
     * Rays with an attenuation factor below this threshold are terminated.
     */
    private static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * The initial attenuation factor for primary rays.
     */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * The number of rays (_superSamplingResolution by _superSamplingResolution) for super sampling.
     * Turned off by default.
     */
    private int _superSamplingResolution = 1;

    /**
     * The cached sampler for generating target rays.
     */
    private Sampler _sampler = new Jittered(_superSamplingResolution);

    /**
     * Constructs a {@link SimpleRayTracer} for a given scene.
     *
     * @param scene The scene to be rendered.
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        return intersections == null
                ? _scene.background
                : calcColor(ray.findClosestIntersection(intersections), ray.direction());
    }

    /**
     * Calculates the color at an intersection point, including local and global effects.
     *
     * @param intersection The intersection point.
     * @param v            The direction vector of the ray that caused the intersection.
     * @return The final calculated color.
     */
    private Color calcColor(Intersection intersection, Vector v) {
        return preprocessIntersection(intersection, v)
                ? _scene.ambientLight.getIntensity().scale(intersection.geometry.getMaterial()._kA)
                .add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K))
                : Color.BLACK;
    }

    /**
     * Recursively calculates the color at an intersection point, considering an accumulated attenuation factor.
     *
     * @param intersection The intersection point.
     * @param level        The current recursion depth.
     * @param k            The accumulated attenuation factor.
     * @return The calculated color.
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcLocalEffects(intersection, k);
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates the local illumination effects (diffuse and specular) at an intersection point.
     *
     * @param intersection The intersection point.
     * @param k            The accumulated attenuation factor.
     * @return The color resulting from local illumination.
     */
    private Color calcLocalEffects(Intersection intersection, Double3 k) {
        Color color = intersection.geometry.getEmission();

        for (LightSource lightSource : _scene.lights) {
            if (preprocessLightSource(intersection, lightSource)) {
                Double3 ktr = transparency(intersection);
                if (ktr.product(k).isGreaterThan(MIN_CALC_COLOR_K)) {
                    Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);
                    color = color.add(lightIntensity.scale(calcDiffuse(intersection).add(calcSpecular(intersection))));
                }
            }
        }
        return color;
    }

    /**
     * Calculates the global effects (reflection and transparency) at an intersection point.
     *
     * @param intersection The intersection point.
     * @param level        The current recursion depth.
     * @param k            The accumulated attenuation factor.
     * @return The color resulting from global effects.
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Color color = Color.BLACK;

        // reflection
        if (_superSamplingResolution <= 1 || Util.isZero(intersection.material._mattness)) {
            color = color.add(calcGlobalEffect(constructReflectionRay(intersection), level, k, intersection.material._kR));
        } else {
            color = color.add(avgCalcGlobalEffect(constructReflectionRays(intersection), level, k, intersection.material._kR));
        }

        // transparency
        if (_superSamplingResolution <= 1 || Util.isZero(intersection.material._mattness)) {
            color = color.add(calcGlobalEffect(constructTransparencyRay(intersection), level, k, intersection.material._kT));
        } else {
            color = color.add(avgCalcGlobalEffect(constructTransparencyRays(intersection), level, k, intersection.material._kT));
        }

        return color;
    }

    /**
     * Helper method to calculate a single global effect (reflection or transparency).
     *
     * @param ray   The secondary ray (reflection or transparency).
     * @param level The current recursion depth.
     * @param k     The accumulated attenuation factor.
     * @param kx    The local attenuation factor for the effect (kR or kT).
     * @return The calculated color for the global effect.
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.isLowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;

        List<Intersection> intersections = _scene.geometries.calcIntersections(ray);
        Intersection intersection = ray.findClosestIntersection(intersections);
        if (intersection == null)
            return _scene.background.scale(kx);

        return preprocessIntersection(intersection, ray.direction())
                ? calcColor(intersection, level - 1, kkx).scale(kx)
                : Color.BLACK;
    }

    /**
     * Calculates the transparency attenuation factor for a shadow ray.
     *
     * @param intersection The intersection point from which the shadow ray originates.
     * @return The cumulative transparency factor {@code ktr}.
     */
    private Double3 transparency(Intersection intersection) {
        List<Intersection> shadowIntersections = getShadowIntersections(intersection);
        if (shadowIntersections == null) return Double3.ONE;

        Double3 ktr = Double3.ONE;
        for (Intersection shadowInt : shadowIntersections) {
            ktr = ktr.product(shadowInt.geometry.getMaterial()._kT);
            if (ktr.isLowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO;
        }
        return ktr;
    }

    /**
     * Constructs a shadow ray from an intersection point towards a light source.
     *
     * @param intersection The intersection point.
     * @return A list of intersections found along the shadow ray.
     */
    private List<Intersection> getShadowIntersections(Intersection intersection) {
        Vector pointToLight = intersection.l.scale(-1);
        Vector delta = intersection.normal.scale(intersection.lNormal < 0 ? DELTA : -DELTA);
        Ray shadowRay = new Ray(intersection.point.add(delta), pointToLight);
        double lightDistance = intersection.light.getDistance(intersection.point);
        return _scene.geometries.calcIntersections(shadowRay, lightDistance);
    }

    /**
     * Helper method that reflects a given vector across a normal vector.
     * Prevents logic duplication between global reflection and specular reflection math.
     *
     * @param vec        The incoming vector to reflect.
     * @param normal     The normal vector of the surface.
     * @param dotProduct The pre-calculated dot product between the incoming vector and the normal.
     * @return The reflected {@link Vector}.
     */
    private Vector reflectVector(Vector vec, Vector normal, double dotProduct) {
        return vec.subtract(normal.scale(2 * dotProduct));
    }

    /**
     * Constructs a single reflection ray from an intersection point.
     *
     * @param intersection The intersection point.
     * @return The reflected ray.
     */
    private Ray constructReflectionRay(Intersection intersection) {
        Vector r = reflectVector(intersection.v, intersection.normal, intersection.vNormal);
        double rn = alignZero(r.dotProduct(intersection.normal));
        Vector delta = intersection.normal.scale(rn < 0 ? -DELTA : DELTA);
        return new Ray(intersection.point.add(delta), r);
    }

    /**
     * A function that generates a beam of rays through the black board that it generates
     * according to the ray and the mattness.
     *
     * @param ray      The secondary center ray.
     * @param mattness The spread size of the target area.
     * @return A list of rays to shoot through the blackboard.
     */
    private List<Ray> generateBeam(Ray ray, double mattness) {
        Point center = ray.getPoint(1);

        Vector vRight = Vector.AXIS_Y;
        Vector vUp = null;
        if (vRight.areParallel(ray.direction())){
            vRight = Vector.AXIS_Z;
        }
        vRight = ray.direction().crossProduct(vRight).normalize();
        vUp = vRight.crossProduct(ray.direction());

        BlackBoard targetArea = new BlackBoard(vUp, vRight, mattness, mattness, center);

        return targetArea.generateBeam(ray.origin(), _sampler);
    }

    /**
     * A function that generates a beam of reflection rays for glossy surfaces.
     *
     * @param intersection The intersection point.
     * @return A list of rays (beam) of reflection rays.
     */
    private List<Ray> constructReflectionRays(Intersection intersection) {
        return generateBeam(constructReflectionRay(intersection), intersection.material._mattness);
    }

    /**
     * Constructs a single transparency ray from an intersection point.
     *
     * @param intersection The intersection point.
     * @return The transparency ray.
     */
    private Ray constructTransparencyRay(Intersection intersection) {
        Vector delta = intersection.normal.scale(intersection.vNormal < 0 ? -DELTA : DELTA);
        return new Ray(intersection.point.add(delta), intersection.v);
    }

    /**
     * A function that generates a beam of transparency rays for diffuse glass surfaces.
     *
     * @param intersection The intersection point.
     * @return A list of rays (beam) of transparency rays.
     */
    private List<Ray> constructTransparencyRays(Intersection intersection) {
        return generateBeam(constructTransparencyRay(intersection), intersection.material._mattness);
    }

    /**
     * Pre-calculates necessary values for an intersection.
     *
     * @param intersection The intersection to process.
     * @param v            The direction of the ray.
     * @return {@code true} if the intersection is valid, otherwise {@code false}.
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(intersection.v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    /**
     * Pre-calculates necessary values for a light source at an intersection.
     *
     * @param intersection The intersection to process.
     * @param light        The light source.
     * @return {@code true} if the light source illuminates the intersection, otherwise {@code false}.
     */
    private boolean preprocessLightSource(Intersection intersection, LightSource light) {
        intersection.light = light;
        intersection.l = light.getL(intersection.point);
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }

    /**
     * Calculates the diffuse reflection component.
     *
     * @param intersection The intersection point.
     * @return The diffuse reflection factor.
     */
    private Double3 calcDiffuse(Intersection intersection) {
        return intersection.material._kD.scale(Math.abs(intersection.lNormal));
    }

    /**
     * Calculates the specular reflection component.
     *
     * @param intersection The intersection point.
     * @return The specular reflection factor.
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector r = reflectVector(intersection.l, intersection.normal, intersection.lNormal);
        double vr = -intersection.v.dotProduct(r);
        return intersection.material._kS.scale(Math.pow(Math.max(0, vr), intersection.material._nShininess));
    }

    /**
     * A function that calculates the average color a beam returns.
     *
     * @param beam  A list of rays to trace.
     * @param level The current recursion depth.
     * @param k     The accumulated attenuation factor.
     * @param kx    The local attenuation factor for the effect (kR or kT).
     * @return The average color the beam gets.
     */
    Color avgCalcGlobalEffect(List<Ray> beam, int level, Double3 k, Double3 kx){
        Color avg = Color.BLACK;
        for (Ray ray : beam) {
            avg = avg.add(calcGlobalEffect(ray, level, k, kx));
        }
        return avg.reduce(beam.size());
    }

    /**
     * Sets the super-sampling resolution and instantly updates the cached sampler.
     *
     * @param resolution The resolution for super-sampling (e.g. 3 for a 3x3 grid).
     * @return This {@link SimpleRayTracer} instance for method chaining.
     */
    public SimpleRayTracer setSuperSamplingResolution(int resolution) {
        this._superSamplingResolution = resolution;
        this._sampler = new Jittered(resolution);
        return this;
    }
}