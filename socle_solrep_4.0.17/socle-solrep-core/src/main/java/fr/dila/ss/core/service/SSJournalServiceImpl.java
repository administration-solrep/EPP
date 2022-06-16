package fr.dila.ss.core.service;

import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.SSJournalService;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.JournalServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSJournalServiceImpl extends JournalServiceImpl implements SSJournalService {

    public SSJournalServiceImpl() {
        super();
    }

    @Override
    public void journaliserActionEtapeFDR(
        CoreSession session,
        SSRouteStep etape,
        DocumentModel dossierDoc,
        String eventName,
        String commentAction
    ) {
        String stepTypeId = etape.getType();
        String stepMailboxId = etape.getDistributionMailboxId();
        String comment = "";
        if (stepTypeId != null && stepMailboxId != null) {
            final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
            final MailboxService mailboxService = STServiceLocator.getMailboxService();
            String stepTypeLabel = vocabularyService.getEntryLabel(
                STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY,
                stepTypeId
            );
            String posteLabel = "";
            try {
                posteLabel = mailboxService.getMailboxTitle(session, stepMailboxId);
            } catch (Exception e) {
                posteLabel = "";
            }
            comment = commentAction + " : [" + posteLabel + "] " + stepTypeLabel;
        }
        journaliserActionFDR(session, dossierDoc, eventName, comment);
    }
}
