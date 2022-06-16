package fr.dila.st.ui.helper;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Classe permettant de récupérer le fichier Json correspondant au DTO
 *
 * @author akeo
 *
 */
public class DtoJsonHelper {
    private static final String RESOURCES_JSON_PATH = "/json";

    public static <T> T getObjectFromJson(HttpServletRequest httpServletRequest, String jsonFilename, Class<T> clazz) {
        try {
            String resourcesPath = httpServletRequest.getServletContext().getRealPath(RESOURCES_JSON_PATH);
            String filepath = resourcesPath + jsonFilename;
            return new ObjectMapper().readValue(new File(filepath), clazz);
        } catch (Exception e) {
            throw new NuxeoException(
                "Erreur lors de la création du DTO de test à partir du fichier json : " +
                jsonFilename +
                "\r\n" +
                e.getMessage()
            );
        }
    }
}
