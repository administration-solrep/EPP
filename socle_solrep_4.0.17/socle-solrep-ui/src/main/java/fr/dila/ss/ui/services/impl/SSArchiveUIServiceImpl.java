package fr.dila.ss.ui.services.impl;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.ss.api.service.SSArchiveService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSArchiveUIService;
import fr.dila.ss.ui.th.bean.DossierMailForm;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.actions.MailSuggestionActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class SSArchiveUIServiceImpl implements SSArchiveUIService {
    protected static final String ERROR_DOSSIER_MAIL = "mail.envoi.fail";
    protected static final String OK_DOSSIER_MAIL = "mail.envoi.success";

    private static final STLogger LOGGER = STLogFactory.getLog(SSArchiveUIServiceImpl.class);

    @Override
    public void envoyerMailDossier(SpecificContext context) {
        CoreSession session = context.getSession();
        String dossierId = context.getFromContextData(STContextDataKey.DOSSIER_ID);
        DossierMailForm formMail = context.getFromContextData(SSContextDataKey.DOSSIER_MAIL_FORM);

        final List<String> listMail = new ArrayList<>(getDestinataireMail(formMail.getDestinataireIds()));

        String autresDest = formMail.getAutresDestinataires();
        if (StringUtils.isNotBlank(autresDest)) {
            listMail.addAll(Arrays.asList(autresDest.split(";")));
        }

        final SSArchiveService archiveService = SSServiceLocator.getSSArchiveService();
        final DocumentModel dossierDoc = session.getDocument(new IdRef(dossierId));
        final STDossier dossier = dossierDoc.getAdapter(STDossier.class);

        try {
            archiveService.prepareAndSendArchiveMail(
                session,
                archiveService.getListDocsToSend(session, dossier),
                listMail,
                formMail.getEtreEnCopie(),
                formMail.getObjet(),
                formMail.getMessage(),
                Collections.singletonList(dossierDoc)
            );
            context.getMessageQueue().addInfoToQueue(ResourceHelper.getString(OK_DOSSIER_MAIL));
        } catch (final Exception exc) {
            context.getMessageQueue().addWarnToQueue(ResourceHelper.getString(ERROR_DOSSIER_MAIL));
            LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_FONC, exc);
        }
    }

    protected List<String> getDestinataireMail(List<String> destinataireIds) {
        MailSuggestionActionService mailSuggestionActionService = getRequiredService(MailSuggestionActionService.class);
        return destinataireIds.stream().map(mailSuggestionActionService::getMailInfoName).collect(Collectors.toList());
    }
}
