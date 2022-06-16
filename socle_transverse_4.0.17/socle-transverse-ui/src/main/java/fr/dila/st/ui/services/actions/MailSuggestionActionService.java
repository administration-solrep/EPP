package fr.dila.st.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface MailSuggestionActionService {
    /**
     * Recherche des mails.
     *
     * @param input
     *            Chaine de recherche
     * @return Liste d'entrées du LDAP correspondant à des fonctions
     */
    Set<DocumentModel> getMailSuggestions(SpecificContext context, String pattern, String userSuggestionMessageId);

    /**
     * Retourne un tableau associatif contenant les propriétés d'un profil.
     */
    Map<String, String> getMailInfo(String userId);

    /**
     * Retourne un tableau associatif contenant les propriétés d'un profil.
     */
    String getMailInfoName(String userId);

    List<Map<String, Object>> getSuggestions(
        SpecificContext context,
        String input,
        Integer mailSuggestionMaxSearchResults,
        String userSuggestionMessageId
    );
}
