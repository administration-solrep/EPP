package fr.dila.st.core.schema;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public final class RoutingTaskSchemaUtils {

    public static List<String> getComments(DocumentModel docModel) {
        return PropertyUtil.getStringListProperty(
            docModel,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY
        );
    }

    public static void setComments(DocumentModel docModel, List<String> value) {
        PropertyUtil.setProperty(
            docModel,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY,
            value
        );
    }
}
