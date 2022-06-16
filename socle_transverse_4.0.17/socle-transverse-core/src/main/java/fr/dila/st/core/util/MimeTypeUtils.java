package fr.dila.st.core.util;

import static fr.dila.st.api.constant.MediaType.APPLICATION_OCTET_STREAM;
import static fr.dila.st.core.util.ObjectHelper.requireNonNullElse;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.POIFSContainerDetector;
import org.apache.tika.parser.pkg.ZipContainerDetector;
import org.apache.tika.sax.BodyContentHandler;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeRegistry;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public final class MimeTypeUtils {
    private static final String MIME_OCTET_STREAM = APPLICATION_OCTET_STREAM.mime();

    private static final STLogger LOGGER = STLogFactory.getLog(MimeTypeUtils.class);

    private static final String APPLICATION_X_TIKA_MSOFFICE = "application/x-tika-msoffice";

    private MimeTypeUtils() {}

    private static Detector detector = null;

    private static Detector getTikaDectector() {
        if (detector == null) {
            List<Detector> detectors = new ArrayList<>();

            // zip compressed container types
            detectors.add(new ZipContainerDetector());
            // Microsoft related files
            detectors.add(new POIFSContainerDetector());
            // mime magic detection as fallback
            detectors.add(MimeTypes.getDefaultMimeTypes());

            detector = new CompositeDetector(detectors);
        }
        return detector;
    }

    public static String detect(String filename) {
        MimetypeRegistry mimeService = ServiceUtil.getRequiredService(MimetypeRegistry.class);
        String detectedMimeType = mimeService.getMimetypeFromFilename(filename);
        return ObjectHelper.requireNonNullElse(detectedMimeType, MIME_OCTET_STREAM);
    }

    public static String detect(String filename, Blob blob, String mimeType) {
        MimetypeRegistry mimeService = ServiceUtil.getRequiredService(MimetypeRegistry.class);
        String detectedMimeType = mimeService.getMimetypeFromFilenameAndBlobWithDefault(filename, blob, null);

        return requireNonNullElse(detectedMimeType, requireNonNullElse(mimeType, MIME_OCTET_STREAM));
    }

    public static String tikaDetect(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return tikaDetect(fis);
        } catch (IOException e) {
            LOGGER.error(STLogEnumImpl.FAIL_GET_FILE_TEC, e);
            return MIME_OCTET_STREAM;
        }
    }

    public static String tikaDetect(InputStream inputStream) {
        if (inputStream.markSupported()) {
            inputStream.mark(0);
        }
        MediaType mediatype = null;

        try (TikaInputStream tis = TikaInputStream.get(inputStream)) {
            Detector detector = getTikaDectector();
            Metadata metadata = new Metadata();
            mediatype = detector.detect(tis, metadata);
            if (mediatype == null || mediatype.toString().equals(APPLICATION_X_TIKA_MSOFFICE)) {
                return tikaParse(tis);
            }
        } catch (IOException | SAXException | TikaException e) {
            LOGGER.error(STLogEnumImpl.FAIL_GET_FILE_TEC, e);
            return MIME_OCTET_STREAM;
        } finally {
            if (inputStream.markSupported()) {
                try {
                    inputStream.reset();
                } catch (IOException e) {
                    LOGGER.warn(STLogEnumImpl.FAIL_GET_FILE_TEC, e);
                }
            }
        }
        return mediatype == null ? MIME_OCTET_STREAM : mediatype.toString();
    }

    private static String tikaParse(TikaInputStream inputStream) throws IOException, SAXException, TikaException {
        Parser parser = new AutoDetectParser();
        ContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        parser.parse(inputStream, handler, metadata, context);
        inputStream.reset();

        return metadata.get("Content-type");
    }
}
