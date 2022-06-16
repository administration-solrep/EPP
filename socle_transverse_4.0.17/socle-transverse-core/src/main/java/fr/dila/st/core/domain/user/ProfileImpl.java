package fr.dila.st.core.domain.user;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.user.Profile;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.GroupConfig;

/**
 * Implémentation de l'objet métier profil.
 *
 * @author jtremeaux
 */
public class ProfileImpl implements Profile {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    protected DocumentModel document;

    /**
     * Constructeur de ProfileImpl.
     *
     * @param document
     *            Document
     */
    public ProfileImpl(DocumentModel document) {
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
    public String getName() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.GROUP_SCHEMA, GroupConfig.DEFAULT_ID_FIELD);
    }

    @Override
    public List<String> getBaseFunctionList() {
        return PropertyUtil.getStringListProperty(
            document,
            STSchemaConstant.GROUP_SCHEMA,
            STSchemaConstant.DIRECTORY_GROUP_FUNCTIONS_PROPERTY
        );
    }

    @Override
    public void setBaseFunctionList(List<String> baseFunctionList) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.GROUP_SCHEMA,
            STSchemaConstant.DIRECTORY_GROUP_FUNCTIONS_PROPERTY,
            baseFunctionList
        );
    }
}
