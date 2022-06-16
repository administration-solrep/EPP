package fr.dila.st.core.requeteur;

import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

public class RequeteExperteImpl implements RequeteExperte {
    protected DocumentModel document;

    public RequeteExperteImpl(DocumentModel doc) {
        this.document = doc;
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
    public String getWhereClause() {
        return PropertyUtil.getStringProperty(
            document,
            STRequeteConstants.SMART_FOLDER_SCHEMA,
            STRequeteConstants.SMART_FOLDER_QUERY_PART_PROP
        );
    }

    @Override
    public void setWhereClause(String whereClause) {
        PropertyUtil.setProperty(
            document,
            STRequeteConstants.SMART_FOLDER_SCHEMA,
            STRequeteConstants.SMART_FOLDER_QUERY_PART_PROP,
            whereClause
        );
    }

    @Override
    public String getLastContributor() {
        return DublincoreSchemaUtils.getLastContributor(document);
    }

    @Override
    public void setLastContributor(String lastContributor) {
        DublincoreSchemaUtils.setLastContributor(document, lastContributor);
    }
}
