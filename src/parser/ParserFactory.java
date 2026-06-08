package parser;

/**
 * A factory class for creating instances of {@link Parser}.
 * <p>
 * This class provides a centralized mechanism for obtaining the correct parser
 * based on the specified {@link ParserType}. It follows the factory design pattern.
 *
 * @author Elad Zwecher and Benjamin Godfrey
 */
public class ParserFactory {
    /**
     * Default constructor to satisfy the JavaDoc generator.
     */
    ParserFactory() { /* to satisfy JavaDoc generator */ }

    /**
     * A singleton instance of the XML parser.
     */
    private static final Parser XML_PARSER = new XmlParser();
    /**
     * A singleton instance of the JSON parser.
     */
    private static final Parser JSON_PARSER = new JsonParser();

    /**
     * Returns a parser instance corresponding to the specified format.
     *
     * @param format The {@link ParserType} of the desired parser (e.g., XML, JSON).
     * @return The appropriate {@link Parser} instance.
     * @throws IllegalArgumentException if the specified format is unknown or not supported.
     */
    public static Parser getParser(ParserType format) {
        if (format == ParserType.XML) return XML_PARSER;
        if (format == ParserType.JSON) return JSON_PARSER;
        throw new IllegalArgumentException("Unknown parser format specified.");
    }
}
