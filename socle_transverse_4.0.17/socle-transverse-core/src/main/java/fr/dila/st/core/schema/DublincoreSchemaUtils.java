package fr.dila.st.core.schema;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Manipulation du schema dublincore
 *
 * @author SPL
 *
 */
public final class DublincoreSchemaUtils {

    /**
     * utility class
     */
    private DublincoreSchemaUtils() {
        // do nothing
    }

    public static String getTitle(final DocumentModel document) {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_TITLE_PROPERTY
        );
    }

    public static void setTitle(final DocumentModel document, final String title) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_TITLE_PROPERTY,
            title
        );
    }

    public static String getDescription(final DocumentModel document) {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY
        );
    }

    public static void setDescription(final DocumentModel document, final String description) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY,
            description
        );
    }

    public static Calendar getModifiedDate(final DocumentModel document) {
        return PropertyUtil.getCalendarProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_MODIFIED_PROPERTY
        );
    }

    public static void setModifiedDate(final DocumentModel document, final Calendar modifiedDate) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_MODIFIED_PROPERTY,
            modifiedDate
        );
    }

    public static String getCreator(final DocumentModel document) {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_CREATOR_PROPERTY
        );
    }

    public static void setCreator(final DocumentModel document, final String creator) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_CREATOR_PROPERTY,
            creator
        );
    }

    public static String getLastContributor(final DocumentModel document) {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_LAST_CONTRIBUTOR_PROPERTY
        );
    }

    public static void setLastContributor(final DocumentModel document, final String lastContributor) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_LAST_CONTRIBUTOR_PROPERTY,
            lastContributor
        );
    }

    public static Calendar getCreatedDate(final DocumentModel document) {
        return PropertyUtil.getCalendarProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_CREATED_PROPERTY
        );
    }

    public static void setCreatedDate(final DocumentModel document, final Calendar createdDate) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_CREATED_PROPERTY,
            createdDate
        );
    }

    public static Calendar getValidDate(final DocumentModel document) {
        return PropertyUtil.getCalendarProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_VALID_PROPERTY
        );
    }

    public static void setValidDate(final DocumentModel document, final Calendar validDate) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.DUBLINCORE_SCHEMA,
            STSchemaConstant.DUBLINCORE_VALID_PROPERTY,
            validDate
        );
    }
}
