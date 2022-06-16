package fr.dila.ss.ui.services;

import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.ui.bean.FicheProfilDTO;
import fr.dila.st.ui.bean.PageProfilDTO;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Optional;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public interface ProfilUIService {
    void computeProfilActions(SpecificContext context);

    DocumentModelList getProfiles(SpecificContext context, String searchString);

    Boolean isSearchOverflow(SpecificContext context);

    void resetProfiles(SpecificContext context);

    DocumentModel getProfilDoc(SpecificContext context);

    Optional<DocumentModel> getOptionalProfilDoc(SpecificContext context);

    void createProfile(SpecificContext context);

    void deleteProfile(SpecificContext context, String profileName);

    void updateProfile(SpecificContext context);

    void validateProfileName(SpecificContext context, String profileName);

    /**
     * Controle l'accès à la vue correspondante
     *
     */
    boolean isAccessAuthorized(STPrincipal currentUser);

    /**
     * Retourne vrai si l'utilisateur peut créer des profils.
     *
     * @return Vrai si l'utilisateur peut créer des profils
     */
    boolean getAllowCreateProfile(SpecificContext context);

    boolean getAllowDeleteProfile(SpecificContext context);

    boolean getAllowEditProfile(SpecificContext context);

    List<SelectValueDTO> getAllFunctions();

    PageProfilDTO getPageProfilDTO(SpecificContext context);

    FicheProfilDTO getFicheProfilDTO(SpecificContext context);

    /**
     * Permet de renseigné les actions autorisé sur la page de listage
     * des profiles dans le context
     *
     * @param context : le context
     */
    void computeListProfilActions(SpecificContext context);
}
