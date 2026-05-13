package scene;

import geometries.impl.Geometries;
import lighting.api.LightSource;
import lighting.impl.AmbientLight;
import primitives.Color;

import java.util.ArrayList;
import java.util.List;


/**
 * passive data structure of the scene
 */
public final class Scene {
    /** the name of the scene. */
    public String name;

    /**
     * the ambient light of the scene, default is no ambient light.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * the background color of the scene, default is black.
     */
    public Color background = Color.BLACK;

    /**
     * the collection of 3D geometries present in the scene.
    */
    public Geometries geometries = new Geometries();

    /**
     * a list of all light sources in the scene.
     */
    public List<LightSource> lights;
    /**
     * constructor
     * @param string the name of the scene
     */
    public Scene(String string) {
        name = string;
        lights = new ArrayList<>();
    }

    /**
     * a setter for the ambient light of the scene
     * @param ambient the ambient light of the scene
     * @return this
     */
    public Scene setAmbientLight(AmbientLight ambient) {
        this.ambientLight = ambient;
        return this;
    }

    /**
     * a setter for the background of the scene
     * @param backGround the color of the background
     * @return this
     */
    public Scene setBackground(Color backGround) {
        this.background = backGround;
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

    @Override
    public String toString() {
        return "Scene '" + name + "' {\n" +
               "  background: " + background + ",\n" +
               "  ambientLight: " + ambientLight + ",\n" +
               "  " + geometries.toString().replace("\n", "\n  ") + "\n" +
               "}";
    }
}
