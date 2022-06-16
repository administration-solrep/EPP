package fr.dila.st.ui.jaxrs.webobject;

import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.ActionEnum;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import java.io.Serializable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

public class SolonWebObject extends DefaultObject {
    private static final String SLASH = "/";

    @Context
    private HttpServletRequest request;

    protected ThTemplate template = getMyTemplate();

    protected SpecificContext context = getMyContext();

    @Override
    protected final void initialize(Object... args) {
        super.initialize(args);
        if (args.length > 0 && args[0] instanceof SpecificContext) {
            context = (SpecificContext) args[0];
            if (args.length > 1 && args[1] instanceof ThTemplate) {
                template = (ThTemplate) args[1];
            }
        }
    }

    @Override
    public Response redirect(String uri) {
        String completeUri = URLUtils.generateRedirectPath(uri, request);
        return super.redirect(completeUri);
    }

    public Response redirectWithoutContext(String uri) {
        String baseUrl = VirtualHostHelper.getBaseURL(request);
        if (baseUrl.endsWith(SLASH) && uri.startsWith(SLASH)) {
            uri = uri.substring(1);
        }
        return super.redirect(baseUrl + uri);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    protected ThTemplate getMyTemplate() {
        return new ThTemplate();
    }

    protected SpecificContext getMyContext() {
        return new SpecificContext();
    }

    protected void addMessageQueueInSession() {
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
    }

    /**
     * A n'appeler que si besoin de recharger la page et de passer la MessageQueue en session pour pouvoir afficher les messages au rechargement.
     * @return
     */
    protected Response getJsonResponseWithMessagesInSessionIfSuccess() {
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            addMessageQueueInSession();
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    /**
     * A n'appeler que si besoin de recharger la page avec data et de passer la MessageQueue en session pour pouvoir afficher les messages au rechargement.
     * @return
     */
    protected Response getJsonDataResponseWithMessagesInSessionIfSuccess(Serializable data) {
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            addMessageQueueInSession();
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), data).build();
    }

    protected void verifyAction(ActionEnum action, String ressource) {
        if (context.getAction(action) == null) {
            throw new STAuthorizationException(ressource);
        }
    }

    protected void verifyActions(List<ActionEnum> actions, String ressource) {
        if (actions.stream().allMatch(action -> context.getAction(action) == null)) {
            throw new STAuthorizationException(ressource);
        }
    }
}
