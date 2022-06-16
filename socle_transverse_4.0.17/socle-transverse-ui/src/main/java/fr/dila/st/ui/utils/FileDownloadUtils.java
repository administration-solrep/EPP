package fr.dila.st.ui.utils;

import fr.dila.st.api.constant.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Classe utilitaire pour tout ce qui est lié au téléchargement de fichiers depuis le front.
 *
 * @author tlombard
 */
public final class FileDownloadUtils {
    public static final String HDR_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_CONTENT_DISPOSITION = "attachment; filename=\"%s\"";
    private static final String INLINE_CONTENT_DISPOSITION = "inline;";
    private static final String HEADER_CONTENT_TYPE_ZIP = MediaType.APPLICATION_ZIP.mime();

    /**
     * Caractères accentués autorisés dans un nom de fichier
     */
    private static final String FILE_ACCENTED_CHARACTERS =
        "àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇßØøÅåÆæœ";

    /**
     * Regex définissant le pattern pour valider un nom de fichier.
     */
    private static final Pattern FILENAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9" + FILE_ACCENTED_CHARACTERS + "._ -]+$"
    );

    private FileDownloadUtils() {
        // Classe non instanciable
    }

    /**
     * Permet de vérifier si un nom de fichier passé en paramètre pour récupérer un fichier est sécurisé : on interdit
     * le parcours de l'arborescence.
     *
     * @param filename nom du fichier (pas de chemin complet, avec ou sans extension)
     * @return true ssi le nom de fichier est considéré sécurisé.
     */
    public static boolean isSecuredFilename(String filename) {
        return FILENAME_PATTERN.matcher(filename).matches();
    }

    /**
     * Construit et renvoie l'objet Response destiné à permettre le téléchargement du fichier indiqué en paramètre. Le
     * fichier doit être directement sous le répertoire indiqué. Aucun changement de répertoire n'est autorisé.
     *
     * @param folder   répertoire dans lequel se trouve le fichier
     * @param filename le nom du fichier
     * @return Response ok/forbidden si l'accès est interdit par les règles mises en place.
     */
    public static Response getResponse(File folder, String filename) {
        if (folder.exists() && folder.isDirectory() && isSecuredFilename(filename)) {
            File file = new File(folder, filename);
            Tika tika = new Tika();
            try {
                String mimeType = tika.detect(file);
                return getResponse(file, filename, mimeType);
            } catch (IOException e) {
                throw new NuxeoException(
                    "Impossible de déterminer le mimetype du fichier : " + filename,
                    e,
                    Response.Status.NOT_FOUND.getStatusCode()
                );
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Construit et renvoie l'objet Response destiné à permettre le téléchargement du fichier issu du repository Nuxeo
     *
     * @param file     l'objet File reconstitué depuis le repository
     * @param filename le nom à donner au fichier à télécharger
     * @param mimetype le mimetype du fichier.
     * @return Response ok/forbidden si le fichier ou le mimetype est nul ainsi que si le filename est suspect.
     */
    public static Response getResponse(File file, String filename, String mimetype) {
        return getResponseWithContentDisposition(
            file,
            mimetype,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, filename)
        );
    }

    public static Response getInlinePdf(File file) {
        return getResponseWithContentDisposition(file, MediaType.APPLICATION_PDF.mime(), INLINE_CONTENT_DISPOSITION);
    }

    public static Response getAttachmentPdf(File file, String filename) {
        return getResponseWithContentDisposition(
            file,
            MediaType.APPLICATION_PDF,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, filename)
        );
    }

    public static Response getAttachmentHtml(File file, String filename) {
        return getResponseWithContentDisposition(
            file,
            MediaType.TEXT_HTML,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, filename)
        );
    }

    public static Response getAttachmentXls(File file, String filename) {
        return getResponseWithContentDisposition(
            file,
            MediaType.APPLICATION_MS_EXCEL,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, filename)
        );
    }

    public static Response getAttachmentXls(StreamingOutput stream, String filename) {
        return getResponseWithContentDisposition(
            stream,
            MediaType.APPLICATION_MS_EXCEL,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, filename)
        );
    }

    public static Response getAttachmentCsv(File file) {
        return getResponseWithContentDisposition(
            file,
            MediaType.TEXT_CSV,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, file.getName())
        );
    }

    public static Response getAttachmentDoc(File file, String filename) {
        return getResponseWithContentDisposition(
            file,
            MediaType.APPLICATION_MS_WORD,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, filename)
        );
    }

    public static Response getAttachmentDocx(File file, String filename) {
        return getResponseWithContentDisposition(
            file,
            MediaType.APPLICATION_OPENXML_WORD,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, filename)
        );
    }

    private static Response getResponseWithContentDisposition(
        Object entity,
        MediaType mimetype,
        String contentDisposition
    ) {
        return getResponseWithContentDisposition(entity, mimetype.mime(), contentDisposition);
    }

    private static Response getResponseWithContentDisposition(
        Object entity,
        String mimetype,
        String contentDisposition
    ) {
        if (entity != null && StringUtils.isNotBlank(mimetype)) {
            return Response.ok(entity, mimetype).header(HDR_CONTENT_DISPOSITION, contentDisposition).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public static Response getZipResponse(Object obj, String filename) {
        return Response
            .ok(obj)
            .type(HEADER_CONTENT_TYPE_ZIP)
            .header(HDR_CONTENT_DISPOSITION, String.format(ATTACHMENT_CONTENT_DISPOSITION, filename))
            .build();
    }

    public static Response getAttachmentFromStream(StreamingOutput stream, String filename) {
        return getResponseWithContentDisposition(
            stream,
            MediaType.APPLICATION_OCTET_STREAM,
            String.format(ATTACHMENT_CONTENT_DISPOSITION, filename)
        );
    }
}
