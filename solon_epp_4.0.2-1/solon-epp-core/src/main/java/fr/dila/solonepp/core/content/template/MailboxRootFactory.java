package fr.dila.solonepp.core.content.template;

import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.content.template.factories.SimpleTemplateBasedFactory;

/**
 * Factory de content template qui crée le document MailboxRoot.
 *
 * @author jtremeaux
 */
public class MailboxRootFactory extends SimpleTemplateBasedFactory {

    @Override
    public void createContentStructure(DocumentModel eventDoc) {
        super.createContentStructure(eventDoc);

        // Sauvegarde la session pour pouvoir requeter sur MailboxRoot...
        session.save();

        // Crée automatiquement les Mailbox des institutions
        final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();
        mailboxInstitutionService.createAllMailboxInstitution(session, eventDoc);
    }
}
