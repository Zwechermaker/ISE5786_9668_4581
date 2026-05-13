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
     * the shininess value
     */
    public double _nShininess = 0;
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
        _kA = new Double3(kA);
        return this;
    }

    /**
     * setter for kd attenuation factor
     * @param kD the attenuation factor parameter
     * @return this object to allow concantenation
     */
    public Material setKd(Double3 kD){
        _kD = kD;
        if (!checkAttenuationFactors()){
            throw new IllegalArgumentException("The sum of kS and kD must be lower or equal to 1");
        }
        return this;
    }

    /**
     * setter for kd the attenuation factor
     * @param kD the attenuation factor for all colors
     * @return this object to allow concantenation
     */
    public Material setKd(double kD){
        _kD = new Double3(kD);
        if (!checkAttenuationFactors()){
            throw new IllegalArgumentException("The sum of kS and kD must be lower or equal to 1");
        }
        return this;
    }

    /**
     * setter for ks attenuation factor
     * @param kS the attenuation factor parameter
     * @return this object to allow concantenation
     */
    public Material setKs(Double3 kS){
        _kS = kS;
        if (!checkAttenuationFactors()){
            throw new IllegalArgumentException("The sum of kS and kD must be lower or equal to 1");
        }
        return this;
    }

    /**
     * setter for ks the attenuation facctor
     * @param kS the attenuation factor for all colors
     * @return this object to allow concantenation
     */
    public Material setKs(double kS){
        _kS = new Double3(kS);
        if (!checkAttenuationFactors()){
            throw new IllegalArgumentException("The sum of kS and kD must be lower or equal to 1");
        }
        return this;
    }

    /**
     * setter for shininess value
     * @param nShininess the shininess value
     * @return this object to allow concantenation
     */
    public Material setShininess(double nShininess){
        _nShininess = nShininess;
        return this;
    }

    /**
     * a function that verifies the kd and ka values.
     * @return true if valid, otherwise false.
     */
    private boolean checkAttenuationFactors(){
        Double3 sum = _kD.add(_kS);
        return (sum.isLowerThan(1) ||
                sum.equals(new Double3(1)));
    }
}
