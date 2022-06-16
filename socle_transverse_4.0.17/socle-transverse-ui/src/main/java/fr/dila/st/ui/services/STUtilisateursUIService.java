package fr.dila.st.ui.services;

import fr.dila.st.api.user.STUser;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface STUtilisateursUIService {
    STUsersList getListeUtilisateurs(SpecificContext context);

    UserForm getUtilisateur(SpecificContext context);

    UserForm mapDocToUserForm(DocumentModel doc, SpecificContext context, boolean fullUser);

    void updateDocWithUserForm(STUser user, UserForm form);

    /**
     * Récupère les suggestions d'utilisateurs pour la page de gestion des notifications.
     *
     * @param pattern chaine entrée par l'utilisateur
     * @return liste de SuggestionDTO.
     */
    List<SuggestionDTO> getNotificationUserSuggestions(String pattern);

    void validateUserForm(SpecificContext context);
}
