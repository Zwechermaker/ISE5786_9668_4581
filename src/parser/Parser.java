package parser;

import scene.Scene;

/**
 * interface that defines a parser class
 */
public interface Parser {
    /**
     * the parser function that makes the scene from the file
     * @param fileName the file name
     * @param scene the scene to build
     * @return the constructed scene
     */
    Scene Parse(String fileName, Scene scene);
}