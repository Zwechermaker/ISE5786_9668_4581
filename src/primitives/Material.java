package primitives;

/**
 * passive data structure that holds the values of a material.
 */
public final class Material {
    /**
     * attenuation factor for emission light.
     */
    /** Default constructor to satisfy JavaDoc generator */
    public Material() { /* to satisfy JavaDoc generator */ }

    /**
     * the attenuation factor
     */
    public Double3 _kA = Double3.ONE;

    /**
     * setter for ka attenuation factor
     * @param kA the attenuation factor parameter
     * @return this object to allow concantenation
     */
    public Material setKa(Double3 kA){
        _kA = kA;
        return this;
    }

    /**
     * setter for ka the attenuation facctor
     * @param kA the attenuation factor for all colors
     * @return this object to allow concantenation
     */
    public Material setKa(double kA){
        _kA = new Double3(kA, kA, kA);
        return this;
    }
}
