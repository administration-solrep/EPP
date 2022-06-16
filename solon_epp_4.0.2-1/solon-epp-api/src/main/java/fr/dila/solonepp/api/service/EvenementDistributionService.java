package fr.dila.solonepp.api.service;

import java.io.Serializable;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service de distribution des événements.
 *
 * @author jtremeaux
 */
public interface EvenementDistributionService extends Serializable {
    /**
     * Distribue l'événement à son émetteur lors de la création de l'événement.
     *
     * @param session Session
     * @param evenementDoc Document événement
     */
    void sendMessageEmetteur(CoreSession session, DocumentModel evenementDoc);

    /**
     * Distribue l'événement à son destinataire lors de la publication de l'événement.
     *
     * @param session Session
     * @param evenementDoc Document événement
     */
    void sendMessageDestinataire(CoreSession session, DocumentModel evenementDoc);

    /**
     * Distribue l'événement à ses destinataires en copie lors de la publication de l'événement.
     *
     * @param session Session
     * @param evenementDoc Document événement
     */
    void sendMessageDestinataireCopie(CoreSession session, DocumentModel evenementDoc);

    /**
     * Met à jour le message de l'émetteur pour accuser réception d'une version de l'événement.
     *
     * @param session Session
     * @param dossierDoc Document dossier
     * @param evenementDoc Document événement
     * @param versionDoc Document version
     * @param validationManuelle Validation manuelle : vérifie si toutes les versions antérieures ont un accusé de réception
     */
    void accuserReceptionMessageEmetteur(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        boolean validationManuelle
    );

    /**
     * Met à jour tous les messages après la création d'une version brouillon.
     *
     * @param session Session
     * @param evenementDoc Document événement
     * @param versionDoc Document version brouillon
     */
    void updateMessageAfterCreerBrouillon(CoreSession session, DocumentModel evenementDoc, DocumentModel versionDoc);

    /**
     * Met à jour tous les messages après la publication d'une version.
     *
     * @param session Session
     * @param dossierDoc Document dossier
     * @param evenementDoc Document événement
     * @param versionDoc Document version publiée
     */
    void updateMessageAfterPublier(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel versionDoc
    );

    /**
     * Met à jour tous les messages après la demande de validation d'une version.
     *
     * @param session Session
     * @param dossierDoc Document dossier
     * @param evenementDoc Document événement
     * @param versionDoc Document version en demande de validation
     */
    void updateMessageAfterDemanderValidation(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel versionDoc
    );

    /**
     * Met à jour tous les messages après la validation d'une version.
     * Le message de l'émetteur passe à la nouvelle version validée ou brouillon reportée.
     * Le message du destinataire et de la copie passent à la nouvelle version validée.
     *
     * @param session Session
     * @param dossierDoc Document dossier
     * @param evenementDoc Document événement
     * @param versionDoc Document version
     * @param messageType Type de message (expéditeur ou destinataire ou copie)
     */
    void updateMessageAfterValider(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        String messageType
    );

    /**
     * Met à jour les messages de l'expéditeur ou du destinataire après le rejet d'une version.
     * Le message de l'émetteur revient à la version brouillon ou publiée antérieure.
     * Le message du destinataire revient à la version publiée antérieure.
     *
     * @param session Session
     * @param dossierDoc Document dossier
     * @param evenementDoc Document événement
     * @param versionDoc Document version
     * @param messageType Type de message (expéditeur ou destinataire)
     */
    void updateMessageAfterRejeter(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        String messageType
    );

    /**
     * Met à jour les messages de l'expéditeur et du destinataire après l'abandon d'une version.
     * Le message de l'émetteur revient à la version brouillon ou publiée antérieure.
     * Le message du destinataire revient à la version publiée antérieure.
     *
     * @param session Session
     * @param dossierDoc Document dossier
     * @param evenementDoc Document événement
     * @param versionDoc Document version
     * @param messageType Type de message (expéditeur ou destinataire)
     */
    void updateMessageAfterAbandonner(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        String messageType
    );

    /**
     * Creation des jetons apres mise a jour du Visa Interne
     * @param session
     * @param dossierDoc
     * @param evenementDoc
     * @param versionDoc
     */
    void updateMessageMajVisaInterne(
        CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final DocumentModel messageDoc
    );

    /**
     * Maj de la date du message après enregistrer brouillon
     * @param session
     * @param evenementDoc
     * @param versionDoc
     */
    void updateMessageAfterEnregister(CoreSession session, DocumentModel evenementDoc, DocumentModel versionDoc);

    /**
     * Met à jour le message de l'emetteur après supprimer brouillon
     * @param session
     * @param evenementDoc
     * @param versionAnterieureDoc
     */
    void updateMessageAfterSupprimerBrouillon(
        CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    );
}
