package fr.dila.st.core.jeton;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.jeton.JetonMaitre;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class JetonMaitreImpl implements JetonMaitre {
    /**
     *
     */
    private static final long serialVersionUID = 8837822447983433637L;

    private DocumentModel doc;

    public JetonMaitreImpl(DocumentModel document) {
        this.doc = document;
    }

    @Override
    public DocumentModel getDocument() {
        return doc;
    }

    @Override
    public String getIdProprietaire() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_MAITRE_SCHEMA,
            STSchemaConstant.JETON_MAITRE_ID_PROPRIETAIRE
        );
    }

    @Override
    public void setIdProprietaire(String id) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_MAITRE_SCHEMA,
            STSchemaConstant.JETON_MAITRE_ID_PROPRIETAIRE,
            id
        );
    }

    @Override
    public Long getNumeroJetonMaitre() {
        return PropertyUtil.getLongProperty(
            doc,
            STSchemaConstant.JETON_MAITRE_SCHEMA,
            STSchemaConstant.JETON_MAITRE_NUMERO
        );
    }

    @Override
    public void setNumeroJetonMaitre(Long id) {
        PropertyUtil.setProperty(doc, STSchemaConstant.JETON_MAITRE_SCHEMA, STSchemaConstant.JETON_MAITRE_NUMERO, id);
    }

    @Override
    public String getTypeWebservice() {
        return PropertyUtil.getStringProperty(
            doc,
            STSchemaConstant.JETON_MAITRE_SCHEMA,
            STSchemaConstant.JETON_MAITRE_WEBSERVICE
        );
    }

    @Override
    public void setTypeWebservice(String type) {
        PropertyUtil.setProperty(
            doc,
            STSchemaConstant.JETON_MAITRE_SCHEMA,
            STSchemaConstant.JETON_MAITRE_WEBSERVICE,
            type
        );
    }

    @Override
    public void saveDocument(CoreSession session) {
        doc = session.saveDocument(doc);
    }
}
