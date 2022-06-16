package fr.dila.solonepp.core.event;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.domain.evenement.Version;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class DenormalisationVersionListener implements EventListener {

    public DenormalisationVersionListener() {
        // do nothing
    }

    @Override
    public void handleEvent(Event event) {
        if (!(event.getContext() instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext ctx = (DocumentEventContext) event.getContext();
        CoreSession session = ctx.getCoreSession();

        // Traite uniquement les modifications ou la creation de document
        if (
            !(
                event.getName().equals(DocumentEventTypes.DOCUMENT_UPDATED) ||
                event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)
            )
        ) {
            return;
        }

        // Traite uniquement les modifications de document ayant pour type Message
        DocumentModel doc = ctx.getSourceDocument();
        String docType = doc.getType();

        if (SolonEppConstant.VERSION_DOC_TYPE.equals(docType)) {
            Version version = doc.getAdapter(Version.class);

            if (
                StringUtils.isNotBlank(version.getDescription()) &&
                !version.getDescription().equals(version.getCommentaire()) ||
                StringUtils.isNotBlank(version.getCommentaire()) &&
                !version.getCommentaire().equals(version.getDescription())
            ) {
                // description modifiée
                version.setCommentaire(version.getDescription());

                session.saveDocument(doc);
                session.save();
            }
        } else {
            return;
        }
    }
}
