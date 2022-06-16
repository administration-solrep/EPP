package fr.dila.st.api.caselink;

import fr.dila.cm.cases.HasParticipants;
import fr.dila.st.api.dossier.STDossier;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * DossierLink Interface (herit CaseLink Interface)
 *
 * @author ARN
 */
public interface STDossierLink extends HasParticipants, Serializable {
    enum CaseLinkState {
        draft,
        project,
        todo,
        done
    }

    enum CaseLinkTransistion {
        toProject,
        toDone
    }

    /**
     * Gets the document model.
     */
    DocumentModel getDocument();

    /**
     * Gets the id of the post.
     */
    String getId();

    /**
     * Gets the subject.
     */
    String getSubject();

    /**
     * Gets the comment.
     */
    String getComment();

    /**
     * Gets the date.
     */
    Calendar getDate();

    /**
     * Gets the sender.
     */
    String getSender();

    /**
     * Gets the sender mailbox id.
     */
    String getSenderMailboxId();

    /**
     * The mail envelope sent.
     */
    <T extends STDossier> T getDossier(CoreSession session, Class<T> adaptClass);

    /**
     * Gets the send date of the post.
     */
    Date getSentDate();

    /**
     * Returns true if this message has been read.
     */
    boolean isRead();

    /**
     * Persists the post.
     */
    void save(CoreSession session);

    void setActionnable(boolean actionnable);

    boolean isActionnable();

    Calendar getDateDebutValidation();

    void setDateDebutValidation(Calendar dateDebutValidation);

    /////////////////////////////////////////////////////////

    // *************************************************************
    // Distribution
    // *************************************************************
    /**
     * Retourne la Mailbox de distribution du DossierLink
     */
    String getDistributionMailbox();

    // *************************************************************
    // Dossier
    // *************************************************************
    /**
     * Retourne l'identifiant technique du dossier.
     *
     * @return Identifiant technique du dossier
     */
    String getDossierId();

    // *************************************************************
    // Étapes de feuille de route
    // *************************************************************
    /**
     * Retourne l'identifiant technique de l'étape en cours.
     *
     * @return Identifiant technique de l'étape en cours
     */
    String getRoutingTaskId();

    /**
     * Renseigne l'identifiant technique de l'étape en cours.
     *
     * @param routingTaskId Identifiant
     *            technique de l'étape en cours
     */
    void setRoutingTaskId(String routingTaskId);

    /**
     * Retourne le type d'étape en cours.
     *
     * @return Type d'étape en cours
     */
    String getRoutingTaskType();

    /**
     * Renseigne le type d'étape en cours.
     *
     * @param routingTaskType
     *            Type d'étape en cours
     */
    void setRoutingTaskType(String routingTaskType);

    /**
     * Retourne le libellé de l'étape en cours.
     *
     * @return Libellé de l'étape en cours
     */
    String getRoutingTaskLabel();

    /**
     * Renseigne le libellé de l'étape en cours.
     *
     * @param routingTaskLabel Libellé
     *            de l'étape en cours
     */
    void setRoutingTaskLabel(String routingTaskLabel);

    /**
     * Retourne le libellé de la Mailbox de distribution (champ dénormalisé).
     *
     * @return Libellé de la Mailbox de distribution
     */
    String getRoutingTaskMailboxLabel();

    /**
     * Renseigne le libellé de la Mailbox de distribution (champ dénormalisé).
     *
     * @param routingTaskMailboxLabel Libellé
     *            de la Mailbox de distribution
     */
    void setRoutingTaskMailboxLabel(String routingTaskMailboxLabel);

    /**
     *
     * Gets the CurrentStepIsMailSendProperty.
     */
    Boolean getCurrentStepIsMailSendProperty();

    void setCurrentStepIsMailSendProperty(Boolean currentStepIsMailSendProperty);

    /**
     * set the value of the property cslk:isRead
     */
    void setReadState(Boolean isRead);
}
