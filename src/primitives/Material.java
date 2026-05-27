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
     * the attenuation factor for ambient light
     */
    public Double3 _kA = Double3.ONE;
    /**
     * the attenuation factor for diffuse light
     */
    public Double3 _kD = Double3.ZERO;
    /**
     * the attenuation factor for specular light
     */
    public Double3 _kS = Double3.ZERO;
    /**
     * the attenuation factor for the transparency light
     */
    public Double3 _kT = Double3.ZERO;
    /**
     * the attenuation factor for the reflection light
     */
    public Double3 _kR = Double3.ZERO;
    /**
     * the shininess value
     */
    public int _nShininess = 0;

    /**
     * a setter for the attenuation factor of the transparency light
     * @param kT the attenuation factor parameter
     * @return the object for concantenation
     */
    public Material setKT(Double3 kT) {
        _kT = kT;
        return this;
    }

    /**
     * a setter for the attenuation factor of the transparency light
     * @param kT the attenuation factor parameter
     * @return the object for concantenation
     */
    public Material setKT(double kT) {
        _kT = new Double3(kT);
        return this;
    }

    /**
     * a setter for the attenuation factor of the reflection light
     * @param kR the attenuation factor parameter
     * @return the object for concantenation
     */
    public Material setKR(Double3 kR) {
        _kR = kR;
        return this;
    }

    /**
     * a setter for the attenuation factor of the reflection light
     * @param kR the attenuation factor parameter
     * @return the object for concantenation
     */
    public Material setKR(double kR) {
        _kR = new Double3(kR);
        return this;
    }
    /**
     * setter for ka attenuation factor
     * @param kA the attenuation factor parameter
     * @return this object to allow concantenation
     */
    public Material setKA(Double3 kA){
        _kA = kA;
        return this;
    }

    /**
     * setter for ka the attenuation factor
     * @param kA the attenuation factor for all colors
     * @return this object to allow concantenation
     */
    public Material setKA(double kA){
        _kA = new Double3(kA);
        return this;
    }

    /**
     * setter for kd attenuation factor
     * @param kD the attenuation factor parameter
     * @return this object to allow concantenation
     */
    public Material setKD(Double3 kD){
        _kD = kD;
        return this;
    }

    /**
     * setter for kd the attenuation factor
     * @param kD the attenuation factor for all colors
     * @return this object to allow concantenation
     */
    public Material setKD(double kD){
        _kD = new Double3(kD);
        return this;
    }

    /**
     * setter for ks attenuation factor
     * @param kS the attenuation factor parameter
     * @return this object to allow concantenation
     */
    public Material setKS(Double3 kS){
        _kS = kS;
        return this;
    }

    /**
     * setter for ks the attenuation facctor
     * @param kS the attenuation factor for all colors
     * @return this object to allow concantenation
     */
    public Material setKS(double kS){
        _kS = new Double3(kS);
        return this;
    }

    /**
     * setter for shininess value
     * @param nShininess the shininess value
     * @return this object to allow concantenation
     */
    public Material setShininess(int nShininess){
        _nShininess = nShininess;
        return this;
    }


}
