package parser;

import scene.Scene;

/**
 * An interface for scene parsers, which are responsible for constructing a {@link Scene} object from a file.
 * <p>
 * This interface defines a standard contract for different types of parsers (e.g., XML, JSON)
 * to ensure they can be used interchangeably.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public interface Parser {
    /**
     * Parses a scene description file and populates a {@link Scene} object with the data.
     *
     * @param filePath The path to the scene file to be parsed.
     * @param scene    The {@link Scene} object to be populated.
     * @return The populated {@link Scene} object.
     * @throws RuntimeException if there is an error during the parsing process.
     */
    Scene parse(String filePath, Scene scene);
}
