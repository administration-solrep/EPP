package fr.dila.cm.core.persister;

import fr.dila.cm.service.CaseManagementPersister;
import fr.dila.st.api.dossier.STDossier;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public class CaseItemInCasePersister extends CaseManagementAbstractPersister implements CaseManagementPersister {

    @Override
    public String getParentDocumentPathForCaseItem(CoreSession session, STDossier kase) {
        return session.getDocument(kase.getDocument().getRef()).getPathAsString();
    }
}
