package parser;

public class ParserFactory {
    private static final Parser XML_PARSER = new XmlParser();
    private static final Parser JSON_PARSER = new JsonParser();

    public static Parser getParser(ParserType format) {
        if (format == ParserType.XML) return XML_PARSER;
        if (format == ParserType.JSON) return JSON_PARSER;
        throw new IllegalArgumentException("Unknown format");
    }
}