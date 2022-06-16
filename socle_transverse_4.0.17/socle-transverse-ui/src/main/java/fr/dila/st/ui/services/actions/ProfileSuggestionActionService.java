package fr.dila.st.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public interface ProfileSuggestionActionService {
    /**
     * Recherche des fonctions.
     *
     * @param pattern
     *            Chaine de recherche
     * @return Liste d'entrées correspondant à des fonctions
     */
    List<DocumentModel> getGroupsSuggestions(SpecificContext context, String pattern);

    Object getSuggestions(SpecificContext context, String input, String userSuggestionSearchType);

    Map<String, Object> getPrefixedUserInfo(String id);

    /**
     * Retourne un tableau associatif contenant les propriétés d'un profil.
     *
     * @param id
     *            Nom du profil (ex. "UserCreator"
     * @return Propriétés
     */
    Map<String, Object> getUserInfo(String id);

    /**
     * Retourne s'il faut affiche les profils pour l'utilisateur actuel
     *
     * @param principal
     * @param profil
     * @return
     */
    boolean filterProfilToDisplay(NuxeoPrincipal principal, String profil);
}
