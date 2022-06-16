package fr.dila.st.ui.jaxrs.exception;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.ThEngineService;
import fr.dila.st.ui.th.impl.ThEngineServiceImpl;
import fr.dila.st.ui.th.model.DisconnectedLayoutThTemplate;
import fr.dila.st.ui.th.model.LayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.nuxeo.ecm.webengine.WebEngine;

public abstract class AbstractExceptionMapper {
    /**
     * Statut HTTP par défaut des erreurs.
     */
    protected static final Status DEFAULT_STATUS = Status.INTERNAL_SERVER_ERROR;
    private static final String THYMELEAF_PATH = "th-templates/";
    private static final String DEFAULT_ERROR_PATH = "error/error";
    private static final String ERROR_EXTENSION_FILE = ".html";

    protected ThEngineService thEngine = new ThEngineServiceImpl();

    @Context
    protected final HttpServletRequest request;

    protected AbstractExceptionMapper(@Context HttpServletRequest request) {
        this(request, new ThEngineServiceImpl());
    }

    protected AbstractExceptionMapper(@Context HttpServletRequest request, ThEngineService thEngine) {
        this.request = request;
        this.thEngine = ObjectHelper.requireNonNullElseGet(thEngine, ThEngineServiceImpl::new);
    }

    protected static void logException(Exception exception, STLogger logger) {
        if (WebEngine.getActiveContext() != null && WebEngine.getActiveContext().getPrincipal() != null) {
            // Si l'utilisateur n'est pas connecté on ne peut pas récupérer la coresession
            logger.error(WebEngine.getActiveContext().getCoreSession(), STLogEnumImpl.LOG_EXCEPTION_TEC, exception);
        } else {
            logger.error(STLogEnumImpl.LOG_EXCEPTION_TEC, exception);
        }
    }

    protected Response buildResponse(Exception exception, int status, Response response) {
        return buildResponse(exception.getMessage(), status, response);
    }

    protected Response buildResponse(String message, int status, Response response) {
        if (URLUtils.isAjaxRequest(request)) {
            return buildResponseAjax(message, status);
        } else {
            return buildResponseHtml(response, status);
        }
    }

    private Response buildResponseAjax(String message, int status) {
        return Response.status(status).type(TEXT_PLAIN).entity(message).build();
    }

    private Response buildResponseHtml(Response response, int status) {
        ResponseBuilder builder;
        if (response != null) {
            builder = Response.fromResponse(response);
        } else {
            builder = Response.status(status);
        }

        String statusStr = String.valueOf(status);
        String errorFilePath = DEFAULT_ERROR_PATH + statusStr;

        // On vérifie s'il existe un fichier d'erreur pour ce code précis (ex: erreur 404 => on vérifie si error/error404.html existe)
        String path = request.getServletContext().getRealPath(THYMELEAF_PATH + errorFilePath + ERROR_EXTENSION_FILE);
        File file = new File(path);
        if (!file.exists()) {
            // Si le fichier n'est pas trouvé on regarde s'il existe un fichier
            // d'erreur par défaut pour le type de status (100,200,300,400,500)
            statusStr = statusStr.charAt(0) + "00";
            errorFilePath = DEFAULT_ERROR_PATH + statusStr;
            path = request.getServletContext().getRealPath(THYMELEAF_PATH + errorFilePath + ERROR_EXTENSION_FILE);
            file = new File(path);
            if (!file.exists()) {
                // Si on ne trouve pas de fichier on renvoie le fichier d'erreur par défaut
                errorFilePath = DEFAULT_ERROR_PATH;
            }
        }

        // On appel le layout de page d'erreur et thymeleaf renvoie le rendu à la réponse
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        SpecificContext context = new SpecificContext();
        ThTemplate template = new DisconnectedLayoutThTemplate(errorFilePath, context);
        String layoutFile = "layouts/disconnectedLayout";
        // Pour les pages d'erreurs on a besoin de savoir quel layout utiliser (connecté / déconnecté)
        if (context.getSession() != null) {
            layoutFile = "layouts/layout";
            template = new LayoutThTemplate(errorFilePath, context);
        } else {
            template.getData().put("config", STUIServiceLocator.getConfigUIService().getConfig());
        }
        template.getData().put("layoutFile", layoutFile);
        template.getData().put("httpCode", status);
        thEngine.render(template, stream);
        return builder.entity(stream.toString()).build();
    }
}
