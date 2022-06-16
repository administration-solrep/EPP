package fr.dila.st.core.util;

import static fr.dila.st.api.constant.MediaType.APPLICATION_MS_EXCEL;
import static fr.dila.st.api.constant.MediaType.APPLICATION_OPENXML_WORD;
import static fr.dila.st.api.constant.MediaType.APPLICATION_PDF;
import static fr.dila.st.api.constant.MediaType.APPLICATION_ZIP;
import static fr.dila.st.api.constant.MediaType.TEXT_CSV;
import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.FileUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

public final class FileUtils {
    public static final String DEFAULT_APP_FOLDER_TMP = "/tmp";

    private static final int FILENAME_MAX_SIZE = 50;
    private static final int FILENAME_FIRST_CHARS_TO_KEEP = 25;
    private static final Pattern ILLEGAL_FILENAME_CHARACTERS_PATTERN = Pattern.compile("[:\\\\/*'\"?|<>]");

    private FileUtils() {}

    /**
     * Returns a clean filename, stripping upload path on client side.
     * For instance, it turns "/tmp/2349876398/foo.pdf" into "foo.pdf"
     * <p>
     * Fixes NXP-544
     *
     * @param filename the filename
     * @return the stripped filename
     */
    public static String sanitizePathTraversal(String filename) {
        Path p = Paths.get(filename.replace("..", ""));
        return sanitizeFilename(p.getFileName().toString());
    }

    /**
     * Reconstitue et renvoie le chemin complet vers le fichier temporaire à partir de son nom seul.
     *
     * @param fileName
     * @return
     */
    public static String getAppTmpFilePath(String fileName) {
        return getAppTmpFile(fileName).getAbsolutePath();
    }

    public static File getAppTmpFile(String fileName) {
        return new File(getAppTmpFolder(), fileName);
    }

    /**
     * Renvoie le répertoire où sont stockés les fichiers temporaires.
     *
     * @return
     */
    public static File getAppTmpFolder() {
        ConfigService configService = STServiceLocator.getConfigService();
        File appTmpFile = new File(configService.getValue(STConfigConstants.APP_FOLDER_TMP, DEFAULT_APP_FOLDER_TMP));
        if (!appTmpFile.exists()) {
            Path directoryPath = Paths.get(appTmpFile.getAbsolutePath());
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new NuxeoException(e);
            }
        }

        return appTmpFile;
    }

    /**
     * Renvoie le nom de fichier sans chemin et sans extension.
     *
     * @param path path ou filename seulement
     * @return un String
     */
    public static String trimExtension(String path) {
        // Suppression du chemin, on ne garde que le filename
        String filename = FileUtil.getFilenameByPath(path);
        int index = filename.lastIndexOf('.');

        if (index < 0) {
            return filename;
        }

        return filename.substring(0, index);
    }

    /**
     * Réduit les noms de fichiers trop longs (max 50 caractères sans compter l'extension). Les caractères supprimés
     * sont remplacés par des ...
     * <br>
     * Ex : nomDeFichierSuperLongPourUnAffichageGraphiqueCorrect.doc
     * ---> nomDeFichierSuperLongPour...ichageGraphiqueCorrect.doc
     *
     * @param filename nom du fichier à réduire
     * @return nom du fichier réduit si initialement supérieur à 50 caractères, sinon il est renvoyé à l'identique
     */
    public static String getShorterName(String filename) {
        String filenameWithoutExtension = FilenameUtils.removeExtension(filename);
        if (filenameWithoutExtension.length() > FILENAME_MAX_SIZE) {
            String debut = filenameWithoutExtension.substring(0, FILENAME_FIRST_CHARS_TO_KEEP);
            String milieu = "...";
            String fin = filenameWithoutExtension.substring(
                filenameWithoutExtension.length() - FILENAME_MAX_SIZE + FILENAME_FIRST_CHARS_TO_KEEP + milieu.length()
            );
            return debut + milieu + fin + getExtensionWithSeparator(filename);
        }
        return filename;
    }

    public static String getExtensionWithSeparator(String filename) {
        String result = "";
        String extension = FilenameUtils.getExtension(filename);
        if (StringUtils.isNotBlank(extension)) {
            result = EXTENSION_SEPARATOR + extension;
        }
        return result;
    }

    public static boolean isCsvFile(String filename) {
        return isExtension(filename, TEXT_CSV.extension());
    }

    public static boolean isPdfFile(String filename) {
        return isExtension(filename, APPLICATION_PDF.extension());
    }

    public static boolean isZipFile(String filename) {
        return isExtension(filename, APPLICATION_ZIP.extension());
    }

    private static boolean isExtension(String filename, String extension) {
        return FilenameUtils.isExtension(filename, extension);
    }

    /**
     * Renvoie true si le mimetype détecté à partir des bytes d'un stream est
     * identique au mimetype détecté du filename donné en paramètre.
     *
     * @param bytes tableau de bytes du stream à tester. En utilisant new
     *              ByteArrayInputStream(bytes), on peut initialiser un InputStream
     *              réutilisable (non consommé)
     */
    public static boolean equalsMimetype(byte[] bytes, String filename) {
        return equalsMimetype(new ByteArrayInputStream(bytes), filename);
    }

    public static boolean equalsMimetype(InputStream inputStream, String filename) {
        String streamMimeType = MimeTypeUtils.tikaDetect(inputStream);

        String fileMimeType = MimeTypeUtils.detect(filename);

        return streamMimeType.equals(fileMimeType);
    }

    public static String generateCompletePdfFilename(String filename) {
        return generateCompleteFilename(filename, APPLICATION_PDF.extension());
    }

    public static String generateCompleteDocxFilename(String filename) {
        return generateCompleteFilename(filename, APPLICATION_OPENXML_WORD.extension());
    }

    public static String generateCompleteCsvFilename(String filename) {
        return generateCompleteFilename(filename, TEXT_CSV.extension());
    }

    public static String generateCompleteXlsFilename(String filename) {
        return generateCompleteFilename(filename, APPLICATION_MS_EXCEL.extension());
    }

    /**
     * Pour info : ce n'est pas possible d'utiliser un Optional avec les instructions filter et map pour faire le code
     * de cette méthode sinon PowerMock n'arrive pas à mocker cette classe utilitaire finale dans certains tests
     * unitaires.
     */
    private static String generateCompleteFilename(String filename, String extension) {
        String completeFilename = filename;

        if (StringUtils.isNotBlank(filename) && !FilenameUtils.isExtension(filename.toLowerCase(), extension)) {
            completeFilename = sanitizePathTraversal(filename) + EXTENSION_SEPARATOR + extension;
        }

        return completeFilename;
    }

    private static String sanitizeFilename(String filename) {
        return ILLEGAL_FILENAME_CHARACTERS_PATTERN.matcher(filename).replaceAll("_");
    }

    public static Boolean isValidFilename(String filename) {
        return !ILLEGAL_FILENAME_CHARACTERS_PATTERN.matcher(filename).find();
    }
}
