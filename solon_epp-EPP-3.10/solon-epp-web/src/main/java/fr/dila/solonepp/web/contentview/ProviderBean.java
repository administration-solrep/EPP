package fr.dila.solonepp.web.contentview;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.core.client.AbstractMapDTO;

@Name("providerBean")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = FRAMEWORK + 1)
public class ProviderBean extends fr.dila.st.web.contentview.ProviderBean {

    private static final long serialVersionUID = -3176979187354080917L;

    public static final String REFRESH_CONTENT_VIEW_EVENT = "refreshContentView";

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    private Map<String, Map<String, Serializable>> filtrableMap;

    public String getResultCount(final List<DocumentModel> documentModels, final long resultCount) {
        final StringBuilder sb = new StringBuilder();

        if (documentModels != null && !documentModels.isEmpty()) {
            if (documentModels.get(0) instanceof DocumentModel) {
                final DocumentModel documentModel = documentModels.get(0);
                sb.append(resultCount);
                sb.append(" ");
                sb.append(resourcesAccessor.getMessages().get("page.result." + documentModel.getType() + (resultCount > 1 ? "s" : "")));
            } else if (documentModels.get(0) instanceof AbstractMapDTO) {
                final AbstractMapDTO abstractMapDTO = (AbstractMapDTO) documentModels.get(0);
                sb.append(resultCount);
                sb.append(" ");
                sb.append(resourcesAccessor.getMessages().get("page.result." + abstractMapDTO.getType() + (resultCount > 1 ? "s" : "")));
            }

        }

        return sb.toString();
    }

    public Map<String, Serializable> getFilter(final String layoutName) throws ClientException {
        if (filtrableMap == null) {
            filtrableMap = new HashMap<String, Map<String, Serializable>>();
        }

        if (filtrableMap.get(layoutName) == null) {
            filtrableMap.put(layoutName, computeNewMap());
        }

        return filtrableMap.get(layoutName);
    }

    /**
     * create a shadow document
     * 
     * @param documentType
     * @return
     * @throws ClientException
     */
    private Map<String, Serializable> computeNewMap() throws ClientException {
        return new HashMap<String, Serializable>();
    }

    public String processFilter(final Map<String, Serializable> filtrableModel, final String layoutName) throws ClientException {
        filtrableMap.put(layoutName, filtrableModel);
        navigationContext.setCurrentDocument(null);
        return null;
    }

    public Map<String, Serializable> resetFilter(final String layoutName) throws ClientException {
        final Map<String, Serializable> map = computeNewMap();
        filtrableMap.put(layoutName, map);
        navigationContext.setCurrentDocument(null);
        return map;
    }

    /**
     * split pour le csv qui ne reconnait pas fn:split
     * 
     * @param value
     * @param regex
     * @return
     */
    public String[] split(final String value, final String regex) {
        return value.split(regex);
    }

    @Observer(value = { REFRESH_CONTENT_VIEW_EVENT })
    public void refreshProvider() {
        contentViewActions.refreshOnSeamEvent(EventNames.DOCUMENT_CHANGED);
    }

}
