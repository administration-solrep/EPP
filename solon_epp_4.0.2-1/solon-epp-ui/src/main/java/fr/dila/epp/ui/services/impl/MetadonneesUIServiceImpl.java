package fr.dila.epp.ui.services.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_VERSION;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.INPUT;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.PIECE_JOINTE_LIST;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.COLUMN_NAME;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.TABLE_REFERENCE;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.TYPE_PIECE_JOINTE;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.XPATH;
import static fr.dila.st.core.service.STServiceLocator.getDownloadService;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static fr.dila.st.ui.enums.WidgetTypeEnum.FILE_MULTI;
import static fr.dila.st.ui.enums.WidgetTypeEnum.PIECE_JOINTE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.AUTEUR;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.COAUTEUR;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.COMMISSIONS;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.COMMISSION_SAISIE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.COMMISSION_SAISIE_AU_FOND;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.COMMISSION_SAISIE_POUR_AVIS;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DATE_REFUS_PROCEDURE_ENGAGEMENT_AN;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DESTINATAIRE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DESTINATAIRE_COPIE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DOSSIER_LEGISLATIF;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.EMETTEUR;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.GROUPE_PARLEMENTAIRE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.LIBELLE_ANNEXE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.NIVEAU_LECTURE_NUMERO;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.ORGANISME;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.PARLEMENTAIRE_SUPPLEANT_LIST;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.PARLEMENTAIRE_TITULAIRE_LIST;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.RAPPORTEUR_LIST;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.URL_BASE_LEGALE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.URL_DOSSIER_AN;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.URL_DOSSIER_SENAT;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.URL_PUBLICATION;
import static fr.dila.st.ui.enums.parlement.WidgetModeEnum.HIDDEN;
import static fr.dila.st.ui.enums.parlement.WidgetModeEnum.VIEW;
import static java.util.Optional.ofNullable;

import com.google.common.collect.ImmutableSet;
import fr.dila.epp.ui.bean.DetailDossier;
import fr.dila.epp.ui.enumeration.DossierMetadonneeEnum;
import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.enumeration.MetadonneeMapperEnum;
import fr.dila.epp.ui.services.MetadonneesUIService;
import fr.dila.epp.ui.services.SelectValueUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.requete.recherchechamp.Parametre;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.DocumentDTO;
import fr.dila.st.ui.bean.PieceJointeDTO;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.bean.WidgetDTO;
import fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum;
import fr.dila.st.ui.enums.parlement.WidgetModeEnum;
import fr.dila.st.ui.enums.parlement.WidgetTypeConstants;
import fr.dila.st.ui.th.bean.mapper.MapPropertyDescToWidget;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.BlobNotFoundException;

public class MetadonneesUIServiceImpl implements MetadonneesUIService {
    private static final String VALEUR = "valeur";

    public static final String IS_EDIT_MODE = "isEditMode";

    private static final STLogger LOG = STLogFactory.getLog(MetadonneesUIServiceImpl.class);
    public static final String LABEL_PREFIX = "label.epp.metadonnee.";
    private static final ImmutableSet<CommunicationMetadonneeEnum> METADONNEES_URL = ImmutableSet.of(
        URL_DOSSIER_AN,
        URL_DOSSIER_SENAT,
        URL_BASE_LEGALE,
        URL_PUBLICATION
    );

    private static final ImmutableSet<CommunicationMetadonneeEnum> METADONNEES_INSTITUTION = ImmutableSet.of(
        EMETTEUR,
        DESTINATAIRE,
        DESTINATAIRE_COPIE
    );
    private static final ImmutableSet<String> METADONNEES_IDENTITE = ImmutableSet.of(AUTEUR.getName());
    private static final ImmutableSet<String> METADONNEES_ORGANISME = ImmutableSet.of(
        ORGANISME.getName(),
        COMMISSION_SAISIE.getName(),
        COMMISSION_SAISIE_AU_FOND.getName()
    );
    public static final ImmutableSet<String> METADONNEES_IDENTITE_LIST = ImmutableSet.of(
        COAUTEUR.getName(),
        RAPPORTEUR_LIST.getName(),
        PARLEMENTAIRE_TITULAIRE_LIST.getName(),
        PARLEMENTAIRE_SUPPLEANT_LIST.getName()
    );
    public static final ImmutableSet<String> METADONNEES_ORGANISME_LIST = ImmutableSet.of(
        COMMISSIONS.getName(),
        COMMISSION_SAISIE_POUR_AVIS.getName(),
        GROUPE_PARLEMENTAIRE.getName()
    );
    private static final ImmutableSet<String> METADONNEES_LISTSIMPLE = ImmutableSet.of(
        LIBELLE_ANNEXE.getName(),
        DOSSIER_LEGISLATIF.getName()
    );
    public static final String LST_NIVEAU_LECTURE_PARAM_NAME = "lstNiveauLecture";
    public static final String LST_VALUES_PARAM_NAME = "lstValues";
    public static final String LST_SUGGEST_NAME = "lstSuggestValues";
    public static final String AUTOCOMPLETE_PARAM_NAME = "autocomplete";
    public static final String INCLURE_PARAM_NAME = "labelInclure";
    public static final String LABEL_INCLURE_MANDAT = "label.suggestion.inclure.Identite";
    public static final String LABEL_INCLURE_COMMISSION = "label.suggestion.inclure.Organisme";

    @Override
    public List<WidgetDTO> getWidgetListForCommunication(SpecificContext context) {
        List<WidgetDTO> widgets = new ArrayList<>();
        Evenement curEvenement = ofNullable(context.getCurrentDocument())
            .map(dm -> dm.getAdapter(Evenement.class))
            .orElse(null);
        if (curEvenement == null) {
            context.getMessageQueue().addErrorToQueue("Echec de récupération de l'événement courant");
            LOG.error(STLogEnumImpl.FAIL_GET_EVENT_TEC);
            return widgets;
        }
        Version curVersion = context.getFromContextData(CURRENT_VERSION);
        if (curVersion == null) {
            context.getMessageQueue().addErrorToQueue("Echec de récupération de la version courante");
            LOG.error(EppLogEnumImpl.FAIL_GET_VERSION_TEC);
            return widgets;
        }
        MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        Map<String, PropertyDescriptor> propertyMap = metaDonneesService.getMapProperty(
            curEvenement.getTypeEvenement()
        );
        widgets =
            propertyMap
                .values()
                .stream()
                .map(prop -> convertToWidget(prop, curEvenement, curVersion, context))
                .filter(w -> isWidgetDisplayed(w, context))
                .collect(Collectors.toList());

        widgets.addAll(getPieceJointeWidgets(context));

        return widgets;
    }

    @Override
    public DetailDossier getDetailDossier(SpecificContext context) {
        DetailDossier detailDossier = new DetailDossier();
        Evenement curEvenement = ofNullable(context.getCurrentDocument())
            .map(dm -> dm.getAdapter(Evenement.class))
            .orElse(null);
        if (curEvenement == null) {
            context.getMessageQueue().addErrorToQueue("Echec de récupération de l'événement courant");
            LOG.error(STLogEnumImpl.FAIL_GET_EVENT_TEC);
            return detailDossier;
        }
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        DocumentModel curDossierDoc = dossierService.getDossier(context.getSession(), curEvenement.getDossier());
        Dossier curDossier = curDossierDoc.getAdapter(Dossier.class);
        Set<String> evtTypeSet = dossierService.getEvenementTypeDossierList(context.getSession(), curDossierDoc);
        List<WidgetDTO> lstWidgets = evtTypeSet
            .stream()
            .flatMap(type -> SolonEppServiceLocator.getMetaDonneesService().getMapProperty(type).values().stream())
            .filter(property -> isVisibleFicheDossier(property, context))
            .filter(distinctByKey(PropertyDescriptor::getName))
            .map(property -> convertToFicheDossierWidget(property, curDossier, context))
            .sorted(Comparator.comparing(w -> DossierMetadonneeEnum.fromString(w.getName()).getOrder()))
            .collect(Collectors.toList());

        detailDossier.setLstWidgets(lstWidgets);

        return detailDossier;
    }

    @Override
    public SelectValueDTO getDestinataireCopie(SpecificContext context) {
        String typeEvenement = context.getFromContextData(EppContextDataKey.TYPE_EVENEMENT);
        String emetteur = context.getFromContextData(EppContextDataKey.EMETTEUR);
        String destinataire = context.getFromContextData(EppContextDataKey.DESTINATAIRE);
        String destinataireCopie = "";

        if (StringUtils.isNotEmpty(emetteur) && StringUtils.isNotEmpty(destinataire)) {
            MetaDonneesDescriptor metaDonneesDescriptor = SolonEppServiceLocator
                .getMetaDonneesService()
                .getEvenementType(typeEvenement);
            EvenementMetaDonneesDescriptor evenementMetaDonneesDescriptor = metaDonneesDescriptor.getEvenement();
            PropertyDescriptor propertyDescriptor = evenementMetaDonneesDescriptor
                .getProperty()
                .get(SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY);
            // recuperation et settage des valeurs par defaut si possible
            if (propertyDescriptor != null) {
                List<String> list = propertyDescriptor.getListInstitutions();
                list.remove(emetteur);
                list.remove(destinataire);
                if (list.size() == 1) {
                    destinataireCopie = list.get(0);
                }
            }
        }

        return new SelectValueDTO(destinataireCopie, InstitutionsEnum.getLabelFromInstitutionKey(destinataireCopie));
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @SuppressWarnings("unchecked")
    private WidgetDTO convertToWidget(
        PropertyDescriptor property,
        Evenement evenement,
        Version version,
        SpecificContext context
    ) {
        CommunicationMetadonneeEnum metaEnum = CommunicationMetadonneeEnum.fromString(property.getName());
        MetadonneeMapperEnum mapperEnum = MetadonneeMapperEnum.getMapperFromCommunicationField(metaEnum);

        boolean isEditMode = isEditMode(context);
        boolean isUserInInstit = isUserInInstit(context, property.getName());
        WidgetModeEnum widgetMode = isEditMode
            ? MapPropertyDescToWidget.getWidgetMode(property, context, isUserInInstit)
            : VIEW;
        WidgetDTO widget = MapPropertyDescToWidget.initWidget(
            property,
            context,
            isUserInInstit,
            MapPropertyDescToWidget.getTypeChamp(metaEnum, widgetMode),
            LABEL_PREFIX
        );
        context.putInContextData(
            XPATH,
            String.format(
                "%s:%s",
                ofNullable(metaEnum).map(CommunicationMetadonneeEnum::getPrefix).orElse(""),
                property.getName()
            )
        );
        widget.setModifiedInCurVersion(
            SolonEppActionsServiceLocator.getMetadonneesActionService().notEqualsLastVersionPublieValue(context)
        );
        List<Parametre> complexParametres = new ArrayList<>(
            Arrays.asList(
                new Parametre(
                    VALEUR,
                    ofNullable(mapperEnum)
                        .map(me -> me.invokeGetter(widgetMode, evenement, version))
                        .map(v -> getValueFromTableRef(property.getName(), v, context))
                        .orElse("")
                ),
                new Parametre(
                    LST_SUGGEST_NAME,
                    (Serializable) ofNullable(mapperEnum)
                        .map(me -> me.invokeGetter(widgetMode, evenement, version))
                        .map(v -> getSuggestList(property.getName(), v, context))
                        .orElse(null)
                ),
                new Parametre(
                    "institution",
                    METADONNEES_INSTITUTION.contains(metaEnum)
                        ? mapperEnum.getValueGetterFunction().apply(evenement, version)
                        : null
                )
            )
        );
        if (WidgetTypeConstants.MULTIPLE_DATE.equals(widget.getTypeChamp())) {
            complexParametres.add(
                new Parametre(
                    LST_VALUES_PARAM_NAME,
                    (List<String>) ofNullable(mapperEnum)
                        .map(me -> me.invokeGetter(widgetMode, evenement, version))
                        .orElse("")
                )
            );
        }
        if (isEditMode) {
            complexParametres.addAll(getAdditionalParametres(metaEnum, version, context));
        }
        widget.getParametres().addAll(complexParametres);
        return widget;
    }

    private List<Parametre> getAdditionalParametres(
        CommunicationMetadonneeEnum metaEnum,
        Version version,
        SpecificContext context
    ) {
        List<Parametre> parametres = new ArrayList<>();
        SelectValueUIService selectValueUIService = SolonEppUIServiceLocator.getSelectValueUIService();
        switch (metaEnum) {
            case EMETTEUR:
            case DESTINATAIRE:
                context.putInContextData(INPUT, metaEnum.getName());
                parametres.add(
                    new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getSelectableInstitutions(context))
                );
                break;
            case TYPE_LOI:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllTypesLoi()));
                break;
            case NATURE_LOI:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllNaturesLoi()));
                break;
            case NATURE:
            case NATURE_RAPPORT:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllNaturesRapport()));
                break;
            case ATTRIBUTION_COMMISSION:
                parametres.add(
                    new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllAttributionsCommission())
                );
                break;
            case SORT_ADOPTION:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllSortsAdoption()));
                break;
            case RESULTAT_CMP:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllResultatsCMP()));
                break;
            case MOTIF_IRRECEVABILITE:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllMotifsIrrecevabilite()));
                break;
            case TYPE_ACTE:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllTypesActe()));
                break;
            case SENS_AVIS:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllSensAvis()));
                break;
            case RAPPORT_PARLEMENT:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllRapportsParlement()));
                break;
            case DECISION_PROC_ACC:
                parametres.add(new Parametre(LST_VALUES_PARAM_NAME, selectValueUIService.getAllDecisionsProcAcc()));
                break;
            case RUBRIQUE:
                parametres.add(new Parametre(AUTOCOMPLETE_PARAM_NAME, Boolean.TRUE.toString()));
                break;
            case AUTEUR:
            case COAUTEUR:
            case RAPPORTEUR_LIST:
            case PARLEMENTAIRE_TITULAIRE_LIST:
            case PARLEMENTAIRE_SUPPLEANT_LIST:
                parametres.add(new Parametre(AUTOCOMPLETE_PARAM_NAME, Boolean.TRUE.toString()));
                parametres.add(new Parametre(INCLURE_PARAM_NAME, LABEL_INCLURE_MANDAT));
                break;
            case ORGANISME:
            case COMMISSION_SAISIE:
            case COMMISSION_SAISIE_AU_FOND:
            case COMMISSIONS:
            case COMMISSION_SAISIE_POUR_AVIS:
            case GROUPE_PARLEMENTAIRE:
                parametres.add(new Parametre(AUTOCOMPLETE_PARAM_NAME, Boolean.TRUE.toString()));
                parametres.add(new Parametre(INCLURE_PARAM_NAME, LABEL_INCLURE_COMMISSION));
                break;
            case NIVEAU_LECTURE:
                parametres.add(
                    new Parametre(
                        "niveauLectureNumero",
                        ofNullable(version.getNiveauLectureNumero()).map(n -> n.toString()).orElse("")
                    )
                );
                parametres.add(
                    new Parametre(LST_NIVEAU_LECTURE_PARAM_NAME, selectValueUIService.getAllNiveauxLecture())
                );
                break;
            default:
                break;
        }
        return parametres;
    }

    private boolean isUserInInstit(SpecificContext context, String propertyName) {
        EppPrincipal eppPrincipal = (EppPrincipal) context.getSession().getPrincipal();
        return (
            URL_DOSSIER_AN.getName().equals(propertyName) &&
            !eppPrincipal.isInstitutionAn() ||
            URL_DOSSIER_SENAT.getName().equals(propertyName) &&
            !eppPrincipal.isInstitutionSenat()
        );
    }

    private WidgetDTO convertToFicheDossierWidget(
        PropertyDescriptor property,
        Dossier dossier,
        SpecificContext context
    ) {
        WidgetDTO widget = new WidgetDTO();
        widget.setName(property.getName());
        widget.setLabel(LABEL_PREFIX + property.getName());
        widget.setTypeChamp(
            METADONNEES_URL
                    .stream()
                    .map(CommunicationMetadonneeEnum::getName)
                    .collect(Collectors.toList())
                    .contains(property.getName())
                ? "url"
                : "text"
        );
        DossierMetadonneeEnum metaEnum = DossierMetadonneeEnum.fromString(property.getName());
        widget.setParametres(
            Arrays.asList(
                new Parametre(
                    VALEUR,
                    ofNullable(metaEnum)
                        .map(DossierMetadonneeEnum::getValueFunction)
                        .map(f -> f.apply(dossier))
                        .map(v -> getValueFromTableRef(property.getName(), v, context))
                        .orElse("")
                )
            )
        );
        return widget;
    }

    private boolean isVisibleFicheDossier(PropertyDescriptor property, SpecificContext context) {
        boolean isVisible = false;
        if (DossierMetadonneeEnum.TITLE.getName().equals(property.getName())) {
            isVisible = true;
        } else if (property.isFicheDossier()) {
            // vérification si le principal fait partie du sénat pour
            // les propertyDescriptor pouvant être vu que par le sénat
            EppPrincipal eppPrincipal = (EppPrincipal) context.getSession().getPrincipal();
            isVisible = !property.isVisibility() || eppPrincipal.isInstitutionSenat();
        }
        return DossierMetadonneeEnum.hasMetadonnee(property.getName()) && isVisible;
    }

    @SuppressWarnings("unchecked")
    private Serializable getValueFromTableRef(String name, Serializable valeur, SpecificContext context) {
        Serializable newValeur = valeur;
        if (METADONNEES_IDENTITE.contains(name) && valeur != null) {
            newValeur = getTitleFromTableReference((String) valeur, SolonEppConstant.IDENTITE_DOC_TYPE, context);
        } else if (METADONNEES_ORGANISME.contains(name) && valeur != null) {
            newValeur = getTitleFromTableReference((String) valeur, SolonEppConstant.ORGANISME_DOC_TYPE, context);
        } else if (METADONNEES_IDENTITE_LIST.contains(name) && valeur != null) {
            List<String> identites = (List<String>) valeur;
            newValeur =
                identites
                    .stream()
                    .map(i -> getTitleFromTableReference(i, SolonEppConstant.IDENTITE_DOC_TYPE, context))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        } else if (METADONNEES_ORGANISME_LIST.contains(name) && valeur != null) {
            List<String> organismes = (List<String>) valeur;
            newValeur =
                organismes
                    .stream()
                    .map(o -> getTitleFromTableReference(o, SolonEppConstant.ORGANISME_DOC_TYPE, context))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }
        return newValeur;
    }

    private List<SuggestionDTO> getSuggestList(String name, Object valeur, SpecificContext context) {
        List<SuggestionDTO> lstSuggestion = new ArrayList<>();
        if (METADONNEES_IDENTITE.contains(name) && valeur != null) {
            String label = getTitleFromTableReference((String) valeur, SolonEppConstant.IDENTITE_DOC_TYPE, context);
            lstSuggestion.add(new SuggestionDTO(valeur.toString(), label));
        } else if (METADONNEES_ORGANISME.contains(name) && valeur != null) {
            String label = getTitleFromTableReference((String) valeur, SolonEppConstant.ORGANISME_DOC_TYPE, context);
            lstSuggestion.add(new SuggestionDTO(valeur.toString(), label));
        } else if (METADONNEES_IDENTITE_LIST.contains(name) && valeur != null) {
            List<String> identites = (List<String>) valeur;
            lstSuggestion.addAll(
                identites
                    .stream()
                    .map(
                        cle ->
                            new SuggestionDTO(
                                cle,
                                getTitleFromTableReference(cle, SolonEppConstant.IDENTITE_DOC_TYPE, context)
                            )
                    )
                    .collect(Collectors.toList())
            );
        } else if (METADONNEES_ORGANISME_LIST.contains(name) && valeur != null) {
            List<String> organismes = (List<String>) valeur;
            lstSuggestion.addAll(
                organismes
                    .stream()
                    .map(
                        org ->
                            new SuggestionDTO(
                                org,
                                getTitleFromTableReference(org, SolonEppConstant.ORGANISME_DOC_TYPE, context)
                            )
                    )
                    .collect(Collectors.toList())
            );
        } else if (METADONNEES_LISTSIMPLE.contains(name) && valeur != null) {
            Stream
                .of(((String) valeur).split(", "))
                .map(text -> new SuggestionDTO(text, text))
                .forEach(lstSuggestion::add);
        } else if (CommunicationMetadonneeEnum.RUBRIQUE.getName().equals(name) && valeur != null) {
            lstSuggestion.add(
                new SuggestionDTO(
                    (String) valeur,
                    STServiceLocator
                        .getVocabularyService()
                        .getEntryLabel(SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY, (String) valeur)
                )
            );
        }
        return lstSuggestion;
    }

    private boolean isWidgetDisplayed(WidgetDTO widget, SpecificContext context) {
        context.putInContextData(COLUMN_NAME, widget.getName());
        boolean isDisplayed = SolonEppActionsServiceLocator.getMetadonneesActionService().isColumnVisible(context);
        if (NIVEAU_LECTURE_NUMERO.getName().equals(widget.getName())) {
            isDisplayed = false;
        }
        if (
            (
                DATE_REFUS_PROCEDURE_ENGAGEMENT_AN.getName().equals(widget.getName()) ||
                DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT.getName().equals(widget.getName())
            ) &&
            (isEditMode(context) || StringUtils.isBlank((String) widget.getValueParamByName(VALEUR)))
        ) {
            isDisplayed = false;
        }
        if (HIDDEN.equals(widget.getValueParamByName("widgetMode"))) {
            isDisplayed = false;
        }
        return isDisplayed;
    }

    private String getTitleFromTableReference(String id, String tableReference, SpecificContext context) {
        context.putInContextData(ID, id);
        context.putInContextData(TABLE_REFERENCE, tableReference);
        return SolonEppActionsServiceLocator.getMetadonneesActionService().getTitleFromTableReference(context);
    }

    private boolean isEditMode(SpecificContext context) {
        return (boolean) ofNullable(context.getFromContextData(IS_EDIT_MODE)).orElse(false);
    }

    private List<WidgetDTO> getPieceJointeWidgets(SpecificContext context) {
        List<WidgetDTO> widgets = new ArrayList<>();
        boolean isEditMode = isEditMode(context);
        Map<String, PieceJointeDescriptor> pjTypes = SolonEppActionsServiceLocator
            .getCorbeilleActionService()
            .getMapTypePieceJointe(context);
        for (Entry<String, PieceJointeDescriptor> pj : pjTypes.entrySet()) {
            String pjType = pj.getKey();
            PieceJointeDescriptor pjDescriptor = pj.getValue();
            WidgetDTO pjWidget = new WidgetDTO();
            pjWidget.setName(pjType);
            context.putInContextData(TYPE_PIECE_JOINTE, pjType);
            pjWidget.setLabel(SolonEppActionsServiceLocator.getMetadonneesActionService().getPieceJointeType(context));
            pjWidget.setTypeChamp(isEditMode ? PIECE_JOINTE.toString() : FILE_MULTI.toString());
            pjWidget.setPjMultiValue(pjDescriptor.isMultivalue());
            pjWidget.setLstPieces(getListPieceJointe(context, pjType));
            pjWidget.setRequired(pjDescriptor.isObligatoire());
            widgets.add(pjWidget);
        }
        return widgets;
    }

    private List<PieceJointeDTO> getListPieceJointe(SpecificContext context, String type) {
        Set<String> newPjTitles = SolonEppServiceLocator
            .getPieceJointeService()
            .getNewPieceJointeTitreList(
                context.getSession(),
                type,
                context.getCurrentDocument(),
                ((Version) context.getFromContextData(CURRENT_VERSION)).getDocument()
            );
        List<PieceJointe> pieceJointeList = context.getFromContextData(PIECE_JOINTE_LIST);
        return ofNullable(pieceJointeList)
            .orElse(Collections.emptyList())
            .stream()
            .filter(pj -> type.equals(pj.getTypePieceJointe()))
            .map(pj -> convertToPieceJointeDTO(pj, newPjTitles))
            .collect(Collectors.toList());
    }

    private PieceJointeDTO convertToPieceJointeDTO(PieceJointe pieceJointe, Set<String> newPjTitles) {
        PieceJointeDTO pieceJointeDTO = new PieceJointeDTO();
        pieceJointeDTO.setPieceJointeId(pieceJointe.getTitle());
        pieceJointeDTO.setPieceJointeTitre(pieceJointe.getNom());
        pieceJointeDTO.setPieceJointeUrl(pieceJointe.getUrl());
        pieceJointeDTO.setListPieceJointeFichier(
            pieceJointe
                .getPieceJointeFichierDocList()
                .stream()
                .map(this::convertToDocumentDTO)
                .collect(Collectors.toList())
        );
        pieceJointeDTO.setModifiedMetaList(pieceJointe.getModifiedMetaList());
        pieceJointeDTO.setModifiedFileList(pieceJointe.getModifiedFileList());
        pieceJointeDTO.setDeletedFileList(pieceJointe.getDeletedFileList());
        pieceJointeDTO.setNew(newPjTitles.contains(pieceJointe.getNom()));
        return pieceJointeDTO;
    }

    private DocumentDTO convertToDocumentDTO(DocumentModel pjFichierDoc) {
        DocumentDTO dto = new DocumentDTO();
        PieceJointeFichier pjFichier = pjFichierDoc.getAdapter(PieceJointeFichier.class);
        dto.setId(pjFichierDoc.getId());
        dto.setNom(pjFichier.getSafeFilename());
        try {
            dto.setLink(
                getDownloadService()
                    .getDownloadUrl(
                        pjFichierDoc,
                        STSchemaConstant.FILE_SCHEMA + ":" + STSchemaConstant.FILE_CONTENT_PROPERTY,
                        pjFichier.getFilename()
                    )
            );
        } catch (BlobNotFoundException e) {
            LOG.warn(STLogEnumImpl.FAIL_GET_FILE_TEC, e);
        }
        return dto;
    }
}
