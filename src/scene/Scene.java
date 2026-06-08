package scene;

import geometries.impl.Geometries;
import lighting.api.LightSource;
import lighting.impl.AmbientLight;
import primitives.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a 3D scene, which contains all the elements to be rendered.
 * <p>
 * This class is a passive data structure that holds the scene's name, background color,
 * ambient light, geometries, and light sources. It uses a fluent API for configuration.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public final class Scene {
    /**
     * The name of the scene.
     */
    public final String name;

    /**
     * The ambient light of the scene. Defaults to no ambient light (black).
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * The background color of the scene. Defaults to black.
     */
    public Color background = Color.BLACK;

    /**
     * A collection of all the 3D geometries present in the scene.
     */
    public Geometries geometries = new Geometries();

    /**
     * A list of all the light sources in the scene.
     */
    public List<LightSource> lights;

    /**
     * Constructs a {@link Scene} with a given name.
     *
     * @param name The name of the scene.
     */
    public Scene(String name) {
        this.name = name;
        this.lights = new ArrayList<>();
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambient The {@link AmbientLight} to set.
     * @return This {@link Scene} object, allowing for method chaining.
     */
    public Scene setAmbientLight(AmbientLight ambient) {
        this.ambientLight = ambient;
        return this;
    }

    /**
     * Sets the background color of the scene.
     *
     * @param background The background {@link Color} to set.
     * @return This {@link Scene} object, allowing for method chaining.
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the collection of geometries for the scene.
     *
     * @param geometries The {@link Geometries} collection to set.
     * @return This {@link Scene} object, allowing for method chaining.
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
