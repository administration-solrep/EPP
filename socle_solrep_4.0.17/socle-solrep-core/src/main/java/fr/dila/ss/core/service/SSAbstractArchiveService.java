package fr.dila.ss.core.service;

import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.SSArchiveService;
import fr.dila.ss.api.service.SSFondDeDossierService;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ZipUtil;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipOutputStream;
import javax.mail.MessagingException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.runtime.api.Framework;

public abstract class SSAbstractArchiveService implements SSArchiveService {
    private static final long serialVersionUID = -7780914517900449689L;

    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(SSAbstractArchiveService.class);

    /**
     * Default constructor
     */
    protected SSAbstractArchiveService() {}

    @Override
    public void supprimerDossier(final CoreSession session, final DocumentModel dossierDoc) {
        deleteDossier(session, dossierDoc);
        logSuppressionDossier(session, dossierDoc, null);
    }

    @Override
    public void supprimerDossier(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final NuxeoPrincipal principal
    ) {
        deleteDossier(session, dossierDoc);
        logSuppressionDossier(session, dossierDoc, principal);
    }

    private void deleteDossier(final CoreSession session, final DocumentModel dossierDoc) {
        // Chargement des services
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

        // suppression des DossierLink
        final List<DocumentModel> dossiersLink = findDossierLinkUnrestricted(session, dossierDoc.getId());
        for (final DocumentModel docLink : dossiersLink) {
            LOGGER.info(session, STLogEnumImpl.DEL_DL_TEC, docLink);
            session.removeDocument(docLink.getRef());
            session.save();
        }

        // suppression de la feuille de route
        FeuilleRoute route = documentRoutingService.getDocumentRouteForDossier(session, dossierDoc.getId());
        if (route != null) {
            LOGGER.info(session, SSLogEnumImpl.DEL_FDR_TEC, route.getDocument());
            // désormais on utilise la méthode de docRouting pour supprimer de manière "soft"
            documentRoutingService.softDeleteStep(session, route.getDocument());
        }

        // suppression du Dossier
        LOGGER.info(session, STLogEnumImpl.DEL_DOSSIER_TEC, dossierDoc);
        if ("false".equals(Framework.getProperty("solonepg.dossier.soft.delete", "true"))) {
            session.removeDocument(dossierDoc.getRef());
            session.save();
        }
    }

    protected void logSuppressionDossier(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final NuxeoPrincipal principal
    ) {
        final JournalService journalService = STServiceLocator.getJournalService();
        if (principal == null) {
            journalService.journaliserActionAdministration(
                session,
                dossierDoc,
                STEventConstant.EVENT_ARCHIVAGE_DOSSIER,
                STEventConstant.COMMENT_ARCHIVAGE_DOSSIER
            );
        } else {
            journalService.journaliserActionAdministration(
                session,
                principal,
                dossierDoc,
                STEventConstant.EVENT_ARCHIVAGE_DOSSIER,
                STEventConstant.COMMENT_ARCHIVAGE_DOSSIER
            );
        }
    }

    /**
     * Methode qui retourne les DossierLinks lié au dossier qui nécessite d'appeller le service des corbeille : cette
     * méthode est surchargée dans les applications réponse et solonepg et est utilisé dans la méthode supprimerDossier
     *
     * @return List<DocumentModel>
     */
    protected abstract List<DocumentModel> findDossierLinkUnrestricted(CoreSession session, String id);

    @Override
    public void prepareAndSendArchiveMail(
        final CoreSession documentManager,
        final List<DocumentModel> files,
        final List<String> listMail,
        final Boolean formCopieMail,
        final String formObjetMail,
        final String formTexteMail,
        final List<DocumentModel> dossiers
    )
        throws NuxeoException {
        final byte[] bytes = computeDossierArchiveBytes(documentManager, files, dossiers);

        try {
            STServiceLocator
                .getSTMailService()
                .sendArchiveMail(documentManager, listMail, formCopieMail, formObjetMail, formTexteMail, bytes);
        } catch (MessagingException e) {
            throw new NuxeoException(e);
        }

        // log de l'action
        for (final DocumentModel dossierDoc : dossiers) {
            STServiceLocator
                .getJournalService()
                .journaliserActionParapheur(
                    documentManager,
                    dossierDoc,
                    STEventConstant.EVENT_ENVOI_MAIL_DOSSIER,
                    STEventConstant.COMMENT_ENVOI_MAIL_DOSSIER
                );
        }
    }

    /**
     * Génère le zip du dossier au format archive et renvoie son contenu (byte[])
     *
     * @param files    fichiers à inclure dans le zip
     * @param dossiers les dossiers à inclure dans le zip
     */
    private byte[] computeDossierArchiveBytes(
        final CoreSession session,
        final List<DocumentModel> files,
        final List<DocumentModel> dossiers
    ) {
        Consumer<ZipOutputStream> zosConsumer = getDossierArchiveZosConsumer(session, files, dossiers);

        // now write the ZIP content to the output stream
        try {
            return ZipUtil.generateZipBytes(zosConsumer);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    /**
     * Renvoie le Consumer de ZipOutputStream capable de générer le zip au bon
     * format.
     */
    protected abstract Consumer<ZipOutputStream> getDossierArchiveZosConsumer(
        final CoreSession session,
        final List<DocumentModel> files,
        final List<DocumentModel> dossiers
    );

    protected abstract void generateCurrentDocumentPdf(
        final ZipOutputStream outputStream,
        final DocumentModel dossierDoc,
        final CoreSession session
    )
        throws IOException;

    @Override
    public List<DocumentModel> getListDocsToSend(CoreSession session, STDossier dossier) {
        final SSFondDeDossierService fondDeDossierService = SSServiceLocator.getSSFondDeDossierService();
        return fondDeDossierService.getFddDocuments(session, dossier);
    }
}
