package fr.dila.st.core.util;

import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Utilitaires sur les fichiers compressés.
 *
 * @author jtremeaux
 */
public final class ZipUtil {
    private static final STLogger LOGGER = STLogFactory.getLog(ZipUtil.class);

    private static final int COOKIE_MAX_NB_HOURS = 24;

    /**
     * Utility class
     */
    private ZipUtil() {
        // do nothing
    }

    /**
     * Décompresse un fichier unique de l'archive. Si l'archive contient plusieurs fichier, lève une exception.
     * L'archive peut contenir des répertoires, dans ce cas le premier fichier rencontré n'importe où dans
     * l'arborescence est celui pris en compte.
     *
     * @param inputStream
     *            Flux entrant de l'archive Zip
     * @return Flux du fichier dézippé
     * @throws ClientException
     */
    public static ByteArrayOutputStream unzipSingleFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = null;
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    if (outputStream != null) {
                        throw new NuxeoException("L'archive ZIP doit contenir un seul fichier");
                    }
                    checkZipEntryName(zipEntry);
                    outputStream = new ByteArrayOutputStream();
                    IOUtils.copy(zipInputStream, outputStream);
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        } catch (IOException e) {
            throw new NuxeoException("Erreur lors du traitement de l'archive ZIP", e);
        }
        return outputStream;
    }

    /**
     * Charge le blob correspondant à un fichier zip dans le HttpServletResponse pour permettre le téléchargement
     *
     * @param response
     * @param zipBlob
     */
    public static void downloadZip(HttpServletResponse response, Blob zipBlob) {
        response.reset();

        response.setContentType("application/zip; charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + zipBlob.getFilename() + "\"");
        addCookieToResponse(response, "fileDownload", "true", "/");
        response.setCharacterEncoding("UTF-8");

        try (
            OutputStream responseOutputStream = response.getOutputStream();
            InputStream zipInputStream = zipBlob.getStream()
        ) {
            byte[] bytesBuffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = zipInputStream.read(bytesBuffer)) > 0) {
                responseOutputStream.write(bytesBuffer, 0, bytesRead);
            }

            responseOutputStream.flush();
        } catch (IOException ioExc) {
            LOGGER.error(null, STLogEnumImpl.FAIL_GET_FILE_TEC, ioExc);
        }
    }

    private static void addCookieToResponse(HttpServletResponse response, String name, String value, String path) {
        Cookie appCookie = new Cookie(name, value);
        appCookie.setMaxAge((int) TimeUnit.HOURS.toSeconds(COOKIE_MAX_NB_HOURS));
        appCookie.setSecure(true);
        appCookie.setHttpOnly(true);
        if (path != null) {
            appCookie.setPath(path);
        }
        response.addCookie(appCookie);
    }

    /**
     * Ajoute les fichiers contenus dans la List à l'archive zip
     *
     * @param zipFile
     *            le fichier d'archive
     * @param filesToAdd
     *            les fichiers à ajouter à l'archive
     * @param encoding
     *            l'encodage utilisé pour l'archive
     */
    public static void zipFiles(File zipFile, List<File> filesToAdd, String encoding) {
        try (
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(new BufferedOutputStream(fos))
        ) {
            zaos.setEncoding(encoding);
            zaos.setFallbackToUTF8(true);
            zaos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.NOT_ENCODEABLE);
            zaos.setLevel(Deflater.BEST_COMPRESSION);

            // Get to putting all the files in the compressed output file
            for (File f : filesToAdd) {
                addFile(zaos, f, "");
            }
        } catch (IOException exc) {
            LOGGER.error(STLogEnumImpl.FAIL_ZIP_FILE_TEC, exc);
        }
    }

    /**
     * Ajoute un fichier à une archive zip
     *
     * @param outputStream
     * @param fileToAdd
     * @throws IOException
     */
    public static void addFile(ArchiveOutputStream outputStream, File fileToAdd, String parentPath) throws IOException {
        if (fileToAdd.isFile()) {
            outputStream.putArchiveEntry(new ZipArchiveEntry(fileToAdd, fileToAdd.getName()));

            // Add the file to the archive
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileToAdd))) {
                IOUtils.copy(bis, outputStream);
                outputStream.closeArchiveEntry();
            }
        } else if (fileToAdd.isDirectory()) {
            // go through all the files in the directory and using recursion, add them to the archive
            for (File childFile : fileToAdd.listFiles()) {
                addFile(outputStream, childFile, parentPath + fileToAdd.getName());
            }
        }
    }

    public static void writeBlobToZipStream(ZipOutputStream out, String path, Blob blob) {
        // nettoyage du chemin
        path = org.nuxeo.common.utils.StringUtils.toAscii(path);
        ZipEntry entry = null;
        // write blobs
        try {
            entry = new ZipEntry(path);
            out.putNextEntry(entry);
            try (InputStream inputStream = blob.getStream()) {
                IOUtils.copy(inputStream, out);
            }
        } catch (IOException e) {
            LOGGER.error(STLogEnumImpl.FAIL_ZIP_FILE_TEC, e);
        }
    }

    public static byte[] generateZip(List<ExportBlob> listFiles) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);

            // Attach the files to the message
            listFiles.forEach(
                exportBlob -> {
                    Blob blob = exportBlob.getBlob();
                    if (blob != null) {
                        ZipUtil.writeBlobToZipStream(
                            zipOutputStream,
                            exportBlob.getPath() + "/" + blob.getFilename(),
                            blob
                        );
                    }
                }
            );
        }

        return outputStream.toByteArray();
    }

    /**
     * Génère le fichier zip correspondant au Consumer en paramètre et renvoie ses bytes.
     */
    public static byte[] generateZipBytes(Consumer<ZipOutputStream> zosConsumer) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writeZipToOutputStream(zosConsumer, outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Génère un fichier zip correspondant au Consumer en paramètre et l'écrit dans le OutputStream passé en paramètre.
     */
    public static void writeZipToOutputStream(Consumer<ZipOutputStream> zosConsumer, final OutputStream outputStream)
        throws IOException {
        try (final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);

            zosConsumer.accept(zipOutputStream);
        }
    }

    public static ZipEntry checkZipEntryName(ZipEntry zipEntry) {
        String entryName = zipEntry.getName();

        // ABI : audit de sécurité 2017. Vérification pour attaque via path traversal
        if (entryName.contains("..") || entryName.contains("/") || entryName.contains("\\")) {
            throw new IllegalArgumentException(
                String.format("Sécurité : le fichier %s du zip possède un nom invalide ( '/','..','\\')", entryName)
            );
        }

        return zipEntry;
    }

    public static byte[] getZipEntryContent(ZipFile zipFile, ZipEntry zipEntry) {
        byte[] content;
        try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
            content = IOUtils.toByteArray(inputStream);
        } catch (IOException exception) {
            throw new NuxeoException(
                String.format(
                    "Erreur lors de la récupération du contenu du fichier %s de l'archive ZIP %s",
                    zipEntry.getName(),
                    zipFile.getName()
                ),
                exception
            );
        }
        return content;
    }
}
