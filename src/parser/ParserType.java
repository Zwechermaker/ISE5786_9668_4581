package parser;

/**
 * Enumerates the available types of scene parsers.
 * <p>
 * This enum is used by {@link ParserFactory} to create the appropriate parser
 * based on the desired file format.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public enum ParserType {
    /**
     * Represents a parser for XML files.
     */
    XML,

    /**
     * Represents a parser for JSON files.
     */
    JSON
}
