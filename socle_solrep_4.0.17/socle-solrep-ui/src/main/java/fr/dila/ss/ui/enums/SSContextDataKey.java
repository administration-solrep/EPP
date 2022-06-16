package fr.dila.ss.ui.enums;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.criteria.FeuilleRouteCriteria;
import fr.dila.ss.api.criteria.SubstitutionCriteria;
import fr.dila.ss.ui.bean.AlerteForm;
import fr.dila.ss.ui.bean.EditionEtapeFdrDTO;
import fr.dila.ss.ui.bean.RequeteExperteDTO;
import fr.dila.ss.ui.bean.actions.CorbeilleActionDTO;
import fr.dila.ss.ui.bean.actions.ListProfilActionsDTO;
import fr.dila.ss.ui.bean.actions.NoteEtapeActionDTO;
import fr.dila.ss.ui.bean.actions.ProfilActionsDTO;
import fr.dila.ss.ui.bean.actions.RoutingActionDTO;
import fr.dila.ss.ui.bean.actions.SSNavigationActionDTO;
import fr.dila.ss.ui.bean.actualites.ActualiteRechercheForm;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.bean.fdr.NoteEtapeFormDTO;
import fr.dila.ss.ui.th.bean.DossierMailForm;
import fr.dila.ss.ui.th.bean.FicheProfilForm;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.ss.ui.th.bean.RechercheModeleFdrForm;
import fr.dila.st.ui.enums.ContextDataKey;
import fr.dila.st.ui.th.bean.UsersListForm;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

public enum SSContextDataKey implements ContextDataKey {
    ACTUALITE_RECHERCHE_FORM(ActualiteRechercheForm.class),
    ADD_BEFORE(Boolean.class),
    ALERTE_FORM(AlerteForm.class),
    BIRT_OUTPUT_FILENAME(String.class),
    BIRT_OUTPUT_FORMAT(BirtOutputFormat.class),
    BIRT_REPORT(BirtReport.class),
    BIRT_REPORT_ID(String.class),
    BIRT_REPORT_VALUES(Map.class),
    COMMENT_CONTENT(String.class),
    COMMENT_DOC(DocumentModel.class),
    COMMENT_ID(String.class),
    CORBEILLE_ACTIONS(CorbeilleActionDTO.class, "corbeilleActions"),
    CREATION_ETAPE_DTO(CreationEtapeDTO.class),
    DATE_CONNEXION(Calendar.class),
    DIRECTION_MOVE_STEP(String.class),
    DOSSIER_IS_READ(Boolean.class),
    DOSSIER_LINK_IDS(List.class),
    DOSSIER_MAIL_FORM(DossierMailForm.class),
    EDITION_ETAPE_FDR_DTO(EditionEtapeFdrDTO.class),
    ETAPE_ACTIONS(RoutingActionDTO.class, "etapeActions"),
    FEUILLE_ROUTE(DocumentModel.class),
    FEUILLE_ROUTE_CRITERIA(FeuilleRouteCriteria.class),
    HAS_RIGHT_TO_EXPORT(Boolean.class, "hasRightToExport"),
    ID_ETAPE(String.class),
    ID_MIGRATION_LOGGER(String.class),
    ID_MINISTERE_ATTRIBUTAIRE(String.class),
    ID_MODELE(String.class),
    ID_MODELES(List.class),
    ID_POSTE(String.class),
    IS_ALERT_ACTIVATED(Boolean.class, "isAlertActivated"),
    IS_DIRECTION_COPIED(Boolean.class, "isDirectionCopied"),
    IS_EXPORT(Boolean.class),
    IS_IN_PROGRESS_STEP(Boolean.class, "isInProgressStep"),
    IS_MINISTERE_COPIED(Boolean.class, "isMinistereCopied"),
    IS_MULTI(Boolean.class, "isMulti"),
    IS_POSTE_COPIED(Boolean.class, "isPosteCopied"),
    IS_STAT_GRAPH(Boolean.class, "isStatistiqueGraphique"),
    IS_US_COPIED(Boolean.class, "isUniteStructurelleCopied"),
    LIST_MODELE_FDR(ModeleFDRListForm.class),
    LIST_PROFIL_ACTIONS(ListProfilActionsDTO.class, "listProfilActions"),
    LIST_USERS_FORM(UsersListForm.class),
    MINISTERE_ID(String.class),
    MODELE_FDR_LIST(ModeleFDRList.class),
    MODELE_FORM(ModeleFdrForm.class, "modeleForm"),
    NAVIGATION_ACTIONS(SSNavigationActionDTO.class, "navigationActions"),
    NOTE_ETAPE_ACTIONS(NoteEtapeActionDTO.class, "noteEtapeActions"),
    NOTE_ETAPE_FORM(NoteEtapeFormDTO.class),
    OTHER_PARAMETER(String.class, "otherParameter"),
    OUTPUTSTREAM(OutputStream.class),
    PROFIL_ACTIONS(ProfilActionsDTO.class, "profilActions"),
    PROFIL_FORM(FicheProfilForm.class),
    REQUETE_EXPERTE_DTO(RequeteExperteDTO.class),
    ROUTE_STEP_IDS(List.class),
    SEARCH_MODELEFDR_FORM(RechercheModeleFdrForm.class),
    STAT_ID(String.class),
    STEP_DOC(DocumentModel.class),
    SUBSTITUTION_CRITERIA(SubstitutionCriteria.class),
    TYPE_ETAPE(String.class);

    private final Class<?> valueType;
    private final String specificKey;

    SSContextDataKey(Class<?> valueType) {
        this(valueType, null);
    }

    SSContextDataKey(Class<?> valueType, String specificKey) {
        this.valueType = valueType;
        this.specificKey = specificKey;
    }

    @Override
    public String getName() {
        return StringUtils.defaultIfBlank(specificKey, name());
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }
}
