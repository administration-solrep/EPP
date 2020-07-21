package fr.dila.st.core.caselink;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.caselink.CaseLinkImpl;
import fr.dila.cm.cases.HasParticipants;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation du Dossier link dans le socle transverse
 * 
 * @author ARN
 */
public abstract class STDossierLinkImpl extends CaseLinkImpl implements STDossierLink {

	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 7702130784858564652L;

	/**
	 * Constructeur de STDossierLinkImpl.
	 * 
	 * @param doc
	 *            Document
	 */
	public STDossierLinkImpl(DocumentModel doc) {
		super(doc, doc.getAdapter(HasParticipants.class));
	}

	/**
	 * Constructeur de STDossierLinkImpl.
	 * 
	 * @param doc
	 *            Document
	 * @param recipientAdapted
	 */
	public STDossierLinkImpl(DocumentModel doc, HasParticipants recipientAdapted) {
		super(doc, recipientAdapted);
	}

	@Override
	public String getDistributionMailbox() {
		Map<String, List<String>> participantsMap = getInitialInternalParticipants();
		if (participantsMap != null) {
			// Retourne le premier participant, on n'en utilise qu'un dans SOLON
			for (List<String> participantList : participantsMap.values()) {
				return participantList.iterator().next();
			}
		}
		return null;
	}

	@Override
	public String getDossierId() {
		return PropertyUtil.getStringProperty(document, CaseLinkConstants.CASE_LINK_SCHEMA, "caseDocumentId" /*
																											 * CaseLinkConstants
																											 * .
																											 * CASE_DOCUMENT_ID_FIELD
																											 */);
	}

	@Override
	public String getRoutingTaskId() {
		return getStringProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_ID_PROPERTY);
	}

	@Override
	public void setRoutingTaskId(String routingTaskId) {
		setProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_ID_PROPERTY, routingTaskId);
	}

	@Override
	public String getRoutingTaskType() {
		return getStringProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_TYPE_PROPERTY);
	}

	@Override
	public void setRoutingTaskType(String routingTaskType) {
		setProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_TYPE_PROPERTY,
				routingTaskType);
	}

	@Override
	public String getRoutingTaskLabel() {
		return getStringProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_LABEL_PROPERTY);
	}

	@Override
	public void setRoutingTaskLabel(String routingTaskLabel) {
		setProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_LABEL_PROPERTY,
				routingTaskLabel);
	}

	@Override
	public String getRoutingTaskMailboxLabel() {
		return getStringProperty(getDossierLinkSchema(),
				STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_MAILBOX_LABEL_PROPERTY);
	}

	@Override
	public void setRoutingTaskMailboxLabel(String routingTaskMailboxLabel) {
		setProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_ROUTING_TASK_MAILBOX_LABEL_PROPERTY,
				routingTaskMailboxLabel);
	}

	// ///////////////////////////////////////
	// CurrentStepProperties Method
	// ///////////////////////////////////////

	@Override
	public Boolean getCurrentStepIsMailSendProperty() {
		return getBooleanProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_IS_MAIL_SEND_PROPERTY);
	}

	@Override
	public void setCurrentStepIsMailSendProperty(Boolean currentStepIsMailSendProperty) {
		setProperty(getDossierLinkSchema(), STDossierLinkConstant.DOSSIER_LINK_IS_MAIL_SEND_PROPERTY,
				currentStepIsMailSendProperty);
	}

	@Override
	public void setReadState(Boolean isRead) {
		PropertyUtil.setProperty(document, STSchemaConstant.CASE_LINK_SCHEMA,
				STSchemaConstant.CASE_LINK_IS_READ_PROPERTY, isRead);
	}

	/**
	 * Récupère le schéma du dossier link
	 */
	protected abstract String getDossierLinkSchema();

	// ///////////////////////////////////////
	// Generic property getter & setter
	// ///////////////////////////////////////

	protected String getStringProperty(String schema, String value) {
		try {
			return (String) document.getProperty(schema, value);
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<String> getListStringProperty(String schema, String value) {
		try {
			return (List<String>) document.getProperty(schema, value);
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	protected Long getLongProperty(String schema, String value) {
		try {
			return (Long) document.getProperty(schema, value);
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	protected void setProperty(String schema, String property, Object value) {
		try {
			document.setProperty(schema, property, value);
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	protected Calendar getDateProperty(String schema, String value) {
		try {
			return (Calendar) document.getProperty(schema, value);
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	protected Boolean getBooleanProperty(String schema, String value) {
		try {
			return (Boolean) document.getProperty(schema, value);
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

}
