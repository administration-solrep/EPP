package fr.dila.epp.ui.services.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.JSON_SEARCH;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.MESSAGE_LIST_FORM;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.AUTOCOMPLETE_PARAM_NAME;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.INCLURE_PARAM_NAME;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.LABEL_INCLURE_COMMISSION;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.LABEL_INCLURE_MANDAT;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.LABEL_PREFIX;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.LST_NIVEAU_LECTURE_PARAM_NAME;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.LST_VALUES_PARAM_NAME;
import static fr.dila.st.ui.enums.STContextDataKey.ID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.bean.RechercheDynamique;
import fr.dila.epp.ui.contentview.CorbeillePageProvider;
import fr.dila.epp.ui.enumeration.SelectWidgetMetadonneesEnum;
import fr.dila.epp.ui.helper.MessageListHelper;
import fr.dila.epp.ui.services.RechercheUIService;
import fr.dila.epp.ui.services.SelectValueUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.th.bean.MessageListForm;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppMetaConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.core.dao.MessageDao;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import fr.dila.st.core.requete.recherchechamp.Parametre;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.bean.WidgetDTO;
import fr.dila.st.ui.enums.WidgetTypeEnum;
import fr.dila.st.ui.th.bean.PaginationForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

public class RechercheUIServiceImpl implements RechercheUIService {
    private static final String LABEL_TEXTE_INTEGRAL = "recherche.label.texte.integral";
    private static final String LIKE = " LIKE";
    private static final String META_CATEGORIE = "categorie";
    private static final String META_DATE_COMMUNICATION = "dateCommunication";
    private static final String META_OBJET_DOSSIER = "objetDossier";
    private static final String META_TEXTE_INTEGRAL = "texteIntegral";
    private static final String SUFFIXE_DEBUT = "Debut";
    private static final String SUFFIXE_FIN = "Fin";
    private static final String SUFFIXE_FIN_FOR_MESSAGE_DAO = "_fin";

    private static final Set<String> METADONEES_TO_REMOVE = Sets.newHashSet(
        META_CATEGORIE,
        SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY,
        META_OBJET_DOSSIER,
        META_DATE_COMMUNICATION,
        META_DATE_COMMUNICATION + SUFFIXE_FIN_FOR_MESSAGE_DAO,
        STSchemaConstant.DUBLINCORE_TITLE_PROPERTY,
        SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY,
        SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY,
        SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY,
        SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY,
        PaginationForm.TOTAL_PARAM_NAME,
        META_TEXTE_INTEGRAL,
        MessageListForm.ID_DOSSIER_TRI,
        MessageListForm.OBJET_DOSSIER_TRI,
        MessageListForm.EMETTEUR_TRI,
        MessageListForm.DESTINATAIRE_TRI,
        MessageListForm.COPIE_TRI,
        MessageListForm.COMMUNICATION_TRI,
        MessageListForm.DATE_TRI,
        PaginationForm.PAGE_PARAM_NAME,
        PaginationForm.SIZE_PARAM_NAME
    );

    @Override
    public RechercheDynamique getRechercheDynamique(SpecificContext context) {
        String categorieEvenement = context.getFromContextData(ID);
        RechercheDynamique rechercheDynamique = new RechercheDynamique();

        // Ne construit pas de widget pour certaines propriétés
        Set<String> ignoredMetadata = ImmutableSet.of(
            SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY,
            // Niveau numero a prendre avec widget du niveau code
            SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY
        );

        // Liste des types d'événements de la catégorie
        Set<EvenementTypeDescriptor> evenements = SolonEppServiceLocator
            .getEvenementTypeService()
            .findEvenementType()
            .stream()
            .filter(descriptor -> descriptor.getProcedure().equals(categorieEvenement))
            .collect(Collectors.toSet());

        List<WidgetDTO> widgets = getEvenementWidgetDTOs(evenements);
        widgets.addAll(
            SolonEppServiceLocator
                .getMetaDonneesService()
                .getAll()
                .stream()
                .filter(
                    descriptor ->
                        evenements.stream().anyMatch(evenement -> evenement.getName().equals(descriptor.getName()))
                )
                .map(MetaDonneesDescriptor::getVersion)
                .map(VersionMetaDonneesDescriptor::getProperty)
                .flatMap(map -> map.entrySet().stream())
                .map(Entry::getValue)
                .filter(distinctByKey(PropertyDescriptor::getName))
                .filter(descriptor -> !ignoredMetadata.contains(descriptor.getName()))
                .map(this::getWidgetFromPropertyDescriptor)
                .collect(Collectors.toList())
        );
        widgets.add(new WidgetDTO(META_TEXTE_INTEGRAL, LABEL_TEXTE_INTEGRAL, WidgetTypeEnum.INPUT_TEXT.toString()));

        rechercheDynamique.setLstWidgets(widgets);

        return rechercheDynamique;
    }

    private List<WidgetDTO> getEvenementWidgetDTOs(Set<EvenementTypeDescriptor> evenements) {
        WidgetDTO widgetTypeCommunication = new WidgetDTO(
            SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY,
            LABEL_PREFIX + SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY,
            WidgetTypeEnum.MULTIPLE_INPUT_TEXT.toString()
        );
        widgetTypeCommunication.setParametres(
            Arrays.asList(
                new Parametre(
                    LST_VALUES_PARAM_NAME,
                    evenements
                        .stream()
                        .map(evenement -> new SelectValueDTO(evenement.getName(), evenement.getLabel()))
                        .sorted(Comparator.comparing(SelectValueDTO::getLabel))
                        .collect(Collectors.toList())
                )
            )
        );

        WidgetDTO widgetObjetDossier = new WidgetDTO(
            META_OBJET_DOSSIER,
            LABEL_PREFIX + META_OBJET_DOSSIER,
            WidgetTypeEnum.INPUT_TEXT.toString()
        );
        WidgetDTO widgetDateCommunication = new WidgetDTO(
            META_DATE_COMMUNICATION,
            LABEL_PREFIX + META_DATE_COMMUNICATION,
            WidgetTypeEnum.DATE.toString()
        );
        WidgetDTO widgetIdCommunication = new WidgetDTO(
            STSchemaConstant.DUBLINCORE_TITLE_PROPERTY,
            LABEL_PREFIX + STSchemaConstant.DUBLINCORE_TITLE_PROPERTY,
            WidgetTypeEnum.INPUT_TEXT.toString()
        );
        WidgetDTO widgetIdDossier = new WidgetDTO(
            SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY,
            LABEL_PREFIX + SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY,
            WidgetTypeEnum.INPUT_TEXT.toString()
        );

        List<SelectValueDTO> institutions = SolonEppUIServiceLocator.getSelectValueUIService().getAllInstitutions();
        WidgetDTO widgetEmetteur = new WidgetDTO(
            SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY,
            LABEL_PREFIX + SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY,
            WidgetTypeEnum.MULTIPLE_INPUT_TEXT.toString()
        );
        widgetEmetteur.setParametres(Arrays.asList(new Parametre(LST_VALUES_PARAM_NAME, institutions)));
        WidgetDTO widgetDestinataire = new WidgetDTO(
            SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY,
            LABEL_PREFIX + SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY,
            WidgetTypeEnum.MULTIPLE_INPUT_TEXT.toString()
        );
        widgetDestinataire.setParametres(Arrays.asList(new Parametre(LST_VALUES_PARAM_NAME, institutions)));
        WidgetDTO widgetCopie = new WidgetDTO(
            SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY,
            LABEL_PREFIX + SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY,
            WidgetTypeEnum.MULTIPLE_INPUT_TEXT.toString()
        );
        widgetCopie.setParametres(Arrays.asList(new Parametre(LST_VALUES_PARAM_NAME, institutions)));
        return Lists.newArrayList(
            widgetTypeCommunication,
            widgetObjetDossier,
            widgetDateCommunication,
            widgetIdCommunication,
            widgetIdDossier,
            widgetEmetteur,
            widgetDestinataire,
            widgetCopie
        );
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private WidgetDTO getWidgetFromPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        String propertyName = propertyDescriptor.getName();
        String propertyType = propertyDescriptor.getType();
        WidgetDTO widget = new WidgetDTO(propertyName, LABEL_PREFIX + propertyName);
        SelectValueUIService selectValueUIService = SolonEppUIServiceLocator.getSelectValueUIService();

        if (SelectWidgetMetadonneesEnum.containsProperty(propertyType)) {
            SelectWidgetMetadonneesEnum value = SelectWidgetMetadonneesEnum.getEnumFromProperty(propertyType);
            if (value != null) {
                widget.setTypeChamp(value.getWidgetType().toString());
                List<Parametre> parametres = Collections.emptyList();
                if (WidgetTypeEnum.SELECT.equals(value.getWidgetType())) {
                    String vocabulary = value.getProperties().get(0);
                    parametres =
                        Arrays.asList(
                            new Parametre(
                                LST_VALUES_PARAM_NAME,
                                SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY.equals(vocabulary)
                                    ? selectValueUIService.getAllRubriques()
                                    : selectValueUIService.getSelectValuesFromVocabulary(vocabulary)
                            )
                        );
                } else if (WidgetTypeEnum.INPUT_TEXT.equals(value.getWidgetType())) {
                    parametres = new ArrayList<>();
                    parametres.add(new Parametre(AUTOCOMPLETE_PARAM_NAME, "true"));
                    parametres.add(
                        new Parametre(
                            INCLURE_PARAM_NAME,
                            SolonEppConstant.IDENTITE_DOC_TYPE.equals(value.getProperties().get(0))
                                ? LABEL_INCLURE_MANDAT
                                : LABEL_INCLURE_COMMISSION
                        )
                    );
                }
                widget.setParametres(parametres);
            }
        } else if (
            SolonEppMetaConstant.META_TYPE_STRING.equals(propertyType) ||
            SolonEppMetaConstant.META_TYPE_INTEGER.equals(propertyType)
        ) {
            widget.setTypeChamp(WidgetTypeEnum.INPUT_TEXT.toString());
        } else if (SolonEppMetaConstant.META_TYPE_NIVEAU_LECTURE.equals(propertyType)) {
            widget.setTypeChamp(WidgetTypeEnum.NIVEAU_LECTURE.toString());
            widget.setParametres(
                Arrays.asList(new Parametre(LST_NIVEAU_LECTURE_PARAM_NAME, selectValueUIService.getAllNiveauxLecture()))
            );
        } else if (SolonEppMetaConstant.META_TYPE_DATE.equals(propertyType)) {
            widget.setTypeChamp(WidgetTypeEnum.DATE.toString());
        } else {
            throw new NuxeoException("Type de métadonnée inconnu: " + propertyType);
        }

        return widget;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MessageList getResultatsRecherche(SpecificContext context) {
        String jsonSearch = context.getFromContextData(JSON_SEARCH);
        MessageListForm messageListForm = context.getFromContextData(MESSAGE_LIST_FORM);
        String query;
        MessageCriteria messageCriteria = buildMessageCriteriaFromJson(jsonSearch);
        MessageDao messageDao = new MessageDao(context.getSession(), messageCriteria);
        query = messageDao.getQueryString();
        query =
            query.replaceAll(
                SolonEppSchemaConstant.VERSION_REDEPOT_PROPERTY + LIKE,
                SolonEppSchemaConstant.VERSION_REDEPOT_PROPERTY + " ="
            );
        query =
            query.replaceAll(
                SolonEppSchemaConstant.VERSION_RECTIFICATIF_PROPERTY + LIKE,
                SolonEppSchemaConstant.VERSION_RECTIFICATIF_PROPERTY + " ="
            );
        query =
            query.replaceAll(
                SolonEppSchemaConstant.VERSION_POSITION_ALERTE_PROPERTY + LIKE,
                SolonEppSchemaConstant.VERSION_POSITION_ALERTE_PROPERTY + " ="
            );
        List<Object> paramList = messageDao.getParamList();
        paramList.replaceAll(
            o ->
                o instanceof GregorianCalendar
                    ? String.format(
                        "TIMESTAMP '%s'",
                        SolonDateConverter.DATETIME_DASH_REVERSE_T_SECOND_COLON_Z.format((GregorianCalendar) o, true)
                    )
                    : o instanceof String ? "'" + o + "'" : o
        );
        paramList.replaceAll(o -> "'%false%'".equals(o) ? "0" : "'%true%'".equals(o) ? "1" : o);

        CorbeillePageProvider provider = messageListForm.getPageProvider(
            context.getSession(),
            "corbeillePageProvider",
            paramList
        );
        provider.getDefinition().setPattern(query);

        List<Map<String, Serializable>> docList = provider.getCurrentPage();

        return MessageListHelper.buildMessageList(docList, messageListForm, (int) provider.getResultsCount());
    }

    @SuppressWarnings("unchecked")
    private MessageCriteria buildMessageCriteriaFromJson(String jsonSearch) {
        Gson gson = new Gson();
        Map<String, Object> metadonneeCriteria = formatDateMetadonnees(gson.fromJson(jsonSearch, Map.class));
        MessageCriteria messageCriteria = new MessageCriteria();
        messageCriteria.setCheckReadPermission(true);

        List<String> typeEvenement = (List<String>) metadonneeCriteria.get(
            SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY
        );
        String categorieEvenementId = (String) metadonneeCriteria.get(META_CATEGORIE);
        if (CollectionUtils.isEmpty(typeEvenement) && categorieEvenementId != null) {
            typeEvenement =
                SolonEppServiceLocator
                    .getEvenementTypeService()
                    .findEvenementByCategory(categorieEvenementId)
                    .stream()
                    .map(eventType -> eventType.getName())
                    .collect(Collectors.toList());
        }
        messageCriteria.setEvenementTypeIn(typeEvenement);

        String objetDossier = (String) metadonneeCriteria.get(META_OBJET_DOSSIER);
        if (StringUtils.isNotBlank(objetDossier)) {
            messageCriteria.setVersionObjetLike(objetDossier);
        }

        Date dateCommunicationDebut = (Date) metadonneeCriteria.get(META_DATE_COMMUNICATION);
        if (dateCommunicationDebut != null) {
            messageCriteria.setVersionHorodatageMin(DateUtil.dateToGregorianCalendar(dateCommunicationDebut));
        }
        Date dateCommunicationFin = (Date) metadonneeCriteria.get(
            META_DATE_COMMUNICATION + SUFFIXE_FIN_FOR_MESSAGE_DAO
        );
        if (dateCommunicationFin != null) {
            Calendar dateFin = DateUtil.dateToGregorianCalendar(dateCommunicationFin);
            dateFin.add(Calendar.DAY_OF_MONTH, 1);
            messageCriteria.setVersionHorodatageMax(dateFin);
        }

        String idCommunication = (String) metadonneeCriteria.get(STSchemaConstant.DUBLINCORE_TITLE_PROPERTY);
        if (StringUtils.isNotBlank(idCommunication)) {
            messageCriteria.setEvenementId(idCommunication);
        }

        String idDossier = (String) metadonneeCriteria.get(SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY);
        if (StringUtils.isNotBlank(idDossier)) {
            messageCriteria.setDossierId(idDossier);
        }

        List<String> emetteur = (List<String>) metadonneeCriteria.get(
            SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY
        );
        if (CollectionUtils.isNotEmpty(emetteur)) {
            messageCriteria.setEvenementEmetteurIn(emetteur);
        }

        List<String> destinataire = (List<String>) metadonneeCriteria.get(
            SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY
        );
        if (CollectionUtils.isNotEmpty(destinataire)) {
            messageCriteria.setEvenementDestinataireIn(destinataire);
        }

        List<String> copie = (List<String>) metadonneeCriteria.get(
            SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY
        );
        if (CollectionUtils.isNotEmpty(copie)) {
            messageCriteria.setEvenementDestinataireCopieIn(copie);
        }

        String texteIntegral = (String) metadonneeCriteria.get(META_TEXTE_INTEGRAL);
        if (StringUtils.isNotBlank(texteIntegral)) {
            messageCriteria.setPieceJointeFichierFulltext(texteIntegral);
        }

        metadonneeCriteria.keySet().removeAll(METADONEES_TO_REMOVE);
        messageCriteria.setMetadonneeCriteria(metadonneeCriteria);

        return messageCriteria;
    }

    private Map<String, Object> formatDateMetadonnees(Map<String, Object> map) {
        return map
            .entrySet()
            .stream()
            .collect(HashMap::new, (m, v) -> m.put(buildKeyFormat(v), buildValueFormat(v)), HashMap::putAll);
    }

    private String buildKeyFormat(Map.Entry<String, Object> entry) {
        if (entry.getKey().endsWith(SUFFIXE_DEBUT)) {
            return StringUtils.substringBeforeLast(entry.getKey(), SUFFIXE_DEBUT);
        } else if (entry.getKey().endsWith(SUFFIXE_FIN)) {
            return StringUtils.substringBeforeLast(entry.getKey(), SUFFIXE_FIN).concat(SUFFIXE_FIN_FOR_MESSAGE_DAO);
        } else {
            return entry.getKey();
        }
    }

    private Object buildValueFormat(Map.Entry<String, Object> entry) {
        return StringUtils.endsWithAny(entry.getKey(), SUFFIXE_DEBUT, SUFFIXE_FIN)
            ? SolonDateConverter.DATE_SLASH.parseToDateOrNull((String) entry.getValue())
            : entry.getValue();
    }
}
