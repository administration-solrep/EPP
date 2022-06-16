package fr.dila.st.ui.th.model;

import com.google.common.base.Strings;
import fr.dila.st.core.util.RegexUtils;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.ActionCategory;
import fr.dila.st.ui.enums.ActionEnum;
import fr.dila.st.ui.enums.ContextDataKey;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ActionContext;
import org.nuxeo.ecm.platform.actions.ELActionContext;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.api.Framework;

public class SpecificContext {
    public static final String MESSAGE_QUEUE = "messageQueue";
    public static final String LAST_TEMPLATE = "lastTemplate";
    private static final String VALUE_TYPE_ERROR_MESSAGE =
        "Le type de la donnée %s ne correspond pas à celui de la clé %s : %s";

    /**
     * Pattern regex permettant de repérer les properties à traduire dans les action
     * contribs, de la forme ##{toEval}
     */
    private static final String PROPS_PATTERN_STRING = "##\\{[^\\{\\}]*\\}";
    private static final Pattern PROPS_PATTERN = Pattern.compile(PROPS_PATTERN_STRING);

    /**
     * Nombre maximal de matches de messages properties à trouver dans une property
     * d'action contrib afin d'éviter une CWE-606: Unchecked Input for Loop
     * Condition.
     */
    private static final int MAX_NB_PROPS_TO_FIND = 100;

    private CoreSession session;
    private WebContext webcontext;
    private DocumentModel currentDocument;
    private Map<String, Object> contextData = new HashMap<>();
    private Boolean copyDataToResponse = false;
    private SolonAlertManager messageQueue;

    public SpecificContext() {
        this.webcontext = WebEngine.getActiveContext();
        this.messageQueue = new SolonAlertManager();
        // CoreSession cannot be created without a principal
        if (this.webcontext != null && this.webcontext.getPrincipal() != null) {
            this.session = this.webcontext.getCoreSession();
            // Si messageQueue présent en session on le remplace
            UserSession userSession = this.webcontext.getUserSession();
            if (userSession.get(SpecificContext.MESSAGE_QUEUE) != null) {
                SolonAlertManager alertManager = (SolonAlertManager) userSession.get(SpecificContext.MESSAGE_QUEUE);
                userSession.remove(SpecificContext.MESSAGE_QUEUE);
                this.messageQueue = alertManager;
            }
        }
    }

    public CoreSession getSession() {
        return session;
    }

    public void setSession(CoreSession session) {
        this.session = session;
    }

    public Map<String, Object> getContextData() {
        return contextData;
    }

    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }

    public WebContext getWebcontext() {
        return webcontext;
    }

    public void setWebcontext(WebContext webcontext) {
        this.webcontext = webcontext;
    }

    public Boolean getCopyDataToResponse() {
        return copyDataToResponse;
    }

    public void setCopyDataToResponse(Boolean copyDataToResponse) {
        this.copyDataToResponse = copyDataToResponse;
    }

    public DocumentModel getCurrentDocument() {
        return currentDocument;
    }

    public void setCurrentDocument(DocumentModel currentDocument) {
        this.currentDocument = currentDocument;
    }

    public void setCurrentDocument(String idDocument) {
        if (StringUtils.isNotBlank(idDocument)) {
            currentDocument = session.getDocument(new IdRef(idDocument));
        }
    }

    public List<Action> getActions(ActionCategory category) {
        final ActionManager actionService = Framework.getService(ActionManager.class);
        ActionContext actionContext = createActionContext();
        List<Action> actions = actionService.getActions(category.getName(), actionContext);
        actions.forEach(action -> evalActionProperties(action, actionContext));
        return actions;
    }

    private Action evalActionProperties(Action action, ActionContext actionContext) {
        Map<String, Serializable> props = action.getProperties();
        Map<String, Serializable> newProps = new HashMap<>();

        for (Entry<String, Serializable> entry : props.entrySet()) {
            String propValue = entry.getValue().toString();
            AtomicReference<String> valueWrapper = new AtomicReference<>(propValue);
            RegexUtils
                .listMatches(PROPS_PATTERN, propValue, MAX_NB_PROPS_TO_FIND)
                .stream()
                .map(val -> ImmutablePair.of(val, actionContext.evalExpression(val.substring(1), String.class)))
                .forEachOrdered(
                    pair -> valueWrapper.set(StringUtils.replace(valueWrapper.get(), pair.getLeft(), pair.getRight()))
                );

            newProps.put(entry.getKey(), valueWrapper.get());
        }
        action.setProperties(newProps);
        computeLabel(action);
        return action;
    }

    private final void computeLabel(Action action) {
        String label = action.getLabel();
        if (StringUtils.isBlank(label)) {
            return;
        }

        if (BooleanUtils.isTrue(getFromContextData(STContextDataKey.IS_ACTION_MASS))) {
            String labelMass = label + ".mass";
            if (ResourceHelper.exists(labelMass)) {
                action.setLabel(labelMass);
            }
        }
    }

    public Action getAction(ActionEnum actionId) {
        final ActionManager actionService = Framework.getService(ActionManager.class);
        ActionContext actionContext = createActionContext();
        Action action = actionService.getAction(actionId.getName(), actionContext, true);
        if (action != null) {
            evalActionProperties(action, actionContext);
        }

        return action;
    }

    private ActionContext createActionContext() {
        ActionContext actionContext = new ELActionContext();

        actionContext.setCurrentDocument(this.currentDocument);
        actionContext.putAllLocalVariables(contextData);

        actionContext.setDocumentManager(this.session);
        if (this.session != null) {
            actionContext.setCurrentPrincipal(this.session.getPrincipal());
        }
        return actionContext;
    }

    @SuppressWarnings("unchecked")
    public List<Breadcrumb> getNavigationContext() {
        if (
            webcontext != null &&
            webcontext.getUserSession() != null &&
            webcontext.getUserSession().containsKey(Breadcrumb.USER_SESSION_KEY)
        ) {
            return (List<Breadcrumb>) webcontext.getUserSession().get(Breadcrumb.USER_SESSION_KEY);
        }
        return new ArrayList<>();
    }

    public boolean comesFromMenu(String menuLabelKey) {
        return getMenuIfExist(menuLabelKey) != null;
    }

    public Breadcrumb getMenuIfExist(String menuLabelKey) {
        List<Breadcrumb> lstContext = getNavigationContext();

        return lstContext.stream().filter(b -> b.isThisMenu(menuLabelKey)).findFirst().orElse(null);
    }

    public void setNavigationContextTitle(String title) {
        if (webcontext != null && webcontext.getUserSession() != null) {
            removeNavigationContextTitle(Breadcrumb.TITLE_ORDER);
            setNavigationContextTitle(new Breadcrumb(title, null, Breadcrumb.TITLE_ORDER));
        }
    }

    public void setNavigationContextTitle(Breadcrumb crumbTitle) {
        if (webcontext != null && webcontext.getUserSession() != null) {
            removeNavigationContextTitle(crumbTitle.getOrder());

            List<Breadcrumb> lstContext = getNavigationContext();
            lstContext.add(crumbTitle);
            webcontext.getUserSession().put(Breadcrumb.USER_SESSION_KEY, lstContext);
        }
    }

    public void changeContextUrl(String title, String baseUrl) {
        List<Breadcrumb> lstBreadCrumbs = getNavigationContext();
        lstBreadCrumbs =
            lstBreadCrumbs
                .stream()
                .map(
                    b -> {
                        // Obligatoire car les objets sont Immutable
                        Breadcrumb crumb = new Breadcrumb(b.getKey(), b.getUrl(), b.getOrder());
                        if (crumb.isThisMenu(title)) {
                            String params = Strings.nullToEmpty(webcontext.getRequest().getQueryString());
                            if (Strings.isNullOrEmpty(params) || baseUrl.contains("?")) {
                                crumb.setUrl(baseUrl);
                            } else {
                                crumb.setUrl(baseUrl + "?" + params);
                            }
                        }
                        return crumb;
                    }
                )
                .collect(Collectors.toList());
        webcontext.getUserSession().put(Breadcrumb.USER_SESSION_KEY, lstBreadCrumbs);
    }

    public List<Breadcrumb> getNavigationContextTitle() {
        List<Breadcrumb> lstContext = getNavigationContext();
        // Filtre de titres précédents
        return lstContext.stream().filter(n -> (n.getOrder() >= Breadcrumb.TITLE_ORDER)).collect(Collectors.toList());
    }

    public void clearNavigationContext() {
        webcontext.getUserSession().remove(Breadcrumb.USER_SESSION_KEY);
    }

    public void removeNavigationContextTitle() {
        removeNavigationContextTitle(Breadcrumb.TITLE_ORDER);
    }

    public void removeNavigationContextTitle(Integer order) {
        if (order != null) {
            List<Breadcrumb> lstContext = getNavigationContext();
            // Filtre de titres précédents
            lstContext = lstContext.stream().filter(n -> (n.getOrder() < order)).collect(Collectors.toList());
            webcontext.getUserSession().put(Breadcrumb.USER_SESSION_KEY, lstContext);
        }
    }

    public SolonAlertManager getMessageQueue() {
        return messageQueue;
    }

    public void setMessageQueue(SolonAlertManager messageQueue) {
        this.messageQueue = messageQueue;
    }

    public Boolean getBooleanProperty(String key) {
        return (Boolean) getProperty(key);
    }

    public String getStringProperty(String key) {
        return (String) getProperty(key);
    }

    public Object getProperty(String key) {
        return this.webcontext.getProperty(key);
    }

    public void setProperty(String key, Object value) {
        this.webcontext.setProperty(key, value);
    }

    public String getUrlPreviousPage() {
        List<Breadcrumb> navigationContext = getNavigationContext();
        if (!navigationContext.isEmpty()) {
            Breadcrumb breadcrumb = navigationContext.size() > 1
                ? navigationContext.get(navigationContext.size() - 2)
                : navigationContext.get(0);
            if (breadcrumb.getUrl() != null) {
                return breadcrumb.getUrl();
            }
        }
        return "";
    }

    public <T> void putInContextData(ContextDataKey key, T value) {
        if (value != null && !key.getValueType().isInstance(value)) {
            throw new IllegalArgumentException(
                String.format(VALUE_TYPE_ERROR_MESSAGE, value, key.getName(), key.getValueType().getSimpleName())
            );
        }

        putInContextData(key.getName(), value);
    }

    public <T> void putInContextData(String key, T value) {
        contextData.put(key, value);
    }

    public <T> T getFromContextData(ContextDataKey key) {
        return getFromContextData(key.getName());
    }

    public <T> T computeFromContextDataIfAbsent(ContextDataKey key, Supplier<T> supplierFunction) {
        return computeFromContextDataIfAbsent(key, k -> supplierFunction.get());
    }

    @SuppressWarnings("unchecked")
    public <T> T computeFromContextDataIfAbsent(ContextDataKey key, Function<ContextDataKey, T> mappingFunction) {
        Function<String, T> stringMappingFunction = mappingFunction.compose((String s) -> key);
        return (T) contextData.computeIfAbsent(key.getName(), stringMappingFunction);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFromContextData(String key) {
        return (T) contextData.get(key);
    }

    public boolean containsKeyInContextData(ContextDataKey key) {
        return containsKeyInContextData(key.getName());
    }

    public boolean containsKeyInContextData(String key) {
        return contextData.containsKey(key);
    }
}
