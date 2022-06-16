package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.core.alert.AlertImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapteur de Document vers Alert.
 *
 * @author jgomez
 */
public class AlertAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constrcutor
     */
    public AlertAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STAlertConstant.ALERT_SCHEMA);
        return new AlertImpl(doc);
    }
}
