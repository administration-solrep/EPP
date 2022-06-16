package fr.dila.ss.ui.services.impl;

import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static fr.dila.st.ui.enums.STContextDataKey.PROFILES;
import static fr.dila.st.ui.enums.STContextDataKey.PROFILE_ID;
import static fr.dila.st.ui.enums.STContextDataKey.PROFILE_LISTING_MODE;
import static fr.dila.st.ui.enums.STContextDataKey.SORT_ORDER;

import fr.dila.ss.ui.bean.actions.ListProfilActionsDTO;
import fr.dila.ss.ui.bean.actions.ProfilActionsDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.ss.ui.th.bean.FicheProfilForm;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.FonctionService;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.user.BaseFunction;
import fr.dila.st.api.user.Profile;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.FicheProfilDTO;
import fr.dila.st.ui.bean.PageProfilDTO;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.directory.SizeLimitExceededException;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

public class ProfilUIServiceImpl implements ProfilUIService {
    private static final Log LOG = LogFactory.getLog(ProfilUIServiceImpl.class);

    private static final String ALL = "all";

    private static final String VALID_CHARS = "0123456789_-" + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final String CAN_DELETE_PROFILES_KEY = "canDeleteProfiles";

    private static final String SEARCH_OVERFLOW_KEY = "searchOverflow";

    public static final String PROFIL_SORT_NAME = "ordreProfil";

    public static final String COLONNE_PROFIL_TITLE = "profil.column.profil";

    public static final String FONCTION_SORT_NAME = "fonctionsAttribuees";

    public static final String PROFIL_FICHE_HEADER = "profil.fiche.header";

    @Override
    public void computeProfilActions(SpecificContext context) {
        ProfilActionsDTO profilActions = context.computeFromContextDataIfAbsent(
            SSContextDataKey.PROFIL_ACTIONS,
            ProfilActionsDTO::new
        );
        profilActions.setIsDeleteAllowed(this.getAllowDeleteProfile(context));
        profilActions.setEditAllowed(this.getAllowEditProfile(context));
    }

    @Override
    public void computeListProfilActions(SpecificContext context) {
        ListProfilActionsDTO profilActions = context.computeFromContextDataIfAbsent(
            SSContextDataKey.LIST_PROFIL_ACTIONS,
            ListProfilActionsDTO::new
        );
        profilActions.setCreateAllowed(this.getAllowCreateProfile(context));
    }

    protected String getProfileListingMode(SpecificContext context) {
        if (!context.containsKeyInContextData(PROFILE_LISTING_MODE)) {
            context.putInContextData(PROFILE_LISTING_MODE, getUserManager().getGroupListingMode());
        }
        return context.getFromContextData(PROFILE_LISTING_MODE);
    }

    @Override
    public List<SelectValueDTO> getAllFunctions() {
        return STServiceLocator
            .getProfileService()
            .findAllBaseFunction()
            .stream()
            .map(d -> d.getAdapter(BaseFunction.class))
            .map(f -> new SelectValueDTO(f.getGroupname(), f.getDescription()))
            .sorted(Comparator.comparing(SelectValueDTO::getLabel))
            .collect(Collectors.toList());
    }

    @Override
    public DocumentModelList getProfiles(SpecificContext context, String searchString) {
        DocumentModelList profiles = context.getFromContextData(PROFILES);

        context.putInContextData(SEARCH_OVERFLOW_KEY, Boolean.FALSE);
        try {
            String profileListingMode = getProfileListingMode(context);
            String trimmedSearchString = StringUtils.trim(searchString);
            final UserManager um = getUserManager();
            if (ALL.equals(profileListingMode) || "*".equals(trimmedSearchString)) {
                profiles = um.searchGroups(Collections.emptyMap(), null);
            } else if (StringUtils.isNotBlank(trimmedSearchString)) {
                Map<String, Serializable> filter = new HashMap<>();
                // XXX: search only on id, better conf should be set in user
                // manager interface
                filter.put(um.getGroupIdField(), trimmedSearchString);
                // parameters must be serializable so copy keySet to HashSet
                profiles = um.searchGroups(filter, new HashSet<>(filter.keySet()));
            }
        } catch (SizeLimitExceededException e) {
            LOG.warn("search overflow while getting profiles", e);
            context.putInContextData(SEARCH_OVERFLOW_KEY, Boolean.TRUE);
        }
        if (profiles == null) {
            profiles = new DocumentModelListImpl();
        }
        context.putInContextData(PROFILES, profiles);
        return profiles;
    }

    @Override
    public Boolean isSearchOverflow(SpecificContext context) {
        return context.getBooleanProperty(SEARCH_OVERFLOW_KEY);
    }

    @Override
    public void resetProfiles(SpecificContext context) {
        context.putInContextData(PROFILES, null);
        STServiceLocator.getProfileService().resetProfilMap();
    }

    private DocumentModel getProfile(String profileName) {
        return getUserManager().getGroupModel(profileName);
    }

    @Override
    public void createProfile(SpecificContext context) {
        FicheProfilForm form = context.getFromContextData(SSContextDataKey.PROFIL_FORM);
        Objects.requireNonNull(form, "Un form de type [FicheProfilForm] est attendu");
        DocumentModel doc = toProfilDoc(form);

        if (doc.getId() == null) {
            Framework.doPrivileged(() -> getUserManager().createGroup(doc));
            // reset so that profile list is computed again
            resetProfiles(context);
            context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString("admin.profil.added"));
        } else {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("admin.profil.alreadyPresent"));
        }
    }

    @Override
    public void deleteProfile(SpecificContext context, String profileName) {
        Framework.doPrivileged(() -> getUserManager().deleteGroup(profileName));
        resetProfiles(context);
    }

    @Override
    public void updateProfile(SpecificContext context) {
        FicheProfilForm form = context.getFromContextData(SSContextDataKey.PROFIL_FORM);
        Objects.requireNonNull(form, "Un form de type [FicheProfilForm] est attendu");

        DocumentModel doc = toProfilDoc(form);
        Framework.doPrivileged(() -> getUserManager().updateGroup(doc));
        // reset so that profile list is computed again
        resetProfiles(context);

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString("admin.profil.updated"));
    }

    @Override
    public void validateProfileName(SpecificContext context, String profileName) {
        if (!StringUtils.containsOnly(profileName, VALID_CHARS)) {
            context.getMessageQueue().addErrorToQueue("label.profileManager.wrongProfileName");
        }
    }

    @Override
    public boolean isAccessAuthorized(STPrincipal currentUser) {
        return (
            currentUser.isAdministrator() || currentUser.isMemberOf(STBaseFunctionConstant.ADMINISTRATION_PROFIL_READER)
        );
    }

    /**
     * Retourne vrai si l'utilisateur peut supprimer des profils.
     *
     * @return Vrai si l'utilisateur peut supprimer des profils
     */
    protected boolean getCanDeleteProfiles(SpecificContext context, NuxeoPrincipal currentUser) {
        Boolean canDeleteProfiles = context.getBooleanProperty(CAN_DELETE_PROFILES_KEY);
        if (canDeleteProfiles == null) {
            canDeleteProfiles =
                !getUserManager().areGroupsReadOnly() &&
                (currentUser.isAdministrator() || currentUser.isMemberOf(STBaseFunctionConstant.PROFIL_DELETER));
            context.putInContextData(CAN_DELETE_PROFILES_KEY, canDeleteProfiles);
        }
        return canDeleteProfiles;
    }

    /**
     * Retourne vrai si l'utilisateur peut éditer les profils.
     *
     * @return Vrai si l'utilisateur peut éditer les profils
     */
    protected Boolean getCanEditProfiles(SpecificContext context) {
        NuxeoPrincipal currentUser = context.getSession().getPrincipal();

        return (
            !getUserManager().areGroupsReadOnly() &&
            (currentUser.isAdministrator() || currentUser.isMemberOf(STBaseFunctionConstant.PROFIL_UPDATER))
        );
    }

    @Override
    public boolean getAllowCreateProfile(SpecificContext context) {
        NuxeoPrincipal currentUser = context.getSession().getPrincipal();

        return (!getUserManager().areGroupsReadOnly() && currentUser.isMemberOf(STBaseFunctionConstant.PROFIL_CREATOR));
    }

    @Override
    public boolean getAllowEditProfile(SpecificContext context) {
        DocumentModel selectedProfile = context.getCurrentDocument();
        Objects.requireNonNull(selectedProfile, "Un document courant de type [courant] est requis");

        final ProfileService profileService = STServiceLocator.getProfileService();
        return (
            getCanEditProfiles(context) &&
            !BaseSession.isReadOnlyEntry(selectedProfile) &&
            profileService.isProfileUpdatable(selectedProfile.getId())
        );
    }

    @Override
    public boolean getAllowDeleteProfile(SpecificContext context) {
        DocumentModel profilDoc = context.getCurrentDocument();
        Objects.requireNonNull(profilDoc, "Le document courant de type [profil] doit être défini");

        final ProfileService profileService = STServiceLocator.getProfileService();
        return (
            getCanDeleteProfiles(context, context.getSession().getPrincipal()) &&
            !BaseSession.isReadOnlyEntry(profilDoc) &&
            profileService.isProfileUpdatable(profilDoc.getId())
        );
    }

    @Override
    public PageProfilDTO getPageProfilDTO(SpecificContext context) {
        DocumentModelList modelList = getProfiles(context, "*");
        List<String> profilsDTO = convertDocumentModelListToProfilDTO(modelList);
        SortOrder tri = context.getFromContextData(SORT_ORDER);

        if (SortOrder.DESC == tri) {
            Collections.reverse(profilsDTO);
        }
        PageProfilDTO pageProfilDTO = new PageProfilDTO();
        pageProfilDTO.setProfils(profilsDTO);

        List<ColonneInfo> lstColonnes = new ArrayList<>();
        ColonneInfo colonne = new ColonneInfo(COLONNE_PROFIL_TITLE, true, PROFIL_SORT_NAME, tri);
        lstColonnes.add(colonne);
        pageProfilDTO.setLstColonnes(lstColonnes);

        return pageProfilDTO;
    }

    private List<String> convertDocumentModelListToProfilDTO(DocumentModelList docList) {
        return docList.stream().map(doc -> doc.getAdapter(Profile.class).getName()).collect(Collectors.toList());
    }

    @Override
    public DocumentModel getProfilDoc(SpecificContext context) {
        String profilId = context.getFromContextData(PROFILE_ID);
        return getOptionalProfilDoc(context)
            .orElseThrow(() -> new STValidationException("admin.profil.notFound", profilId));
    }

    @Override
    public Optional<DocumentModel> getOptionalProfilDoc(SpecificContext context) {
        String profilId = context.getFromContextData(PROFILE_ID);
        Objects.requireNonNull(profilId, "un id de profil est requis dans le context data");

        return Optional.ofNullable(getProfile(profilId));
    }

    @Override
    public FicheProfilDTO getFicheProfilDTO(SpecificContext context) {
        DocumentModel profileDoc = context.getCurrentDocument();
        Objects.requireNonNull(profileDoc, "un document courant de type [Profil] est requis");
        computeProfilActions(context);

        Profile profile = profileDoc.getAdapter(Profile.class);
        List<SelectValueDTO> fonctions = convertBaseFunctionListToDTOList(profile.getBaseFunctionList());
        SortOrder tri = context.getFromContextData(SORT_ORDER);
        if (SortOrder.DESC == tri) {
            Collections.reverse(fonctions);
        }

        List<ColonneInfo> lstColonnes = new ArrayList<>();
        ColonneInfo colonne = new ColonneInfo(PROFIL_FICHE_HEADER, true, FONCTION_SORT_NAME, tri);
        lstColonnes.add(colonne);

        FicheProfilDTO ficheProfilDTO = new FicheProfilDTO();
        ficheProfilDTO.setId(profile.getName());
        ficheProfilDTO.setLabel(profile.getName());
        ficheProfilDTO.setLstColonnes(lstColonnes);
        ficheProfilDTO.setFonctions(fonctions);
        return ficheProfilDTO;
    }

    private List<SelectValueDTO> convertBaseFunctionListToDTOList(List<String> baseFunctionList) {
        FonctionService service = STServiceLocator.getFonctionService();
        return baseFunctionList
            .stream()
            .map(
                fonctionName -> {
                    BaseFunction fonction = service.getFonction(fonctionName);
                    return new SelectValueDTO(
                        fonctionName,
                        Optional.ofNullable(fonction).map(BaseFunction::getDescription).orElse(fonctionName)
                    );
                }
            )
            .sorted(Comparator.comparing(SelectValueDTO::getLabel))
            .collect(Collectors.toList());
    }

    protected DocumentModel toProfilDoc(FicheProfilForm form) {
        if (StringUtils.isBlank(form.getId())) {
            form.setId(form.getLabel());
        }
        String profilId = form.getId();

        DocumentModel profilDoc = ObjectHelper.requireNonNullElseGet(
            getProfile(profilId),
            getUserManager()::getBareGroupModel
        );
        MapDoc2Bean.beanToDoc(form, profilDoc);

        return profilDoc;
    }
}
