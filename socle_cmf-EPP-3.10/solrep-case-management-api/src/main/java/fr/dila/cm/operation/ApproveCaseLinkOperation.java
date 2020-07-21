package fr.dila.cm.operation;

import java.util.List;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.cases.Case;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;

@Operation(id = ApproveCaseLinkOperation.ID, category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY, label = "Approve Case Links from Mailboxes", description = "Approve case link; this operation takes as an input a list of docs where the first doc is the mailbox and the second is the case to approve")
public class ApproveCaseLinkOperation {

	public static final String			ID	= "Case.Management.Approve.CaseLink";

	@Context
	protected OperationContext			context;

	@Context
	protected CoreSession				session;

	@Context
	protected CaseDistributionService	caseDistributionService;

	@OperationMethod
	public DocumentModel approveCaseLink(DocumentModelList docs) throws ClientException {
		DocumentModel mailboxDoc = docs.get(0);
		DocumentModel kaseDoc = docs.get(1);
		Case kase = kaseDoc.getAdapter(Case.class);
		Mailbox mailbox = mailboxDoc.getAdapter(Mailbox.class);
		if (kase == null || mailbox == null) {
			return null;
		}
		List<CaseLink> caseLinks = caseDistributionService.getCaseLinks(session, mailbox, kase);
		for (CaseLink caseLink : caseLinks) {
			ActionableCaseLink acl = ((DocumentModel) caseLink.getDocument()).getAdapter(ActionableCaseLink.class);
			acl.validate(session);
		}
		return mailboxDoc;
	}
}
