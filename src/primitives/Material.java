package primitives;

/**
 * passive data structure that holds the values of a material.
 */
public class Material {
    /**
     * attenuation factor for emission light.
     */
    public Double3 _kA = Double3.ONE;

    public Material setKa(Double3 kA){
        _kA = kA;
        return this;
    }

    public Material setKa(double kA){
        _kA = new Double3(kA, kA, kA);
        return this;
    }
}
