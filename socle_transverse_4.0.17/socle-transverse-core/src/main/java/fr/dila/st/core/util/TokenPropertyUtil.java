package fr.dila.st.core.util;

import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

public class TokenPropertyUtil {
    public static final String SCHEMA = "authtoken";

    public static final String TOKEN_PROP = "token";
    public static final String USERNAME_PROP = "userName";
    public static final String CREATIONDATE_PROP = "creationDate";

    public static String getToken(DocumentModel doc) {
        return PropertyUtil.getStringProperty(doc, SCHEMA, TOKEN_PROP);
    }

    public static String getUserName(DocumentModel doc) {
        return PropertyUtil.getStringProperty(doc, SCHEMA, USERNAME_PROP);
    }

    public static Calendar getCreationDate(DocumentModel doc) {
        return PropertyUtil.getCalendarProperty(doc, SCHEMA, CREATIONDATE_PROP);
    }
}
