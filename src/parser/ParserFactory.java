package parser;

/**
 * a class responsible for creating parsers
 */
public class ParserFactory {
    /** Default constructor to satisfy JavaDoc generator */
    ParserFactory() { /* to satisfy JavaDoc generator */ }

    /**
     * an instance of an XML parser
     */
    private static final Parser XML_PARSER = new XmlParser();
    /**
     * an instance of JSON parser
     */
    private static final Parser JSON_PARSER = new JsonParser();

    /**
     * the function that gets the parser based on the format
     * @param format type of parser requested
     * @return the parser
     */
    public static Parser getParser(ParserType format) {
        if (format == ParserType.XML) return XML_PARSER;
        if (format == ParserType.JSON) return JSON_PARSER;
        throw new IllegalArgumentException("Unknown format");
    }
}