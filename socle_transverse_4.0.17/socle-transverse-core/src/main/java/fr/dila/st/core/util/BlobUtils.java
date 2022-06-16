package fr.dila.st.core.util;

import fr.dila.st.api.constant.MediaType;
import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.activation.DataSource;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;
import org.nuxeo.ecm.platform.mimetype.MimetypeDetectionException;

public final class BlobUtils {
    private static final STLogger LOGGER = STLogFactory.getLog(BlobUtils.class);

    private BlobUtils() {}

    public static Blob createBlob(File file) {
        Blob blob = null;
        if (file != null) {
            blob = toBlob(file);
        }
        return blob;
    }

    static Blob toBlob(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            return createSerializableBlob(in, file.getName());
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    public static Blob createSerializableBlob(InputStream in, String filename) {
        return createSerializableBlob(in, filename, null);
    }

    /**
     * Creates a serializable blob from a stream, with filename and mimetype
     * detection.
     * <p>
     * Creates a serializable FileBlob which stores data in a temporary file on the
     * hard disk.
     *
     * @param in       the input stream holding data
     * @param filename the file name. Will be set on the blob and will used for
     *                 mimetype detection.
     * @param mimeType the detected mimetype at upload. Can be null. Will be
     *                 verified by the mimetype service.
     */
    public static Blob createSerializableBlob(InputStream in, String filename, String mimeType) {
        Blob blob = null;
        try {
            // persisting the blob makes it possible to read the binary content
            // of the request stream several times (mimetype sniffing, digest
            // computation, core binary storage)
            blob = Blobs.createBlob(in, mimeType);
            // filename
            if (filename != null) {
                filename = FileUtils.sanitizePathTraversal(filename);
            }
            blob.setFilename(filename);
            // mimetype detection
            String detectedMimeType = MimeTypeUtils.detect(filename, blob, mimeType);

            blob.setMimeType(detectedMimeType);
        } catch (MimetypeDetectionException e) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e, "Could not fetch mimetype for file " + filename);
        } catch (IOException e) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e);
        }
        return blob;
    }

    /**
     * Crée un ByteArrayBlob à partir du blob en entrée
     */
    public static ByteArrayBlob toByteArrayBlob(Blob blob) throws IOException {
        ByteArrayBlob bab = new ByteArrayBlob(blob.getByteArray(), blob.getMimeType(), blob.getEncoding());
        bab.setFilename(blob.getFilename());
        bab.setDigest(blob.getDigest());
        return bab;
    }

    /**
     * Crée un ByteArrayBlob (serializable) à partir d'une datasource (non serializable)
     */
    public static ByteArrayBlob toByteArrayBlob(DataSource datasource, String filename) throws IOException {
        ByteArrayBlob bab = new ByteArrayBlob(
            STIOUtils.toByteArray(datasource.getInputStream()),
            datasource.getContentType()
        );
        bab.setFilename(filename);
        return bab;
    }

    /**
     * Crée un ByteArrayBlob à partir d'une liste de fichiers (sera zippée) et d'un nom de fichier
     */
    public static ByteArrayBlob toZipByteArrayBlob(List<ExportBlob> listFiles, String fileName) throws IOException {
        byte[] bytes = ZipUtil.generateZip(listFiles);

        ByteArrayBlob bab = new ByteArrayBlob(bytes, MediaType.APPLICATION_ZIP.mime());
        bab.setFilename(fileName);

        return bab;
    }
}
