package fr.dila.st.core.user;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.api.util.XPathUtils;
import fr.dila.st.core.factory.STLogFactory;
import java.util.Calendar;
import java.util.Comparator;
import org.nuxeo.ecm.core.api.SortInfo;

public class UserOrderComparator implements Comparator<STUser> {
    private static final STLogger LOGGER = STLogFactory.getLog(UserOrderComparator.class);
    private static final String UNSORTABLE_COLUMN_ERROR_MESSAGE =
        "La colonne %s n'est pas triable pour les favoris de consultation - utilisateurs";

    private final SortInfo sortInfo;

    public UserOrderComparator(SortInfo sortInfo) {
        this.sortInfo = sortInfo;
    }

    @Override
    public int compare(STUser user0, STUser user1) {
        int result = 0;
        if (sortInfo != null) {
            String xpathColumn = XPathUtils.xPath(STSchemaConstant.USER_SCHEMA, sortInfo.getSortColumn());
            Object value0 = user0.getDocument().getPropertyValue(xpathColumn);
            Object value1 = user1.getDocument().getPropertyValue(xpathColumn);

            if (value0 == null) {
                if (value1 == null) {
                    return 0;
                }
                return -1;
            }

            if (value1 == null) {
                return 1;
            }

            if (value0 instanceof String) {
                if (sortInfo.getSortAscending()) {
                    result = ((String) value0).compareToIgnoreCase((String) value1);
                } else {
                    result = ((String) value1).compareToIgnoreCase((String) value0);
                }
            } else if (value0 instanceof Calendar) {
                if (sortInfo.getSortAscending()) {
                    result = ((Calendar) value0).compareTo((Calendar) value1);
                } else {
                    result = ((Calendar) value1).compareTo((Calendar) value0);
                }
            } else {
                LOGGER.error(
                    STLogEnumImpl.DEFAULT,
                    String.format(UNSORTABLE_COLUMN_ERROR_MESSAGE, sortInfo.getSortColumn())
                );
            }
        }
        return result;
    }
}
