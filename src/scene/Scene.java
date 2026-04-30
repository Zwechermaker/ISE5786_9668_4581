package scene;

import geometries.impl.Geometries;
import lighting.AmbientLight;
import primitives.Color;


/**
 * passive data stucture of the scene
 */
public class Scene {
    public String name;
    public AmbientLight ambient = AmbientLight.NONE;
    public Color backGround = Color.BLACK;
    public Geometries geometries = new Geometries();

    /**
     * constructor
     * @param string the name of the scene
     */
    public Scene(String string) {
        name = string;
    }

    /**
     * a setter for the ambient light of the scene
     * @param ambient the ambient light of the scene
     * @return this
     */
    public Scene setAmbientLight(AmbientLight ambient) {
        this.ambient = ambient;
        return this;
    }

    /**
     * a setter for the background of the scene
     * @param backGround the color of the background
     * @return this
     */
    public Scene setBackground(Color backGround) {
        this.backGround = backGround;
        return this;
    }

    /**
     * a setter for geometries of the scene
     * @param geometries of the scene
     * @return this
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }

}
