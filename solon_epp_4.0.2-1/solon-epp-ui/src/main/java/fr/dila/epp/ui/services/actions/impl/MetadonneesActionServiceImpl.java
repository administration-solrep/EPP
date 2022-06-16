package fr.dila.epp.ui.services.actions.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_VERSION;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.IS_EDIT_MODE;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static java.util.Optional.ofNullable;

import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.actions.MetadonneesActionService;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.dto.PieceJointeDTO;
import fr.dila.solonepp.api.dto.TableReferenceDTO;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.VocabularyServiceImpl;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.utils.VocabularyUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class MetadonneesActionServiceImpl implements MetadonneesActionService {
    private static final STLogger LOGGER = STLogFactory.getLog(MetadonneesActionServiceImpl.class);

    public static final String COLUMN_NAME = "columnName";
    public static final String FULL_TABLE_REF = "fullTableRef";
    public static final String INPUT = "input";
    public static final String TABLE_REFERENCE = "tableReference";
    public static final String TITRE_PIECE_JOINTE = "titrePieceJointe";
    public static final String TYPE_EVENEMENT = "typeEvenement";
    public static final String TYPE_ORGANISME = "typeOrganisme";
    public static final String TYPE_PIECE_JOINTE = "typePieceJointe";
    public static final String XPATH = "xpath";

    public static final String LAYOUT_MODE_CREER = "edit";
    public static final String LAYOUT_MODE_COMPLETER = "completer";
    public static final String LAYOUT_MODE_RECTIFIER = "rectifier";

    private static final String METADONNEES_ERREUR_RECUPERATION = "metadonnees.erreur.recuperation";
    private static final String METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE =
        "metadonnees.erreur.recuperation.piece.jointe";

    @Override
    public boolean isColumnVisible(SpecificContext context) {
        String columnName = context.getFromContextData(COLUMN_NAME);
        if (StringUtils.isNotBlank(columnName)) {
            // meta toujours visible
            if ("NO_RESTRICTION".equals(columnName)) {
                return true;
            }
            final Evenement evt = getCurrentEvenement(context);
            if (evt != null) {
                try {
                    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(columnName, evt);
                    boolean isVisible = true;
                    if (propertyDescriptor == null || propertyDescriptor.isHidden()) {
                        isVisible = false;
                    } else if (propertyDescriptor.isVisibility()) {
                        // vérification si le principal fait partie du sénat
                        // pour les propertyDescriptor pouvant être vu que par
                        // le sénat
                        EppPrincipal eppPrincipal = (EppPrincipal) context.getSession().getPrincipal();
                        isVisible = eppPrincipal.isInstitutionSenat();
                    }
                    return isVisible;
                } catch (NuxeoException e) {
                    logErrorRecuperation(context, evt, e);
                }
            }
        }
        return false;
    }

    @Override
    public String getCutomWidgetLabel(SpecificContext context) {
        String columnName = context.getFromContextData(COLUMN_NAME);
        if (StringUtils.isNotBlank(columnName)) {
            final Evenement evt = getCurrentEvenement(context);
            if (evt != null) {
                try {
                    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(columnName, evt);
                    if (propertyDescriptor != null && StringUtils.isNotBlank(propertyDescriptor.getLabel())) {
                        return propertyDescriptor.getLabel();
                    }
                } catch (NuxeoException e) {
                    logErrorRecuperation(context, evt, e);
                }
            }
        }
        return "";
    }

    @Override
    public boolean isColumnVisibleFicheDossier(SpecificContext context) {
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();

        String columnName = context.getFromContextData(COLUMN_NAME);
        if (StringUtils.isNotBlank(columnName)) {
            if (SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY.equals(columnName)) {
                return true;
            }
            final Evenement evt = getCurrentEvenement(context);

            Set<String> evtTypeSet = null;
            if (evt != null) {
                DocumentModel currentDossierDoc = dossierService.getDossier(context.getSession(), evt.getDossier());
                evtTypeSet = dossierService.getEvenementTypeDossierList(context.getSession(), currentDossierDoc);
            }

            if (evtTypeSet != null) {
                try {
                    return isFicheDossier(context, columnName, evtTypeSet);
                } catch (NuxeoException e) {
                    logErrorRecuperation(context, evt, e);
                }
            }
        }
        return false;
    }

    private boolean isFicheDossier(SpecificContext context, final String columnName, final Set<String> evtTypeSet) {
        for (String type : evtTypeSet) {
            Map<String, PropertyDescriptor> map = SolonEppServiceLocator.getMetaDonneesService().getMapProperty(type);
            PropertyDescriptor propertyDescriptor = map.get(columnName);
            if (propertyDescriptor != null && propertyDescriptor.isFicheDossier()) {
                if (propertyDescriptor.isVisibility()) {
                    // vérification si le principal fait partie du sénat pour
                    // les propertyDescriptor pouvant être vu que par le sénat
                    EppPrincipal eppPrincipal = (EppPrincipal) context.getSession().getPrincipal();
                    return eppPrincipal.isInstitutionSenat();
                }
                return true;
            }
        }
        return false;
    }

    private Evenement getCurrentEvenement(SpecificContext context) {
        return ofNullable(context.getCurrentDocument()).map(dm -> dm.getAdapter(Evenement.class)).orElse(null);
    }

    private PropertyDescriptor getPropertyDescriptor(final String columnName, final Evenement evt) {
        Map<String, PropertyDescriptor> map = SolonEppServiceLocator
            .getMetaDonneesService()
            .getMapProperty(evt.getTypeEvenement());
        return map.get(columnName);
    }

    private void logErrorRecuperation(SpecificContext context, final Evenement evt, NuxeoException e) {
        String message = ResourceHelper.getString(METADONNEES_ERREUR_RECUPERATION);
        LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_GET_META_DONNEE_TEC, message + evt.toString(), e);
        context.getMessageQueue().addWarnToQueue(message);
    }

    @Override
    public boolean isColumnRequired(SpecificContext context) {
        String columnName = context.getFromContextData(COLUMN_NAME);
        if (StringUtils.isNotBlank(columnName)) {
            final Evenement evt = getCurrentEvenement(context);
            if (evt != null) {
                try {
                    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(columnName, evt);
                    if (propertyDescriptor != null) {
                        return propertyDescriptor.isObligatoire();
                    }
                } catch (NuxeoException e) {
                    logErrorRecuperation(context, evt, e);
                }
            }
        }
        return false;
    }

    @Override
    public String getLibelle(SpecificContext context) {
        String typeEvenement = context.getFromContextData(TYPE_EVENEMENT);
        try {
            EvenementTypeDescriptor evenementTypeDescriptor = SolonEppServiceLocator
                .getEvenementTypeService()
                .getEvenementType(typeEvenement);
            return evenementTypeDescriptor.getLabel();
        } catch (NuxeoException e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.FAIL_GET_EVENT_TYPE_TEC,
                "Echec lors de la récupération du libellé du type d'événement : " + typeEvenement,
                e
            );
            return null;
        }
    }

    @Override
    public List<SuggestionDTO> getSuggestions(SpecificContext context) {
        String input = context.getFromContextData(EppContextDataKey.INPUT);
        String tableReference = context.getFromContextData(EppContextDataKey.TABLE_REF);
        Boolean fullTableRef = context.getFromContextData(EppContextDataKey.FULL_TABLEREF);
        String typeOrganisme = context.getFromContextData(EppContextDataKey.TYPE_ORGANISME);
        EppPrincipal eppPrincipal = (EppPrincipal) context.getSession().getPrincipal();
        try {
            return SolonEppServiceLocator
                .getTableReferenceService()
                .searchTableReference(
                    context.getSession(),
                    input,
                    tableReference,
                    eppPrincipal.getInstitutionId(),
                    fullTableRef,
                    typeOrganisme
                )
                .stream()
                .map(table -> new SuggestionDTO(table.getId(), table.getTitle()))
                .collect(Collectors.toList());
        } catch (NuxeoException e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.FAIL_GET_TABLE_REFERENCE_TEC,
                "tableReference = " + tableReference,
                e
            );
            context.getMessageQueue().addErrorToQueue("La suggestion " + tableReference + " n'a pas été trouvée");
        }

        return null;
    }

    @Override
    public Object getSuggestionsAll(SpecificContext context) {
        String input = context.getFromContextData(INPUT);
        String tableReference = context.getFromContextData(TABLE_REFERENCE);
        boolean fullTableRef =
            context.containsKeyInContextData(FULL_TABLE_REF) && (boolean) context.getFromContextData(FULL_TABLE_REF);
        String typeOrganisme = context.getFromContextData(TYPE_ORGANISME);
        try {
            return SolonEppServiceLocator
                .getTableReferenceService()
                .searchTableReference(context.getSession(), input, tableReference, null, fullTableRef, typeOrganisme);
        } catch (NuxeoException e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.FAIL_GET_TABLE_REFERENCE_TEC,
                "tableReference = " + tableReference,
                e
            );
            return null;
        }
    }

    @Override
    public String getTitleFromTableReference(SpecificContext context) {
        String identifiant = context.getFromContextData(ID);
        if (StringUtils.isBlank(identifiant)) {
            return null;
        }
        String tableReference = context.getFromContextData(TABLE_REFERENCE);
        try {
            TableReferenceDTO tableReferenceDTO = SolonEppServiceLocator
                .getTableReferenceService()
                .findTableReferenceByIdAndType(context.getSession(), identifiant, tableReference, null);
            return tableReferenceDTO != null ? tableReferenceDTO.getTitle() : "**inconnu**";
        } catch (NuxeoException e) {
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.FAIL_GET_TABLE_REFERENCE_TEC,
                "TableReference = " + tableReference + ", evenement = " + getCurrentEvenement(context),
                e
            );
            return null;
        }
    }

    @Override
    public String getLabelNatureVersion(SpecificContext context) {
        StringBuilder label = new StringBuilder();
        String nature = null;
        boolean versionCourante = false;
        Version currentVersion = context.getFromContextData(CURRENT_VERSION);
        if (currentVersion != null) {
            nature = currentVersion.getNature();
            versionCourante = currentVersion.isVersionCourante();
        }

        if (StringUtils.isNotBlank(nature)) {
            label.append(ResourceHelper.getString("metadonnees.version.nature." + nature));
        }

        if (versionCourante) {
            if (StringUtils.isNotBlank(nature)) {
                label.append(" - ");
            }
            label.append(ResourceHelper.getString("metadonnees.version.courante"));
        }

        return label.toString();
    }

    @Override
    public String getSelectedVersion(SpecificContext context) {
        Version currentVersion = context.getFromContextData(CURRENT_VERSION);

        return currentVersion.getNumeroVersion().toString();
    }

    @Override
    public List<PieceJointeDTO> getListPieceJointeFichier(SpecificContext context) {
        String idVersion = context.getFromContextData(ID);
        String typePieceJointe = context.getFromContextData(TYPE_PIECE_JOINTE);
        if (idVersion != null) {
            try {
                return SolonEppServiceLocator
                    .getPieceJointeService()
                    .findAllPieceJointeFichierByVersionAndType(typePieceJointe, idVersion, context.getSession());
            } catch (NuxeoException e) {
                String message = ResourceHelper.getString(METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE);
                LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_GET_METADONNEES_PIECE_JOINTE, message, e);
                context.getMessageQueue().addWarnToQueue(message);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<PieceJointeDTO> getDeletedListPieceJointe(SpecificContext context) {
        Version curVersion = context.getFromContextData(CURRENT_VERSION);
        Evenement curEvenement = getCurrentEvenement(context);
        String typePieceJointe = context.getFromContextData(TYPE_PIECE_JOINTE);
        if (curVersion != null && curEvenement != null) {
            try {
                return SolonEppServiceLocator
                    .getPieceJointeService()
                    .getDeletedPieceJointeList(
                        context.getSession(),
                        typePieceJointe,
                        curEvenement.getDocument(),
                        curVersion.getDocument()
                    );
            } catch (NuxeoException e) {
                String message = ResourceHelper.getString(METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE);
                LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_GET_METADONNEES_PIECE_JOINTE, message, e);
                context.getMessageQueue().addWarnToQueue(message);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isNewPieceJointe(SpecificContext context) {
        Version curVersion = context.getFromContextData(CURRENT_VERSION);
        Evenement curEvenement = getCurrentEvenement(context);
        String typePieceJointe = context.getFromContextData(TYPE_PIECE_JOINTE);
        String titrePieceJointe = context.getFromContextData(TITRE_PIECE_JOINTE);
        if (curVersion != null && curEvenement != null) {
            try {
                Set<String> listNew = SolonEppServiceLocator
                    .getPieceJointeService()
                    .getNewPieceJointeTitreList(
                        context.getSession(),
                        typePieceJointe,
                        curEvenement.getDocument(),
                        curVersion.getDocument()
                    );
                return listNew.contains(titrePieceJointe);
            } catch (NuxeoException e) {
                String message = ResourceHelper.getString(METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE);
                LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_GET_METADONNEES_PIECE_JOINTE, message, e);
                context.getMessageQueue().addWarnToQueue(message);
            }
        }
        return false;
    }

    @Override
    public String getPieceJointeType(SpecificContext context) {
        String typePieceJointe = context.getFromContextData(TYPE_PIECE_JOINTE);
        // cherche un label custom dans evenement-type-contrib, sinon dans le
        // vocabulaire
        try {
            final Evenement evt = getCurrentEvenement(context);
            if (evt != null) {
                EvenementTypeDescriptor evenementTypeDescriptor = SolonEppServiceLocator
                    .getEvenementTypeService()
                    .getEvenementType(evt.getTypeEvenement());
                Map<String, PieceJointeDescriptor> pieceJointeMap = evenementTypeDescriptor.getPieceJointe();
                PieceJointeDescriptor pieceJointeDescriptor = pieceJointeMap.get(typePieceJointe);
                if (pieceJointeDescriptor != null && StringUtils.isNotBlank(pieceJointeDescriptor.getLabel())) {
                    return pieceJointeDescriptor.getLabel();
                }
            }
        } catch (NuxeoException e) {
            return null;
        }

        String label = STServiceLocator
            .getVocabularyService()
            .getEntryLabel(SolonEppVocabularyConstant.VOCABULARY_PIECE_JOINTE_DIRECTORY, typePieceJointe);
        return VocabularyServiceImpl.UNKNOWN_ENTRY.equals(label) ? typePieceJointe : label;
    }

    @Override
    public String getDeletedUrl(SpecificContext context) {
        Version curVersion = context.getFromContextData(CURRENT_VERSION);
        Evenement curEvenement = getCurrentEvenement(context);
        String typePieceJointe = context.getFromContextData(TYPE_PIECE_JOINTE);
        if (curVersion != null && curEvenement != null) {
            try {
                return SolonEppServiceLocator
                    .getPieceJointeService()
                    .getDeletedUrl(
                        context.getSession(),
                        typePieceJointe,
                        curEvenement.getDocument(),
                        curVersion.getDocument()
                    );
            } catch (NuxeoException e) {
                String message = ResourceHelper.getString(METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE);
                LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_GET_METADONNEES_PIECE_JOINTE, message, e);
                context.getMessageQueue().addWarnToQueue(message);
            }
        }
        return "";
    }

    @Override
    public List<MetaDonneesDescriptor> getListMetadonnees() {
        return SolonEppServiceLocator.getMetaDonneesService().getAll();
    }

    @Override
    public DocumentModel getCurrentDossier(SpecificContext context) {
        Evenement evenement = getCurrentEvenement(context);
        if (evenement == null) {
            return null;
        }
        String dossierId = evenement.getDossier();
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        return dossierService.getDossier(context.getSession(), dossierId);
    }

    @Override
    public boolean notEqualsLastVersionPublieValue(SpecificContext context) {
        boolean isEditMode = (boolean) ofNullable(context.getFromContextData(IS_EDIT_MODE)).orElse(false);
        Version currentVersion = null;
        if (isEditMode) {
            currentVersion = context.getFromContextData(CURRENT_VERSION); // anciennement evenementActions.getCurrentVersionForCreation();
            if (
                currentVersion != null &&
                (currentVersion.getDocument().getId() == null || currentVersion.isEtatPublie())
            ) {
                return false;
            }
        } else {
            currentVersion = context.getFromContextData(CURRENT_VERSION);
        }
        String xpath = context.getFromContextData(XPATH);
        if (currentVersion != null) {
            List<String> modifiedMetaList = currentVersion.getModifiedMetaList();
            return modifiedMetaList.contains(xpath) || isNiveauLectureModifie(xpath, modifiedMetaList);
        }
        return false;
    }

    private boolean isNiveauLectureModifie(String xPath, List<String> metasModifiees) {
        if (xPath.contains(SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY)) {
            for (String str : metasModifiees) {
                if (str.contains(SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getNiveauLectureLabel(String niveauLectureCode) {
        return VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(
            SolonEppVocabularyConstant.NIVEAU_LECTURE_VOCABULARY,
            niveauLectureCode
        );
    }
}
