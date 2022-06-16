package fr.dila.st.ui.jaxrs.webobject;

import static fr.dila.st.ui.th.constants.STTemplateConstants.CONDITIONS_ACCESS_URL;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_ERROR_MESSAGE;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_WAIT;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.START_PAGE_SAVE_KEY;

import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliLogin")
public class LoginObject extends SolonWebObject {
    private static final Log LOG = LogFactory.getLog(LoginObject.class);

    @Context
    protected HttpServletRequest request;

    @GET
    public Object getLogin(
        @QueryParam("failed") Boolean failed,
        @QueryParam(LOGIN_WAIT) Boolean wait,
        @QueryParam("resetpwd") Boolean resetpwd
    ) {
        // On essaie d'instancier le contexte pour identifier si l'utilisateur est déjà connecté
        WebContext ctx = WebEngine.getActiveContext();
        if (ctx != null && ctx.getPrincipal() != null) {
            // Si utilisateur déjà connecté on le redirige sur le home
            LOG.debug("User already authenticated, redirecting to home");
            return redirect("/" + getHomePagePath());
        }

        LOG.debug("User not authenticated, displaying login page");

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("config", STUIServiceLocator.getConfigUIService().getConfig());

        String conditionAccesUrl = STServiceLocator
            .getSTParametreService()
            .getParametreWithoutSession(STParametreConstant.PAGE_RENSEIGNEMENTS_ID);
        mapData.put(CONDITIONS_ACCESS_URL, conditionAccesUrl);

        Map<String, Object> mapEtatApp = STUIServiceLocator
            .getEtatApplicationUIService()
            .getEtatApplicationDocumentUnrestricted();
        mapData.putAll(mapEtatApp);

        mapData.put(LOGIN_ERROR_MESSAGE, ResourceHelper.getString("login.auth.error"));

        if (BooleanUtils.isTrue((Boolean) mapEtatApp.get(EtatApplicationService.RESTRICTION_ACCESS))) {
            mapData.put(LOGIN_ERROR_MESSAGE, ResourceHelper.getString("login.auth.restricted"));
        }

        HttpSession session = request.getSession(true);
        boolean setStartPageAttr = false;
        if (BooleanUtils.isTrue(wait)) {
            mapData.put(LOGIN_WAIT, true);
            mapData.put(LOGIN_ERROR_MESSAGE, session.getAttribute(LOGIN_ERROR_MESSAGE));
            setStartPageAttr = true;
        } else {
            if (BooleanUtils.isTrue(failed)) {
                // Si la tentative de connexion a échoué, on indique à thymeleaf d'afficher le message d'erreur
                // Et on définit la page après connexion au home (impossible de récupérer l'URL demandée après un échec
                // d'authent)
                mapData.put(NXAuthConstants.LOGIN_FAILED, true);
                Optional
                    .ofNullable(session.getAttribute(LOGIN_ERROR_MESSAGE))
                    .ifPresent(msg -> mapData.put(LOGIN_ERROR_MESSAGE, msg));
                setStartPageAttr = true;
            }
            if (BooleanUtils.isTrue(resetpwd)) {
                mapData.put("resetPwd", true);
                setStartPageAttr = true;
            }
        }

        if (setStartPageAttr) {
            session.setAttribute(START_PAGE_SAVE_KEY, URLUtils.constructPagePath(getHomePagePath(), request));
        }

        // Création du contexte pour thymeleaf
        SpecificContext context = new SpecificContext();
        context.setContextData(mapData);
        context.setCopyDataToResponse(true);

        return new ThTemplate("login", context);
    }

    protected String getHomePagePath() {
        return "home";
    }
}
