package fr.dila.st.api.alert;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.user.STUser;

public interface Alert {

	/**
	 * Retourne vrai si l'alerte est activé par l'utilisateur.
	 * 
	 * @return vrai si activée
	 */
	Boolean isActivated();

	/**
	 * Active ou non l'alerte
	 * 
	 * @param isActivated
	 *            : vrai si activé/ faux si suspendue
	 */
	void setIsActivated(Boolean isActivated);

	/**
	 * Retourne le document model
	 * 
	 * @return le document
	 */
	DocumentModel getDocument();

	/**
	 * Setter pour le document model.
	 * 
	 * @param doc
	 */
	void setDocument(DocumentModel doc);

	/**
	 * Retourne vrai si l'alerte est activée par l'utilisateur, et si la date d'exécution de la méthode est dans
	 * l'intervalle de validité de l'alerte.
	 * 
	 * @return
	 */
	boolean shouldBeRun();

	/**
	 * Retourne une expression CRON relative aux propriétés de la requête.
	 * 
	 * @deprecated
	 * @return Une chaîne de caractère représentant une expression CRON
	 */
	String getCronExpression();

	/**
	 * Renvoie les ids des destinataires de l'alerte
	 * 
	 * @return les ids des destinataires
	 */
	List<String> getRecipientIds();

	/**
	 * Ajoute une liste d 'identifiants de destinataires (des STUsers) à l'alerte
	 * 
	 * @param la
	 *            liste des identifiants des destinataires
	 */
	void setRecipientIds(List<String> recipients);

	/**
	 * Retourne l'identifiant de la requête associée à cette alerte.
	 * 
	 * @return l'identifiant de la requête.
	 */
	String getRequeteId();

	/**
	 * Met en place l'identifiant de la requête de l'alerte.
	 * 
	 * @param requeteId
	 *            : l'identifiant de la requête
	 */
	void setRequeteId(String requeteId);

	/**
	 * La date de début de validité/première exécution de l'alerte.
	 * 
	 * @return la date de début de validité.
	 */
	Calendar getDateValidityBegin();

	/**
	 * Met la date de début de validité sur l'alerte.
	 * 
	 * @param dateBegin
	 *            la date de début de validité.
	 */
	void setDateValidityBegin(Calendar dateBegin);

	/**
	 * Met la date de fin de validité/date d'expiration sur l'alerte.
	 * 
	 * @param dateEnd
	 *            : la date de fin de validité.
	 */
	void setDateValidityEnd(Calendar dateEnd);

	/**
	 * Retourne la date de fin de validité de l'alerte.
	 * 
	 * @return la date de fin de validité.
	 */
	Calendar getDateValidityEnd();

	/**
	 * Retourne la fréquence à laquelle l'alerte doit être exécutée.
	 * 
	 * @return la fréquence d'exécution de l'alerte.
	 */
	Integer getPeriodicity();

	/**
	 * Met la fréquence d'éxécution de l'alerte.
	 * 
	 * @param periodicity
	 *            la fréquence d'éxcution (en jours)
	 */
	void setPeriodicity(Integer periodicity);

	/**
	 * Retourne vraie si l'alerte doit être lancée aujourd'hui, en prenant en compte uniquement le critère de fréquence.
	 * 
	 * @return vrai si l'alerte doit être exécutée.
	 */
	Boolean periodicityChecked();

	/**
	 * Retourne vraie si l'alerte doit être lancée à la date passée en paramètre, en prenant en compte uniquement le
	 * critère de fréquence.
	 * 
	 * @param calExecution
	 * @return vrai si l'alerte doit être exécutée.
	 */
	Boolean periodicityChecked(Calendar calExecution);

	/**
	 * Retourne vrai si l'alert est entre la date de première excution et la date de fin de validité.
	 * 
	 * @return Vrai si l'alerte est dans la plage de validité.
	 */
	Boolean isBetweenValidityRange();

	/**
	 * Retourne la liste des destinataires de l'alerte.
	 * 
	 * @return la liste des destinataires.
	 * @throws ClientException
	 */
	List<STUser> getRecipients() throws ClientException;

	/**
	 * Retourne la requête experte (requête obtenue par une sauvegarde du requêteur) qui est liée à l'alerte.
	 * 
	 * @param session
	 *            : la session de l'utilisateur.
	 * @return La requête experte.
	 */
	RequeteExperte getRequeteExperte(CoreSession session);

	/**
	 * Met en place le titre de l'alerte
	 * 
	 * @param title
	 *            le titre de l'alerte
	 */
	void setTitle(String title);

	/**
	 * Retourne le titre de l'alerte
	 * 
	 * @return le titre de l'alerte
	 */
	String getTitle();

	/**
	 * Retourne le nom du créateur de l'alerte
	 * 
	 * @return le nom du créateur de l'alerte
	 */
	String getNameCreator();

	/**
	 * Renvoie les destinataires externes de l'alerte
	 * 
	 * @return les mails des destinataires
	 */
	List<String> getExternalRecipients();

	/**
	 * Ajoute une liste de destinataires 
	 * 
	 * @param la liste des mails des destinataires
	 */
	void setExternalRecipients(List<String> externalRecipients);
	
}
