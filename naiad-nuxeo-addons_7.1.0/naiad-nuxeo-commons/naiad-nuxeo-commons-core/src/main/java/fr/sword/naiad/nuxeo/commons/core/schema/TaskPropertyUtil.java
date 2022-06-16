package fr.sword.naiad.nuxeo.commons.core.schema;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Manipulation du schema Task
 * @author SPL
 *
 */
public final class TaskPropertyUtil {

    /**
     * Utility class
     */
    private TaskPropertyUtil() {
        // do nothing
    }

    public static List<String> getTargetDocumentsIds(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getStringListProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_TARGETDOCIDS);
    }

    public static void setTargetDocumentsIds(final DocumentModel doc, final List<String> value) throws NuxeoException {
        PropertyUtil.setProperty(doc, CommonSchemaConstant.SCHEMA_TASK, CommonSchemaConstant.PROP_TASK_TARGETDOCIDS,
                value);
    }

    public static String getType(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getStringProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_TYPE);
    }

    public static void setType(final DocumentModel doc, final String type) throws NuxeoException {
        PropertyUtil.setProperty(doc, CommonSchemaConstant.SCHEMA_TASK, CommonSchemaConstant.PROP_TASK_TYPE, type);
    }

    public static List<Map<String, Serializable>> getTaskVariables(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getMapStringSerializableListProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_VARIABLES);
    }

    public static void setTaskVariables(final DocumentModel doc, final List<Map<String, Serializable>> value)
            throws NuxeoException {
        PropertyUtil.setProperty(doc, CommonSchemaConstant.SCHEMA_TASK, CommonSchemaConstant.PROP_TASK_VARIABLES,
                value);
    }

    public static Calendar getDueDate(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getCalendarProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_DUEDATE);
    }

    public static void setDueDate(final DocumentModel doc, final Calendar dueDate) throws NuxeoException {
        PropertyUtil.setProperty(doc, CommonSchemaConstant.SCHEMA_TASK, CommonSchemaConstant.PROP_TASK_DUEDATE,
                dueDate);
    }

    public static List<String> getDelegated(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getStringListProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_DELEGATED);
    }

    public static void setDelegated(final DocumentModel doc, final List<String> delegates) throws NuxeoException {
        PropertyUtil.setProperty(doc, CommonSchemaConstant.SCHEMA_TASK, CommonSchemaConstant.PROP_TASK_DELEGATED,
                delegates);
    }

    public static List<String> getTaskActors(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getStringListProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_ACTORS);
    }

    public static String getTaskName(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getStringProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_NAME);
    }

    public static String getTaskInitiator(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getStringProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_INITIATOR);
    }

    public static String getProcessId(final DocumentModel doc) throws NuxeoException {
        return PropertyUtil.getStringProperty(doc, CommonSchemaConstant.SCHEMA_TASK,
                CommonSchemaConstant.PROP_TASK_PROCESSID);
    }
}
