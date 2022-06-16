package fr.dila.cm.service;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.dossier.STDossier;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Correspondence service.
 * <p>
 * Distributes an email to users/groups/mailboxes and manages mailboxes.
 */
public interface CaseDistributionService extends Serializable {
    /**
     * Returns the received posts for given mailbox
     */
    List<STDossierLink> getReceivedCaseLinks(CoreSession coreSession, Mailbox mailbox, long offset, long limit);

    /**
     * @param session
     * @param caseDoc
     * @param mailbox the mailbox in which this case is created
     * @return an emptyCase
     */
    <T extends STDossier> T createEmptyCase(
        CoreSession session,
        DocumentModel caseDoc,
        Mailbox mailbox,
        Class<T> adapter
    );

    /**
     * Send an case to a mailbox.
     */
    STDossierLink sendCase(CoreSession session, STDossierLink postRequest);
}
