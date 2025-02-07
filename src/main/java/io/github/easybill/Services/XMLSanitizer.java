package io.github.easybill.Services;

import io.github.easybill.Exceptions.XmlSanitizationException;
import java.io.*;
import java.nio.charset.Charset;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
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

    public static @NonNull String sanitize(
        @NonNull String xml,
        @NonNull Charset charset
    ) throws XmlSanitizationException {
        try {
            return removeEmptyTags(
                removeInvalidCharsFromProlog(removeBOM(xml)),
                charset
            );
        } catch (Exception exception) {
            throw new XmlSanitizationException(exception);
        }
    }

    private static @NonNull String removeInvalidCharsFromProlog(
        @NonNull String payload
    ) {
        var indexOfXmlIntro = payload.indexOf("<?xml version");

        if (indexOfXmlIntro == 0) {
            return payload;
        }

        return payload.substring(indexOfXmlIntro);
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

    private static @NonNull String removeEmptyTags(
        @NonNull String xml,
        @NonNull Charset charset
    )
        throws ParserConfigurationException, IOException, SAXException, TransformerException {
        byte[] xmlBytes = xml.getBytes(charset);

        var builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        DocumentBuilder db = builderFactory.newDocumentBuilder();

        try (
            InputStream inputStream = new ByteArrayInputStream(xmlBytes);
            Reader reader = new InputStreamReader(inputStream, charset)
        ) {
            Document document = db.parse(new InputSource(reader));

            removeEmptyElements(document.getDocumentElement());

            TransformerFactory transformerFactory =
                TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, charset.name());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(outputStream, charset)
            ) {
                transformer.transform(
                    new DOMSource(document),
                    new StreamResult(writer)
                );
                writer.flush();

                return outputStream.toString(charset);
            }
        }
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
