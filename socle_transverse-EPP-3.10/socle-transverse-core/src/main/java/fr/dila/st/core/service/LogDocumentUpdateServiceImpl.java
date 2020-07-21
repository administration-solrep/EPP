package fr.dila.st.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.service.LogDocumentUpdateService;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;

/**
 * Implémentation du service de gestion de log des actions.
 * 
 * @author jtremeaux
 */
public abstract class LogDocumentUpdateServiceImpl implements LogDocumentUpdateService {

	/**
	 * Serial UID.
	 */
	private static final long		serialVersionUID	= 1L;

	/**
	 * Chaîne de caractère affichée par défaut lorsqu'on fait un toString sur une liste vide.
	 */
	protected static final String	EMPTY_LIST_STRING	= "[]";

	/**
	 * Logger.
	 */
	// private static final Log LOG = LogFactory.getLog(LogDocumentUpdateServiceImpl.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void logAllDocumentUpdate(CoreSession session, DocumentModel document) throws ClientException {
		// on récupère l'ancien document
		DocumentModel ancienDocument = new UnrestrictedGetDocumentRunner(session).getById(document.getId());

		// pour chaque métadonnée du document, on regarde si la valeur a été modifiée
		for (Entry<String, Object> entry : getMap(document).entrySet()) {
			String entryKey = entry.getKey();
			// l'entrée modifiée doit faire partie des données du document
			if (!getUnLoggableEntry().contains(entryKey)) {
				// TODO décomposer la clé et faire un getProperty
				Object ancienDocumentValue = ancienDocument.getPropertyValue(entry.getKey());
				if (ancienDocumentValue instanceof String && ((String) ancienDocumentValue).isEmpty()) {
					ancienDocumentValue = null;
				}
				// si la nouvelle valeur retournée est de type string et est vide, cette valeur est null.
				Object nouveauDocumentValue = entry.getValue();
				if (nouveauDocumentValue instanceof String && ((String) nouveauDocumentValue).isEmpty()) {
					nouveauDocumentValue = null;
				}

				// Transformation des listes
				if (ancienDocumentValue instanceof List) {
					if (!((List) ancienDocumentValue).isEmpty() && ((List) ancienDocumentValue).get(0) instanceof Calendar) {
						ancienDocumentValue = getListCalendarValue((List<Calendar>) ancienDocumentValue);
					}
				}

				if (nouveauDocumentValue instanceof List) {
					if (!((List) nouveauDocumentValue).isEmpty() && ((List) nouveauDocumentValue).get(0) instanceof Calendar) {
						nouveauDocumentValue = getListCalendarValue((List<Calendar>) nouveauDocumentValue);
					}
				}

				// si la valeur a été changée, on appelle le service de log
				if ((ancienDocumentValue == null && nouveauDocumentValue != null)
						|| (ancienDocumentValue != null && !ancienDocumentValue.equals(nouveauDocumentValue))) {
					String ancienDocumentValueLabel = "";
					if (ancienDocumentValue != null) {
						ancienDocumentValueLabel = ancienDocumentValue.toString();
					}
					// HACK : gère le cas de tableau vide
					if (ancienDocumentValueLabel.equals(EMPTY_LIST_STRING)) {
						ancienDocumentValueLabel = "";
					}

					// gestion de l'affichage du format Calendar
					if (ancienDocumentValue instanceof Calendar) {
						ancienDocumentValueLabel = DateUtil.formatDDMMYYYYSlash((Calendar) ancienDocumentValue);
					}
					if (nouveauDocumentValue instanceof Calendar) {
						nouveauDocumentValue = DateUtil.formatDDMMYYYYSlash((Calendar) nouveauDocumentValue);
					}

					// gestion de l'affichage du format Boolean
					if (ancienDocumentValue instanceof Boolean) {
						if (ancienDocumentValueLabel.equals("false")) {
							ancienDocumentValueLabel = "Non";
						} else if (ancienDocumentValueLabel.equals("true")) {
							ancienDocumentValueLabel = "Oui";
						}
					}
					if (nouveauDocumentValue instanceof Boolean) {
						if (!(Boolean) nouveauDocumentValue) {
							nouveauDocumentValue = "Non";
						} else if ((Boolean) nouveauDocumentValue) {
							nouveauDocumentValue = "Oui";
						}
					}
					// TODO - faire un code générique pour tous les types complexes de l'application (hashmap)
					if (ancienDocumentValue instanceof HashMap<?, ?> && (entryKey.equals("tp:signatureSgg")
							|| entryKey.equals("tp:signaturePm") || entryKey.equals("tp:signaturePr"))) {

						Calendar olddateRetourSignature = ((HashMap<String, Calendar>) ancienDocumentValue)
								.get("dateRetourSignature");
						String olddateRetourSignatureLabel = olddateRetourSignature == null ? ""
								: DateUtil.formatDDMMYYYYSlash(olddateRetourSignature);
						Calendar olddateEnvoiSignature = ((HashMap<String, Calendar>) ancienDocumentValue)
								.get("dateEnvoiSignature");
						String olddateEnvoiSignatureLabel = olddateEnvoiSignature == null ? ""
								: DateUtil.formatDDMMYYYYSlash(olddateEnvoiSignature);
						String oldcommentaireSignature = ((HashMap<String, String>) ancienDocumentValue)
								.get("commentaireSignature");
						oldcommentaireSignature = oldcommentaireSignature == null ? "" : oldcommentaireSignature;

						if (nouveauDocumentValue != null) {
							Calendar newdateRetourSignature = ((HashMap<String, Calendar>) nouveauDocumentValue)
									.get("dateRetourSignature");
							String newdateRetourSignatureLabel = newdateRetourSignature == null ? ""
									: DateUtil.formatDDMMYYYYSlash(newdateRetourSignature);
							Calendar newdateEnvoiSignature = ((HashMap<String, Calendar>) nouveauDocumentValue)
									.get("dateEnvoiSignature");
							String newdateEnvoiSignatureLabel = newdateEnvoiSignature == null ? ""
									: DateUtil.formatDDMMYYYYSlash(newdateEnvoiSignature);
							String newcommentaireSignature = ((HashMap<String, String>) nouveauDocumentValue)
									.get("commentaireSignature");
							newcommentaireSignature = newcommentaireSignature == null ? "" : newcommentaireSignature;

							fireEvent(session, ancienDocument, entry, newdateRetourSignatureLabel,
									olddateRetourSignatureLabel);
							fireEvent(session, ancienDocument, entry, newdateEnvoiSignatureLabel,
									olddateEnvoiSignatureLabel);
							fireEvent(session, ancienDocument, entry, newcommentaireSignature, oldcommentaireSignature);
						}
						continue;
					}

					fireEvent(session, ancienDocument, entry, nouveauDocumentValue, ancienDocumentValueLabel);
				}
			}
		}
	}
	
	/**
	 * Retourne la valeur de la liste, ordonnée par date croissante, au format string dd/MM/yyyy
	 * @param value
	 * @return
	 */
	private String getListCalendarValue(List<Calendar> values) {
		Collections.sort(values);
		List<String> valuesStr = new ArrayList<String>();
		for (Calendar value : values) {
			valuesStr.add(DateUtil.formatDDMMYYYYSlash((Calendar) value));
		}
		StringBuilder result = new StringBuilder("[");
		result.append(StringUtil.join(valuesStr, ", ", ""));
		result.append("]");
		return result.toString();
	}

	protected abstract void fireEvent(CoreSession session, DocumentModel ancienDossier, Entry<String, Object> entry,
			Object nouveauDossierValue, String ancienDossierValueLabel) throws ClientException;

	protected abstract Map<String, Object> getMap(DocumentModel dossierDocument) throws ClientException;

	protected abstract Set<String> getUnLoggableEntry() throws ClientException;

}
