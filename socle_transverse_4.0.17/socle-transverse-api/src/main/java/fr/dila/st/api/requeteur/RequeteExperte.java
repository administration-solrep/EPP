package fr.dila.st.api.requeteur;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface RequeteExperte {
    DocumentModel getDocument();

    void setDocument(DocumentModel document);

    String getWhereClause();

    void setWhereClause(String whereClause);

    String getLastContributor();

    void setLastContributor(String lastContributor);
}
