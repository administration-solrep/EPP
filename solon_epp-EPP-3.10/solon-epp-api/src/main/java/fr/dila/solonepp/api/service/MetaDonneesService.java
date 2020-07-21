package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;

/**
 * Service permettant de gérer les metadonnées .
 * 
 * @author asatre
 */
public interface MetaDonneesService extends Serializable {

	 /**
     * Retourne les metadonnees correspondant au type d'evenement.
     * 
     * @param evenementType Type d'événement
     * @return Description du type d'événement
     * @throws ClientException
     */
    MetaDonneesDescriptor getEvenementType(final String evenementType) throws ClientException;

    /**
     * 
     * @param evenementType
     * @return la map des {@link PropertyDescriptor} correspondant au type d'evenement
     * @throws ClientException
     */
	Map<String, PropertyDescriptor> getMapProperty(final String evenementType) throws ClientException;
	
	/**
	 * retourne la liste de tous les descriptors
	 * @return
	 */
	List<MetaDonneesDescriptor> getAll();
	
	/**
	 * Remap les metadonnes avec des valeurs par defaut d'un document a un autre
	 * 
	 * @param docPrecedent
	 * @param docSuivant
	 * @param mapProperty
	 * @return
	 * @throws ClientException
	 */
	DocumentModel remapDefaultMetaDonnees(DocumentModel docPrecedent, DocumentModel docSuivant, DocumentModel doc, Map<String, PropertyDescriptor> mapProperty) throws ClientException;

	/**
	 * Remap les metadonnees avec des valeurs conditionnelles
	 * @param eventDoc
	 * @param versionDoc
	 * @param mapProperty
	 * @throws ClientException 
	 */
    void remapConditionnelMetaDonnees(DocumentModel eventDoc, DocumentModel versionDoc, Map<String, PropertyDescriptor> mapProperty) throws ClientException;
}
