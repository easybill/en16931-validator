package io.github.easybill.Services;

import io.github.easybill.Exceptions.XmlSanitizationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class XMLSanitizer {

    public static @NonNull String sanitize(@NonNull String xml)
        throws XmlSanitizationException {
        try {
            return removeEmptyTags(removeBOM(xml));
        } catch (Exception exception) {
            throw new XmlSanitizationException(exception);
        }
    }

    private static @NonNull String removeBOM(@NonNull String xml) {
        String UTF8_BOM = "\uFEFF";
        String UTF16LE_BOM = "\uFFFE";
        String UTF16BE_BOM = "\uFEFF";

        if (xml.isEmpty()) {
            return xml;
        }

        if (
            xml.startsWith(UTF8_BOM) ||
            xml.startsWith(UTF16LE_BOM) ||
            xml.startsWith(UTF16BE_BOM)
        ) {
            return xml.substring(1);
        }

        return xml;
    }

    private static @NonNull String removeEmptyTags(@NonNull String xml)
        throws ParserConfigurationException, IOException, SAXException, TransformerException {
        var builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        DocumentBuilder db = builderFactory.newDocumentBuilder();

        Document document = db.parse(new InputSource(new StringReader(xml)));

        removeEmptyElements(document.getDocumentElement());

        TransformerFactory transformerFactory =
            TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();

        StringWriter writer = new StringWriter();

        transformer.transform(
            new DOMSource(document),
            new StreamResult(writer)
        );

        return writer.toString();
    }

    private static void removeEmptyElements(Element element) {
        NodeList children = element.getChildNodes();

        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);

            if (child == null) {
                continue;
            }

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyElements((Element) child);
            }

            if (
                child.getNodeType() == Node.ELEMENT_NODE &&
                isEmptyElement((Element) child)
            ) {
                element.removeChild(child);
            }
        }
    }

    private static boolean isEmptyElement(Element element) {
        return (
            element.getChildNodes().getLength() == 0 ||
            (
                element.getChildNodes().getLength() == 1 &&
                element.getFirstChild() != null &&
                element.getFirstChild().getNodeType() == Node.TEXT_NODE &&
                element.getFirstChild().getTextContent() != null &&
                element.getFirstChild().getTextContent().trim().isEmpty()
            )
        );
    }
}
