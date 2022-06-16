package fr.dila.st.core.domain.user;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.user.BaseFunction;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier fonction unitaire.
 *
 * @author jtremeaux
 */
public class BaseFunctionImpl implements BaseFunction {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    protected DocumentModel document;

    /**
     * Constructeur de BaseFunctionImpl.
     *
     * @param document
     *            Document
     */
    public BaseFunctionImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public void setDocument(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getGroupname() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.BASE_FUNCTION_SCHEMA,
            STSchemaConstant.BASE_FUNCTION_GROUP_NAME_PROPERTY
        );
    }

    @Override
    public void setGroupname(String groupname) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.BASE_FUNCTION_SCHEMA,
            STSchemaConstant.BASE_FUNCTION_GROUP_NAME_PROPERTY,
            groupname
        );
    }

    @Override
    public String getDescription() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.BASE_FUNCTION_SCHEMA,
            STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY
        );
    }

    @Override
    public void setDescription(String description) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.BASE_FUNCTION_SCHEMA,
            STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY,
            description
        );
    }
}
