package fr.dila.st.api.jeton;

import java.io.Serializable;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface LockJetonMaitre extends Serializable {
    DocumentModel getDocument();

    String getIdProprietaire();

    void setIdProprietaire(String idProp);

    String getIdJetonMaitre();

    void setIdJetonMaitre(String idjeton);

    Long getNumeroJetonMaitre();

    void setNumeroJetonMaitre(Long numero);

    String getTypeWebservice();

    void setTypeWebservice(String type);

    void saveDocument(CoreSession session);
}
