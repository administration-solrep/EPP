package fr.dila.st.core.schema;

import fr.dila.st.api.constant.STAlertConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Manipulation du schema alert
 *
 * @author SPL
 *
 */
public final class AlertSchemaUtils {

    private AlertSchemaUtils() {
        // do nothing
    }

    public static void setDateValidityBegin(final DocumentModel document, final Calendar cal) {
        PropertyUtil.setProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_DATE_VALIDITY_BEGIN,
            cal
        );
    }

    public static void setDateValidityEnd(final DocumentModel document, final Calendar cal) {
        PropertyUtil.setProperty(document, STAlertConstant.ALERT_SCHEMA, STAlertConstant.ALERT_DATE_VALIDITY_END, cal);
    }

    public static void setPeriodicity(final DocumentModel document, final String periodicity) {
        PropertyUtil.setProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_PERIODICITY,
            periodicity
        );
    }

    public static void setIsActivated(final DocumentModel document, final boolean isActivated) {
        PropertyUtil.setProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_IS_ACTIVATED,
            isActivated
        );
    }

    public static void setRequeteId(final DocumentModel document, final String reqId) {
        PropertyUtil.setProperty(document, STAlertConstant.ALERT_SCHEMA, STAlertConstant.ALERT_REQUETE_ID, reqId);
    }

    public static String getRequeteId(final DocumentModel document) {
        return PropertyUtil.getStringProperty(document, STAlertConstant.ALERT_SCHEMA, STAlertConstant.ALERT_REQUETE_ID);
    }
}
