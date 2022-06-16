package fr.dila.cm.cases;

import fr.dila.cm.mailbox.Mailbox;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author arussel
 */
public interface HasParticipants extends Serializable {
    /**
     * Adds to the list of recipients.
     *
     * @param recipients A map keyed with the message type and valued with a
     *            list of {@link Mailbox}
     */
    void addParticipants(Map<String, List<String>> recipients);

    /**
     * Adds to the list of initial internal recipients.
     *
     * @param recipients A map keyed with the message type and valued with a
     *            list of {@link Mailbox}
     */
    void addInitialInternalParticipants(Map<String, List<String>> recipients);

    /**
     * Adds to the list of initial external recipients.
     *
     * @param recipients A map keyed with the message type and valued with a
     *            list of {@link Mailbox}
     */
    void addInitialExternalParticipants(Map<String, List<String>> recipients);

    /**
     * Gets the list of all recipients keyed by type.
     */
    Map<String, List<String>> getAllParticipants();

    /**
     * Gets the list of initial internal recipients keyed by type.
     */
    Map<String, List<String>> getInitialInternalParticipants();

    /**
     * Gets the list of initial external recipients keyed by type.
     */
    Map<String, List<String>> getInitialExternalParticipants();
}
