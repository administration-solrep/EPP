package fr.dila.st.api.jeton;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapter pour les documents de type JetonMaitre
 * 
 * @author spesnel
 */
public interface JetonMaitre extends Serializable {

	DocumentModel getDocument();

	String getIdProprietaire();

	void setIdProprietaire(String idProp);

	Long getNumeroJetonMaitre();

	void setNumeroJetonMaitre(Long numero);

	String getTypeWebservice();

	void setTypeWebservice(String type);

	void saveDocument(CoreSession session) throws ClientException;

}
