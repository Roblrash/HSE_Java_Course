package serialization;

public class SerializerFactory {
    public static ArraySerializer forFormat(String filename) {
        if (filename.endsWith(".csv")) {
            return new CsvArraySerializer();
        }
        if (filename.endsWith(".json")) {
            return new JsonArraySerializer();
        }
        if (filename.endsWith(".xml")) {
            return new XmlArraySerializer();
        }
        if (filename.endsWith(".bin")) {
            return new BinaryArraySerializer();
        }
        throw new IllegalArgumentException("Unknown file format");
    }
}
