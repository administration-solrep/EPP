package fr.dila.solonepp.web.action.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.st.core.util.DateUtil;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean spécifique pour la navigation dans la recherche avancée via administrator
 * 
 * @author asatre
 * 
 */
@Name("adminsSearchActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class AdminSearchActionsBean implements Serializable {

    private static final long serialVersionUID = 8216792523503874675L;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    public String navigateTo(final String id) {
        try {
            final DocumentModel doc = documentManager.getDocument(new IdRef(id));
            navigationContext.setCurrentDocument(doc);
        } catch (final ClientException e) {
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
        }
        return null;
    }

    public Map<String, Map<String, Object>> prepareCurrentDoc() {
        final Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        try {

            final DocumentModel doc = navigationContext.getCurrentDocument();
            if (doc == null) {
                facesMessages.add(StatusMessage.Severity.WARN, "Pas de document courant");
            } else {
                final String[] schemas = doc.getSchemas();
                for (final String schema : schemas) {
                    final Map<String, Object> datas = doc.getProperties(schema);
                    for (final String key : datas.keySet()) {
                        final Object object = datas.get(key);
                        if (object instanceof Calendar) {
                            final String date = DateUtil.formatWithHour(((Calendar) object).getTime());
                            datas.put(key, date);
                        } else if (object instanceof List) {
                            @SuppressWarnings({ "unchecked", "rawtypes" })
                            final List<Object> list = (List) object;
                            final List<Object> newList = new ArrayList<Object>();
                            for (final Object elem : list) {
                                if (elem instanceof Calendar) {
                                    final String date = DateUtil.formatWithHour(((Calendar) elem).getTime());
                                    newList.add(date);
                                } else {
                                    newList.add(elem);
                                }
                            }
                            datas.put(key, newList);
                        } else if (object instanceof Calendar[]) {
                            final List<Calendar> list = Arrays.asList((Calendar[]) object);
                            final List<String> newList = new ArrayList<String>();
                            for (final Calendar elem : list) {
                                final String date = DateUtil.formatWithHour(elem.getTime());
                                newList.add(date);
                            }
                            datas.put(key, newList);
                        }
                    }
                    map.put(schema, datas);
                }
            }
        } catch (final ClientException e) {
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
        }

        return map;
    }
}
