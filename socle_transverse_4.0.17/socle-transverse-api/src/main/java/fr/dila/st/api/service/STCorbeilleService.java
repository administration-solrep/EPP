package fr.dila.st.api.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

/**
 * Service qui permet de gérer les corbeilles utilisateur / postes, c'est à dire la recherche de dossiers via leur
 * distribution (DossierLink).
 *
 * @author jtremeaux
 */
public interface STCorbeilleService extends Serializable {
    /**
     * Recherche tous les DossiersLink visibles par l'utilisateur pour un dossier.
     *
     * @param session
     *            Session
     * @param dossierId
     *            Identifiant technique du document dossier
     * @return Liste de DossierLink
     *
     */
    List<DocumentModel> findDossierLink(CoreSession session, String dossierId);

    List<DocumentModel> findDossierLink(CoreSession session, Collection<String> dossierIds, PrefetchInfo prefetch);

    /**
     * Détermine l'ensemble des ID techniques des mailbox des destinataires d'un CaseLink
     *
     * @param caseLinkDoc
     *            Document CaseLink
     * @return Ensemble d'ID techniques de mailbox
     */
    Set<String> getMailboxIdSet(DocumentModel caseLinkDoc);

    /**
     * get Dosier link from step Id
     *
     * @param session
     * @param stepId
     *            the step Id
     *
     */
    DocumentModel getDossierLink(CoreSession session, String stepId);

    /**
     * Recherche les DossiersLink correspondant à la distribution d'un dossier dans une des Mailbox passées en
     * paramètre.
     *
     * @param session
     *            Session
     * @param dossierId
     *            Identifiant technique du document dossier
     * @param mailboxIdList
     *            Collection d'identifiant de mailbox ("poste-1234")...
     * @return Liste de DossierLink
     */
    List<DocumentModel> findDossierLinkInMailbox(
        CoreSession session,
        String dossierId,
        Collection<String> mailboxIdList
    );

    /**
     * Recherche les DossiersLink correspondant à la distribution d'un dossier dans une Mailbox quelconque, c'est-à-dire
     * les distributions d'un dossier dans les mailbox de l'utilisateur.
     *
     * @param session
     *            Session
     * @param dossierId
     *            Identifiant technique du document dossier
     * @return Liste de DossierLink
     */
    List<DocumentModel> findDossierLinkUnrestricted(CoreSession session, String dossierId);

    /**
     * Retourne la liste des labels des etapes courantes d'un dossier
     *
     * @param session
     * @param dossierId
     * @return
     *
     */
    List<String> findCurrentStepsLabel(CoreSession session, String dossierId);

    /**
     * Recherche les DossiersLink correspondant à la distribution des dossiers dans une des Mailbox passées en
     * paramètre.
     * @param session
     * @param dossiersDocsIds
     * @param mailboxIdList
     * @return
     *
     */
    List<DocumentModel> findDossierLinkInMailbox(
        CoreSession session,
        List<String> dossiersDocsIds,
        Collection<String> mailboxIdList
    );

    /**
     * Recherche les DossiersLink correspondant à la distribution des dossiers dans une Mailbox quelconque, c'est-à-dire
     * les distributions des dossiers dans les mailbox de l'utilisateur.
     *
     * @param session
     *            Session
     * @param dossiersDocsIds
     *            Identifiant technique du document dossier
     * @return Liste de DossierLink
     */
    List<DocumentModel> findDossierLinkUnrestricted(CoreSession session, List<String> dossiersDocsIds);

    /**
     * Recherche les Dossier Link lié aux postes saisient en paramètres
     *
     * @param session
     *            Session
     * @param posteId
     *            Identifiant du poste
     * @return Liste des dossier link
     */
    List<DocumentModel> findDossierLinkFromPoste(CoreSession session, Collection<String> posteId);

    int countDossierLinksForPostes(CoreSession session, Collection<String> postesId, String... predicates);
}
