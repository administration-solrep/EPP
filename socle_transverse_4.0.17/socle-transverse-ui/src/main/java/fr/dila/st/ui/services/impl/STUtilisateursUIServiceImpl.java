package fr.dila.st.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.USERS_LIST_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.USER_ID;
import static fr.dila.st.ui.utils.ValidationHelper.date;
import static fr.dila.st.ui.utils.ValidationHelper.email;
import static fr.dila.st.ui.utils.ValidationHelper.future;
import static fr.dila.st.ui.utils.ValidationHelper.matchRegex;
import static fr.dila.st.ui.utils.ValidationHelper.notBlank;
import static fr.dila.st.ui.utils.ValidationHelper.notEmpty;

import com.google.common.collect.Lists;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.services.STUtilisateursUIService;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public class STUtilisateursUIServiceImpl implements STUtilisateursUIService {
    private static final String VALID_CHARS_PATTERN = "^[\\w\\-\\.@]{8,}$";

    OrganigrammeService getOrganigrammeService() {
        return STServiceLocator.getOrganigrammeService();
    }

    protected UserForm getUserForm(SpecificContext context, STUser userDoc, boolean fullUser) {
        NuxeoPrincipal principal = context.getSession().getPrincipal();
        UserForm user = new UserForm();

        user.setNom(userDoc.getLastName());
        user.setPrenom(userDoc.getFirstName());
        user.setUtilisateur(userDoc.getUsername());
        user.setMel(userDoc.getEmail());
        user.setDateDebut(SolonDateConverter.DATE_SLASH.format(userDoc.getDateDebut()));
        user.setMinisteres(userDoc.getMinisteres());

        // Si admin ministériel, ne renseigner la date de dernière connexion que pour les users de son ministère (users stockés en session)
        if (
            principal.isMemberOf(STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_USER_FROM_MINISTERE_VIEW) &&
            !principal.isMemberOf(STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_ALL_USERS_VIEW)
        ) {
            List<String> userInEntiteAdmin = UserSessionHelper.getUserSessionParameter(
                context,
                STUserSessionKey.LIST_USERNAME_FROM_ENTITE
            );
            if (CollectionUtils.isNotEmpty(userInEntiteAdmin) && userInEntiteAdmin.contains(userDoc.getUsername())) {
                user.setDateConnexion(userDoc.getDateDerniereConnexion());
            }
        } else {
            user.setDateConnexion(userDoc.getDateDerniereConnexion());
        }

        if (fullUser) {
            user.setAdresse(userDoc.getPostalAddress());
            user.setCivilite(userDoc.getTitle());
            user.setFonction(userDoc.getEmployeeType());
            user.setCodePostal(userDoc.getPostalCode());
            user.setDateFin(SolonDateConverter.DATE_SLASH.format(userDoc.getDateFin()));
            user.setMapPostes(fetchPosteLabels(userDoc.getPostes()));
            user.setPostes(new ArrayList<>(user.getMapPostes().values()));
            user.setProfils(new ArrayList<>(userDoc.getGroups()));
            user.setTelephone(userDoc.getTelephoneNumber());
            user.setTemporaire(userDoc.isTemporary() ? "oui" : "non");
            user.setVille(userDoc.getLocality());
        }

        return user;
    }

    @Override
    public UserForm mapDocToUserForm(DocumentModel doc, SpecificContext context, boolean fullUser) {
        if (doc == null) {
            context.getMessageQueue().addErrorToQueue("Utilisateur inconnu");
            return new UserForm();
        } else {
            STUser userDoc = doc.getAdapter(STUser.class);

            return getUserForm(context, userDoc, fullUser);
        }
    }

    @Override
    public void updateDocWithUserForm(STUser user, UserForm form) {
        user.setLastName(form.getNom());
        user.setFirstName(form.getPrenom());
        user.setEmail(form.getMel());
        user.setUsername(form.getUtilisateur());

        user.setPostalAddress(form.getAdresse());
        user.setTitle(form.getCivilite());
        user.setEmployeeType(form.getFonction());
        user.setPostalCode(form.getCodePostal());
        user.setPostes(form.getPostes());
        user.setGroups(new ArrayList<>(form.getProfils()));
        user.setTelephoneNumber(form.getTelephone());
        user.setLocality(form.getVille());

        if (user.getDateDebut() == null) {
            user.setDateDebut(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        }

        boolean isTemporary = isUserTemporary(form);
        user.setTemporary(isTemporary);
        user.setOccasional(form.isOccasionnel());

        String dateFin = form.getDateFin();
        if (isTemporary && StringUtils.isNotBlank(dateFin)) {
            user.setDateFin(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull(dateFin));
        }
    }

    private boolean isUserTemporary(UserForm form) {
        return Objects.equals(form.getTemporaire(), "oui");
    }

    private HashMap<String, String> fetchPosteLabels(List<String> posteIds) {
        return posteIds
            .stream()
            .map(id -> getOrganigrammeService().<OrganigrammeNode>getOrganigrammeNodeById(id, OrganigrammeType.POSTE))
            .collect(HashMap::new, (m, v) -> m.put(v.getId(), v.getLabel()), HashMap::putAll);
    }

    @Override
    public STUsersList getListeUtilisateurs(SpecificContext context) {
        STUserManagerUIService umActionService = STUIServiceLocator.getSTUserManagerUIService();
        umActionService.initUserContext(context);

        UserManager userManager = STServiceLocator.getUserManager();

        UsersListForm form = context.getFromContextData(USERS_LIST_FORM);

        DocumentModelList lstUsers = userManager.searchUsers(form.getRecherche());

        if (lstUsers != null) {
            return mapDocumentToUserList(lstUsers, form, context);
        }
        return new STUsersList(false);
    }

    @Override
    public UserForm getUtilisateur(SpecificContext context) {
        UserManager userManager = STServiceLocator.getUserManager();
        String userName = context.getFromContextData(USER_ID);

        if (StringUtils.isNotBlank(userName)) {
            return mapDocToUserForm(userManager.getUserModel(userName), context, true);
        }

        return new UserForm();
    }

    @Override
    public void validateUserForm(SpecificContext context) {
        UserForm form = context.getFromContextData(STContextDataKey.USER_FORM);
        Objects.requireNonNull(form, "un object du context de type [UserForm] est attendu");
        String fieldIdentifiant = "identifiant";

        notBlank(fieldIdentifiant, form.getUtilisateur());
        Boolean isCreation = context.getFromContextData(STContextDataKey.USER_CREATION);
        if (BooleanUtils.isTrue(isCreation)) {
            // vérification uniquement en création car l'identifiant peu être < 8 caractères en modification
            matchRegex(fieldIdentifiant, form.getUtilisateur(), VALID_CHARS_PATTERN);
        }
        notBlank("civilité", form.getCivilite());
        notBlank("nom", form.getNom());
        notBlank("prénom", form.getPrenom());
        notBlank("téléphone", form.getTelephone());
        notBlank("mél", form.getMel());
        email("mél", form.getMel());
        notBlank("utilisateur temporaire", form.getTemporaire());

        if (isUserTemporary(form)) {
            date("date début", form.getDateDebut());
            date("date fin", form.getDateFin());
            future("date fin", form.getDateFin(), "date début", form.getDateDebut());
        }

        notEmpty("profils", form.getProfils());
        notEmpty("postes", form.getPostes());
    }

    private STUsersList mapDocumentToUserList(DocumentModelList docList, UsersListForm form, SpecificContext context) {
        STUsersList lstUsers = new STUsersList();
        Set<String> lstLettre = new TreeSet<>();
        String selectedIndex = StringUtils.isBlank(form.getIndex()) ? "A" : form.getIndex();
        String firstLetter = null;

        for (DocumentModel doc : docList) {
            STUser userDoc = doc.getAdapter(STUser.class);
            String lettre;

            if (userDoc.getLastName() != null && userDoc.getLastName().trim().length() >= 1) {
                lettre = userDoc.getLastName().trim().substring(0, 1).toUpperCase();
                if (!lstLettre.contains(lettre)) {
                    lstLettre.add(lettre);
                    if (firstLetter == null) {
                        firstLetter = lettre;
                    }
                }

                // Si notre lettre correspond à la page sélectionnée pas de
                // souci
                if (lettre.equalsIgnoreCase(selectedIndex)) {
                    UserForm user = mapDocToUserForm(doc, context, false);
                    lstUsers.getListe().add(user);
                } else {
                    // Sinon on regarde si on n'a pas mal positionné notre
                    // page de lettre sélectionnée par rapport aux résultats de la recherche
                    boolean hasUserInFirstPage =
                        selectedIndex.compareToIgnoreCase(firstLetter) <= 0 && lettre.equals(firstLetter);
                    if (hasUserInFirstPage) {
                        UserForm user = mapDocToUserForm(doc, context, false);
                        lstUsers.getListe().add(user);
                    }
                }
            }
        }

        lstUsers.setLstLettres(Lists.newArrayList(lstLettre));

        return lstUsers;
    }

    @Override
    public List<SuggestionDTO> getNotificationUserSuggestions(String pattern) {
        UserManager userManager = STServiceLocator.getUserManager();

        return userManager
            .searchUsers(pattern)
            .stream()
            .map(userDoc -> userManager.getUserModel(userDoc.getId()).getAdapter(STUser.class))
            .filter(
                user ->
                    user.isActive() && user.getGroups().contains(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME)
            )
            .map(user -> new SuggestionDTO(user.getUsername(), user.getReversedFullName()))
            .collect(Collectors.toList());
    }
}
