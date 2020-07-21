package fr.dila.st.core.service;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELException;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.DeletedDocumentModel;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.audit.api.ExtendedInfo;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.impl.ExtendedInfoImpl;
import org.nuxeo.ecm.platform.audit.service.NXAuditEventsService;
import org.nuxeo.ecm.platform.audit.service.extension.AdapterDescriptor;
import org.nuxeo.ecm.platform.audit.service.extension.ExtendedInfoDescriptor;
import org.nuxeo.ecm.platform.el.ExpressionContext;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.STUserService;

/**
 * surcharge du service NXAuditEvents : utilisé pour stocker en dur le nom de l'utilisateur dans le journal et limiter
 * la taille du commentaire.
 * 
 * @author arolin
 */
public class STAuditEventsService extends NXAuditEventsService {

	private static final Log	LOG					= LogFactory.getLog(STAuditEventsService.class);

	private static final String	RECEIVED_EVENT		= "received event ";
	private static final String	WITH_NULL_PRINCIPAL	= " with null principal";
	private static final String	PROPERTY_COMMENT	= "comment";
	private static final String	PROPERTY_CATEGORY	= "category";

	private static final int	MAX_SIZE_COMMENT	= 1024;

	/**
	 * Default constructor
	 */
	public STAuditEventsService() {
		super();
	}

	@Override
	protected void logDocumentEvent(EntityManager entityManager, String eventName, DocumentEventContext docCtx,
			Date eventDate) {
		/**
		 * Code nuxeo : stockage des informations du document model.
		 */
		DocumentModel document = docCtx.getSourceDocument();
		Principal principal = docCtx.getPrincipal();
		Map<String, Serializable> properties = docCtx.getProperties();

		LogEntry entry = newLogEntry();
		entry.setEventId(eventName);
		entry.setEventDate(eventDate);
		if (document == null) {
			LOG.warn(RECEIVED_EVENT + eventName + " with null document");
		} else {
			entry.setDocUUID(document.getId());
			entry.setDocType(document.getType());
		}

		/**
		 * Surcharge : on limite la taille du commentaire à 1024 caractères.
		 */
		setCommentInEntry(entry, properties);

		// note : on n'utilise pas le cycle de vie donc on ne cherche pas à enregistrer sa valeur

		String category = (String) properties.get(PROPERTY_CATEGORY);
		if (category == null) {
			entry.setCategory("eventDocumentCategory");
		} else {
			entry.setCategory(category);
		}

		setUserNameInEntry(principal, entry, eventName);

		doPutExtendedInfos(entry, docCtx, document, principal);

		addLogEntry(entityManager, entry);
	}

	@Override
	protected void doPutExtendedInfos(LogEntry entry, EventContext eventContext, DocumentModel source,
			Principal principal) {
		if (source instanceof DeletedDocumentModel) {
			// nothing to log ; it's a light doc
			return;
		}

		if (eventContext.hasProperty("idDossier")) {
			String idDossier = (String) eventContext.getProperty("idDossier");
			entry.setDocUUID(idDossier);
			entry.setDocType("Dossier");
		}
		ExpressionContext context = new ExpressionContext();
		if (eventContext != null) {
			expressionEvaluator.bindValue(context, "message", eventContext);
		}
		if (source != null) {
			expressionEvaluator.bindValue(context, "source", source);
			// inject now the adapters
			for (AdapterDescriptor ad : documentAdapters) {
				Object adapter = null;
				try {
					adapter = source.getAdapter(ad.getKlass());
				} catch (Exception e) {
					log.debug(String.format("can't get adapter for %s to log extinfo: %s", source.getPathAsString(),
							e.getMessage()));
				}
				if (adapter != null) {
					expressionEvaluator.bindValue(context, ad.getName(), adapter);
				}
			}
		}
		if (principal != null) {
			expressionEvaluator.bindValue(context, "principal", principal);
		}

		Map<String, ExtendedInfo> extendedInfos = entry.getExtendedInfos();
		for (ExtendedInfoDescriptor descriptor : extendedInfoDescriptors) {
			String exp = descriptor.getExpression();
			Serializable value = null;
			try {
				value = expressionEvaluator.evaluateExpression(context, exp, Serializable.class);
			} catch (ELException e) {
				continue;
			}
			if (value == null) {
				continue;
			}
			extendedInfos.put(descriptor.getKey(), newExtendedInfo(value));
		}
	}

	@Override
	protected void logMiscEvent(EntityManager em, String eventName, EventContext ctx, Date eventDate) {
		/**
		 * Code nuxeo : stockage des informations de l'event.
		 */
		Principal principal = ctx.getPrincipal();
		Map<String, Serializable> properties = ctx.getProperties();

		LogEntry entry = newLogEntry();
		entry.setEventId(eventName);
		entry.setEventDate(eventDate);
		setUserNameInEntry(principal, entry, eventName);
		setCommentInEntry(entry, properties);

		String category = (String) properties.get(PROPERTY_CATEGORY);
		entry.setCategory(category);

		doPutExtendedInfos(entry, ctx, null, principal);

		addLogEntry(em, entry);
	}

	/**
	 * Surcharge : On récupère l'utilisateur et on le stocke en dur dans le journal.
	 * 
	 * @param principal
	 * @param entry
	 * @param eventName
	 */
	private void setUserNameInEntry(Principal principal, LogEntry entry, String eventName) {
		if (principal != null && principal.getName() != null && !principal.getName().isEmpty()) {
			String name = principal.getName();

			Map<String, ExtendedInfo> infos = new HashMap<String, ExtendedInfo>();
			ExtendedInfo info = ExtendedInfoImpl.createExtendedInfo(name);
			infos.put("login", info);
			entry.setExtendedInfos(infos);

			if (name.equals(STConstant.NUXEO_SYSTEM_USERNAME)) {

				// cas où l'opération a été lancé par le système : on ne cherche pas les postes de l'utilisateur
				entry.setPrincipalName(STConstant.SYSTEM_USERNAME);
				entry.setDocPath("");
			} else {
				try {
					STUserService sTUserService = STServiceLocator.getSTUserService();

					// récupération des postes de l'utilisateur
					String postesUser = sTUserService.getUserProfils(name);
					// WARN : on stocke les profils de l'utilisateur dans la variable 'DocPath' que l'on utilisais pas
					// auparavant et qui peut contenir jusqu'à 1024 caractères .
					if (postesUser.length() > MAX_SIZE_COMMENT) {
						postesUser = postesUser.substring(0, MAX_SIZE_COMMENT - 1);
					}
					entry.setDocPath(postesUser);
					// récupération du nom complet de l'utilisateur
					name = sTUserService.getUserFullName(name);
				} catch (Exception exc) {
					entry.setDocPath("");
					LOG.warn(exc.getMessage(), exc);
				}
				entry.setPrincipalName(name);
			}
		} else {
			LOG.warn(RECEIVED_EVENT + eventName + WITH_NULL_PRINCIPAL);
		}
	}

	/**
	 * Surcharge : on limite la taille du commentaire à 1024 caractères.
	 * 
	 * @param entry
	 * @param properties
	 */
	private void setCommentInEntry(LogEntry entry, Map<String, Serializable> properties) {
		String comment = (String) properties.get(PROPERTY_COMMENT);
		if (comment.length() > MAX_SIZE_COMMENT) {
			comment = comment.substring(0, MAX_SIZE_COMMENT - 1);
		}
		entry.setComment(comment);
	}
}
