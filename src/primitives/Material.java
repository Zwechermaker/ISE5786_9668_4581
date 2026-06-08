package primitives;

/**
 * A class representing the material properties of a geometric object.
 * <p>
 * This class is a passive data structure (struct-like) that holds coefficients
 * for how a material interacts with light, including its diffuse, specular,
 * and reflective properties. It uses a fluent API for setting properties.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class Material {
    /**
     * The ambient reflection coefficient. This determines how the material reflects ambient light.
     * It is often set to the same color as the diffuse reflection.
     */
    public Double3 _kA = Double3.ONE;
    /**
     * The diffuse reflection coefficient. This determines the color of the material under direct, even lighting.
     */
    public Double3 _kD = Double3.ZERO;
    /**
     * The specular reflection coefficient. This determines the color of highlights on the material's surface.
     */
    public Double3 _kS = Double3.ZERO;
    /**
     * The transparency coefficient. This determines how much light passes through the material.
     */
    public Double3 _kT = Double3.ZERO;
    /**
     * The reflection coefficient. This determines how much light is reflected off the material's surface.
     */
    public Double3 _kR = Double3.ZERO;
    /**
     * The shininess exponent. This controls the size and intensity of specular highlights.
     * A higher value results in smaller, sharper highlights.
     */
    public int _nShininess = 1;

    /**
     * Default constructor for creating a {@link Material} with default property values.
     */
    public Material() { /* to satisfy JavaDoc generator */
    }

    /**
     * Sets the transparency coefficient {@code kT}.
     *
     * @param kT The transparency coefficient.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKT(Double3 kT) {
        _kT = kT;
        return this;
    }

    /**
     * Sets the transparency coefficient {@code kT} to a uniform value for all color channels.
     *
     * @param kT The uniform transparency value.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKT(double kT) {
        _kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the reflection coefficient {@code kR}.
     *
     * @param kR The reflection coefficient.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKR(Double3 kR) {
        _kR = kR;
        return this;
    }

    /**
     * Sets the reflection coefficient {@code kR} to a uniform value for all color channels.
     *
     * @param kR The uniform reflection value.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKR(double kR) {
        _kR = new Double3(kR);
        return this;
    }

    /**
     * Sets the ambient reflection coefficient {@code kA}.
     *
     * @param kA The ambient reflection coefficient.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKA(Double3 kA) {
        _kA = kA;
        return this;
    }

    /**
     * Sets the ambient reflection coefficient {@code kA} to a uniform value for all color channels.
     *
     * @param kA The uniform ambient reflection value.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKA(double kA) {
        _kA = new Double3(kA);
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient {@code kD}.
     *
     * @param kD The diffuse reflection coefficient.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKD(Double3 kD) {
        _kD = kD;
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient {@code kD} to a uniform value for all color channels.
     *
     * @param kD The uniform diffuse reflection value.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKD(double kD) {
        _kD = new Double3(kD);
        return this;
    }

    /**
     * Sets the specular reflection coefficient {@code kS}.
     *
     * @param kS The specular reflection coefficient.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKS(Double3 kS) {
        _kS = kS;
        return this;
    }

    /**
     * Sets the specular reflection coefficient {@code kS} to a uniform value for all color channels.
     *
     * @param kS The uniform specular reflection value.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setKS(double kS) {
        _kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the shininess exponent {@code nShininess}.
     *
     * @param nShininess The shininess exponent.
     * @return This {@link Material} object, allowing for method chaining.
     */
    public Material setShininess(int nShininess) {
        _nShininess = nShininess;
        return this;
    }
}
