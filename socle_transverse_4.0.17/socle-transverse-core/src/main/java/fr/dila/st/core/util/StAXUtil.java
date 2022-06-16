package fr.dila.st.core.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.Consumer;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Classe utilitaire pour la génération de fichiers XML en utilisant StAX
 * (Streaming Api for XML)
 */
public final class StAXUtil {

    private StAXUtil() {
        // Do nothing
    }

    private static XMLStreamWriter initXMLStreamWriter(StringWriter stringWriter)
        throws FactoryConfigurationError, XMLStreamException {
        XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
        return xMLOutputFactory.createXMLStreamWriter(stringWriter);
    }

    private static void writeStartDocument(XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
        xMLStreamWriter.writeStartDocument("UTF-8", "1.0");
    }

    private static void writeEndDocument(XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
        xMLStreamWriter.writeEndDocument();
        xMLStreamWriter.flush();
        xMLStreamWriter.close();
    }

    /**
     * Ajout de l'attribut xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" sur
     * l'élément courant (si requis)
     */
    public static void writeAttributeXmlnsXsi(XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
        xMLStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    }

    private static String xmlToString(StringWriter stringWriter) {
        return stringWriter.getBuffer().toString();
    }

    /**
     * Ajout d'un élément XML simple de la forme <tagName>content</tagName>
     */
    public static void appendTextNode(XMLStreamWriter xMLStreamWriter, String tagName, String content)
        throws XMLStreamException {
        xMLStreamWriter.writeStartElement(tagName);
        xMLStreamWriter.writeCharacters(content);
        xMLStreamWriter.writeEndElement();
    }

    /**
     * Méthode à utiliser pour créer le XML et retourner son contenu, les phases
     * préparatoires et finales sont incluses. Le client n'a plus qu'à manipuler le
     * XMLStreamWriter dans le Consumer qu'il écrira.
     */
    public static String buildXml(Consumer<XMLStreamWriter> xmlBuilder) throws IOException, XMLStreamException {
        try (StringWriter stringWriter = new StringWriter();) {
            XMLStreamWriter xMLStreamWriter = initXMLStreamWriter(stringWriter);
            writeStartDocument(xMLStreamWriter);

            xmlBuilder.accept(xMLStreamWriter);

            writeEndDocument(xMLStreamWriter);
            return xmlToString(stringWriter);
        }
    }
}
