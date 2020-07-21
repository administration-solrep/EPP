package fr.dila.st.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.dila.st.api.alert.Alert;

/**
 * Le service de gestion des alertes, chargé d'exécuter ces alertes à un moment donné.
 * 
 * @author jgomez
 */
public interface STAlertService {

	/**
	 * Execute la requête et envoie le résultat aux destinataires spécifiés dans l'alerte.
	 * 
	 * @param alertDoc
	 * @param requeteId
	 * @throws Exception
	 */
	List<Alert> getAlertsToBeRun(CoreSession session);

	/**
	 * Envoie un mail à partir d'une alert. Le mail est envoyé avec le mail du créateur de l'alerte aux destinataires.
	 * 
	 * @param session
	 *            la session utilisée pour envoyer l'alerte.
	 * @param alert
	 *            L'alerte en paramêtre.
	 * @return Vrai si l'alert a bien été exécuté, faux sinon.
	 * @throws ClientException
	 * @throws Exception
	 * 
	 */
	Boolean sendMail(CoreSession session, Alert alert) throws ClientException;

	/**
	 * Créé une alerte non activée
	 * 
	 * @param session
	 * @return Alert l'alerte créée, null si la création a échoué
	 */
	Alert initAlert(final CoreSession session);

	/**
	 * Créé une alerte non activée à partir d'un documentModel d'alert non persisté
	 * 
	 * @param session
	 * @param doc
	 *            le documentModel de l'alerte à créer
	 * @return Alert l'alerte créée, null si la création a échoué
	 */
	Alert initAlert(final CoreSession session, DocumentModel alertDoc);

	/**
	 * Supprime une alerte
	 * 
	 * @param session
	 * @param alert
	 *            l'alerte à supprimer
	 * @return boolean vrai si la suppression s'est bien déroulée, faux sinon
	 */
	boolean deleteAlert(final CoreSession session, final Alert alert);

	/**
	 * Supprime une alerte à partir de son documentModel
	 * 
	 * @param session
	 * @param alertDoc
	 *            le documentModel de l'alerte à supprimer
	 * @return boolean vrai si la suppression s'est bien déroulée, faux sinon
	 */
	boolean deleteAlert(final CoreSession session, final DocumentModel alertDoc);

	/**
	 * Supprime une alerte à partir de son documentRef
	 * 
	 * @param session
	 * @param alertRef
	 *            le documentRef de l'alerte à supprimer
	 * @return boolean vrai si la suppression s'est bien déroulée, faux sinon
	 */
	boolean deleteAlert(final CoreSession session, final DocumentRef alertRef);

	/**
	 * Active une alerte
	 * 
	 * @param session
	 * @param alert
	 *            l'alerte à activer
	 */
	void activateAlert(final CoreSession session, final Alert alert);

	/**
	 * Desactive une alerte
	 * 
	 * @param session
	 * @param alert
	 *            l'alerte à désactiver
	 */
	void suspendAlert(final CoreSession session, final Alert alert);
}
