package fr.dila.solonepp.core.dao;

import static fr.dila.st.core.query.QueryHelper.DEFAULT_PAGE_SIZE;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppParametreConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.dao.criteria.OrderByCriteria;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dao.pagination.PageInfo;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * DAO des messages.
 *
 * @author jtremeaux
 */
public class MessageDao {
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(MessageDao.class);

    private static final String EXTRA_SELECT = "@@EXTREA_SELECT@@";
    /**
     * Session.
     */
    private CoreSession session;

    /**
     * Critères de recherche.
     */
    private MessageCriteria criteria;

    /**
     * Informations de pagination.
     */
    private PageInfo pageInfo;

    /**
     * Chaîne de la requête après construction après construction de la requête.
     */
    private String queryString;

    /**
     * Paramètres après construction de la requête.
     */
    private List<Object> paramList;

    /**
     * Constructeur de MessageDao.
     *
     * @param session  Session
     * @param criteria Critères de recherche
     * @param pageInfo Informations de pagination
     */
    public MessageDao(CoreSession session, MessageCriteria criteria, PageInfo pageInfo) {
        this.session = session;
        this.criteria = criteria;
        this.pageInfo = pageInfo;

        build();
    }

    /**
     * Constructeur de MessageDao.
     *
     * @param session  Session
     * @param criteria Critères de recherche
     */
    public MessageDao(CoreSession session, MessageCriteria criteria) {
        this(session, criteria, null);
    }

    /**
     * Construit la chaîne de la requete et la liste des paramètres.
     */
    protected void build() {
        StringBuilder sb = new StringBuilder("SELECT m.ecm:uuid as id ");
        sb.append(EXTRA_SELECT + " FROM ");
        sb.append(SolonEppConstant.MESSAGE_DOC_TYPE);
        sb.append(" AS m ");

        List<String> extraSelect = new ArrayList<>();

        boolean joinDossier = isJoinDossier();

        boolean joinEvenement = isJoinEvenement(joinDossier);

        boolean joinPieceJointeFichier = isJoinPieceJointeFichier();

        boolean joinPieceJointe = joinPieceJointeFichier;

        boolean joinVersion = isJoinVersion(joinPieceJointe);

        // gestion de l'order by
        StringBuilder orderBySb = null;
        if (criteria.getOrderByList() != null && !criteria.getOrderByList().isEmpty()) {
            orderBySb = new StringBuilder(" ORDER BY ");

            int index = 0;
            for (OrderByCriteria orderByCtrireia : criteria.getOrderByList()) {
                if (index != 0) {
                    orderBySb.append(", ");
                }
                index++;

                switch (orderByCtrireia.getField()) {
                    case ID_DOSSIER:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "m.",
                            STSchemaConstant.CASE_LINK_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.ID_DOSSIER
                        );
                        joinEvenement = true;
                        break;
                    case OBJET_DOSSIER:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "v.",
                            SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.VERSION_OBJET_PROPERTY
                        );
                        joinVersion = true;
                        break;
                    case HORODATAGE:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "v.",
                            SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY
                        );
                        joinVersion = true;
                        break;
                    case EMETTEUR:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "e.",
                            SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY
                        );
                        joinEvenement = true;
                        break;
                    case DESTINATAIRE:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "e.",
                            SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY
                        );
                        joinEvenement = true;
                        break;
                    case COPIE:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "e.",
                            SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY
                        );
                        joinEvenement = true;
                        break;
                    case TYPE_EVENEMENT:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "e.",
                            SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY
                        );
                        joinEvenement = true;
                        break;
                    case NIVEAU_LECTURE:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "v.",
                            SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY
                        );
                        joinVersion = true;
                        break;
                    case VERSION:
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "v.",
                            SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.VERSION_MAJOR_VERSION_PROPERTY
                        );
                        addOrderClause(
                            orderBySb,
                            orderByCtrireia.getOrder(),
                            "v.",
                            SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX,
                            SolonEppSchemaConstant.VERSION_MINOR_VERSION_PROPERTY
                        );
                        joinVersion = true;
                        break;
                }
            }
        }

        List<String> criteriaList = new ArrayList<>();
        paramList = new ArrayList<>();

        if (joinDossier) {
            addJoin(sb, SolonEppConstant.DOSSIER_DOC_TYPE, "d");

            addJoinRestriction(
                criteriaList,
                "e.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_PARENT_ID_PROPERTY,
                "d.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_UUID_PROPERTY
            );
        }

        if (joinEvenement) {
            addJoin(sb, SolonEppConstant.EVENEMENT_DOC_TYPE, "e");

            addJoinRestriction(
                criteriaList,
                "m.",
                STSchemaConstant.CASE_LINK_SCHEMA_PREFIX,
                STSchemaConstant.CASE_LINK_CASE_DOCUMENT_ID_PROPERTY,
                "e.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_UUID_PROPERTY
            );
        }

        if (joinVersion) {
            addJoin(sb, SolonEppConstant.VERSION_DOC_TYPE, "v");

            addJoinRestriction(
                criteriaList,
                "m.",
                STSchemaConstant.CASE_LINK_SCHEMA_PREFIX,
                SolonEppSchemaConstant.CASE_LINK_ACTIVE_VERSION_ID_PROPERTY,
                "v.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_UUID_PROPERTY
            );
        }

        if (joinPieceJointe) {
            addJoin(sb, SolonEppConstant.PIECE_JOINTE_DOC_TYPE, "p");

            addJoinRestriction(
                criteriaList,
                "p.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_PARENT_ID_PROPERTY,
                "v.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_UUID_PROPERTY
            );
        }

        if (joinPieceJointeFichier) {
            addJoin(sb, SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE, "f");

            addJoinRestriction(
                criteriaList,
                "p.",
                SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA_PREFIX,
                SolonEppSchemaConstant.PIECE_JOINTE_PIECE_JOINTE_FICHIER_LIST_PROPERTY,
                "f.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_UUID_PROPERTY
            );
        }

        if (criteria.getDossierAlerte() != null) {
            Object value = 0L;

            String comparator = " = ? ";
            if (criteria.getDossierAlerte()) {
                comparator = " > ? ";
            }

            addDossierRestriction(
                SolonEppSchemaConstant.DOSSIER_ALERTE_COUNT_PROPERTY,
                comparator,
                criteriaList,
                value
            );
        }

        if (criteria.getCurrentLifeCycleState() != null) {
            addRestriction(
                "m.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE,
                " = ? ",
                criteriaList,
                criteria.getCurrentLifeCycleState()
            );
        }

        if (StringUtils.isNotBlank(criteria.getCaseDocumentId())) {
            addMessageRestriction(
                STSchemaConstant.CASE_LINK_CASE_DOCUMENT_ID_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getCaseDocumentId()
            );
        }

        if (StringUtils.isNotBlank(criteria.getEvenementId())) {
            String evenementId = criteria.getEvenementId().replaceAll("\\*", "%");

            addMessageRestriction(
                SolonEppSchemaConstant.ID_EVENEMENT,
                getEqualsOrLikeClause(evenementId),
                criteriaList,
                criteria.getEvenementId()
            );
        }

        if (StringUtils.isNotBlank(criteria.getMessageType())) {
            addMessageRestriction(
                SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getMessageType()
            );
        }

        if (criteria.getMessageTypeIn() != null) {
            StringBuilder comparator = new StringBuilder(" IN (");
            comparator.append(StringHelper.getQuestionMark(criteria.getMessageTypeIn().size()));
            comparator.append(") ");

            addMessageRestriction(
                SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_PROPERTY,
                comparator.toString(),
                criteriaList,
                criteria.getMessageTypeIn()
            );
        }

        if (StringUtils.isNotBlank(criteria.getCorbeille())) {
            addMessageRestriction(
                SolonEppSchemaConstant.CASE_LINK_CORBEILLE_LIST_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getCorbeille()
            );
        }

        if (criteria.getArNecessaire() != null) {
            addMessageRestriction(
                SolonEppSchemaConstant.CASE_LINK_AR_NECESSAIRE_PROPERTY,
                " ? ",
                criteriaList,
                criteria.getArNecessaire() ? 1 : 0
            );
        }

        if (criteria.getArDonne() != null) {
            Object value = 0L;
            String comparator = " = ? ";

            if (criteria.getArDonne()) {
                comparator = " = ? ";
            }

            addMessageRestriction(
                SolonEppSchemaConstant.CASE_LINK_AR_NON_DONNE_COUNT_PROPERTY,
                comparator,
                criteriaList,
                value
            );
        }

        // état de message
        if (StringUtils.isNotBlank(criteria.getEtatMessage())) {
            addMessageRestriction(
                SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getEtatMessage()
            );
        }

        // liste états de message
        if (criteria.getEtatMessageIn() != null && !criteria.getEtatMessageIn().isEmpty()) {
            StringBuilder comparator = new StringBuilder(" IN (");
            comparator.append(StringHelper.getQuestionMark(criteria.getEtatMessageIn().size()));
            comparator.append(") ");

            addMessageRestriction(
                SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY,
                comparator.toString(),
                criteriaList,
                criteria.getEtatMessageIn()
            );
        }

        // Exclusion d'état de message
        if (criteria.getEtatMessageExclus() != null && !criteria.getEtatMessageExclus().isEmpty()) {
            List<String> etatMessageForIn = getEtatMessageInclus(criteria.getEtatMessageExclus());

            if (criteria.getDateTraitementMin() != null) {
                StringBuilder criteriaSb = new StringBuilder(" ((m.")
                    .append(SolonEppSchemaConstant.CASE_LINK_SCHEMA_PREFIX)
                    .append(":")
                    .append(SolonEppSchemaConstant.CASE_LINK_DATE_TRAITEMENT_PROPERTY)
                    .append(" >= ? ")
                    // Exclusion d'état de message
                    .append(" AND m.")
                    .append(SolonEppSchemaConstant.CASE_LINK_SCHEMA_PREFIX)
                    .append(":")
                    .append(SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY)
                    .append(" IN (")
                    .append(StringHelper.getQuestionMark(criteria.getEtatMessageExclus().size()))
                    .append(")) ")
                    // Exclusion d'état de message
                    .append(" OR m.")
                    .append(SolonEppSchemaConstant.CASE_LINK_SCHEMA_PREFIX)
                    .append(":")
                    .append(SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY)
                    .append(" IN (")
                    .append(StringHelper.getQuestionMark(etatMessageForIn.size()))
                    .append(")) ");

                criteriaList.add(criteriaSb.toString());
                paramList.add(criteria.getDateTraitementMin());
                paramList.addAll(criteria.getEtatMessageExclus());
                paramList.addAll(etatMessageForIn);
            } else {
                StringBuilder comparator = new StringBuilder(" IN (");
                comparator.append(StringHelper.getQuestionMark(etatMessageForIn.size()));
                comparator.append(") ");

                addMessageRestriction(
                    SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY,
                    comparator.toString(),
                    criteriaList,
                    etatMessageForIn
                );
            }
        }

        if (criteria.getCorbeilleMessageType() != null && criteria.getCorbeilleMessageType()) {
            StringBuilder criteriaSb = new StringBuilder(" ((m.")
                .append(SolonEppSchemaConstant.CASE_LINK_SCHEMA_PREFIX)
                .append(":")
                .append(SolonEppSchemaConstant.CASE_LINK_DATE_TRAITEMENT_PROPERTY)
                .append(" >= ?  ")
                // Exclusion d'état de message
                .append(" AND m.")
                .append(SolonEppSchemaConstant.CASE_LINK_SCHEMA_PREFIX)
                .append(":")
                .append(SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY)
                .append(" IN ('")
                .append(
                    SolonEppLifecycleConstant.MESSAGE_ETAT_WS_AR_RECU +
                    "', '" +
                    SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EMIS +
                    "', '" +
                    SolonEppLifecycleConstant.MESSAGE_ETAT_WS_TRAITE
                )
                .append("')) ")
                // Exclusion d'état de message
                .append(" OR m.")
                .append(SolonEppSchemaConstant.CASE_LINK_SCHEMA_PREFIX)
                .append(":")
                .append(SolonEppSchemaConstant.CASE_LINK_ETAT_MESSAGE_PROPERTY)
                .append(" IN ('")
                .append(
                    SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_ATTENTE_AR +
                    "', '" +
                    SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_COURS_REDACTION +
                    "', '" +
                    SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_COURS_TRAITEMENT +
                    "', '" +
                    SolonEppLifecycleConstant.MESSAGE_ETAT_WS_NON_TRAITE
                )
                .append("')) ");
            criteriaList.add(criteriaSb.toString());

            Calendar dateTraitement = getDateTraitementMinFromParam();
            paramList.add(dateTraitement);
        }

        if (StringUtils.isNotBlank(criteria.getDossierId())) {
            String dossierId = criteria.getDossierId().replaceAll("\\*", "%");

            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY,
                getEqualsOrLikeClause(dossierId),
                criteriaList,
                dossierId
            );
        }

        if (StringUtils.isNotBlank(criteria.getEvenementType())) {
            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getEvenementType()
            );
        }

        if (criteria.getEvenementTypeIn() != null && criteria.getEvenementTypeIn().size() > 0) {
            StringBuilder comparator = new StringBuilder(" IN (");
            comparator.append(StringHelper.getQuestionMark(criteria.getEvenementTypeIn().size()));
            comparator.append(") ");

            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY,
                comparator.toString(),
                criteriaList,
                criteria.getEvenementTypeIn()
            );
        }

        if (criteria.getEvenementCurrentLifeCycleState() != null) {
            addRestriction(
                "e.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE,
                " = ? ",
                criteriaList,
                criteria.getEvenementCurrentLifeCycleState()
            );
        }

        if (StringUtils.isNotBlank(criteria.getEvenementEmetteur())) {
            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getEvenementEmetteur()
            );
        }

        if (criteria.getEvenementEmetteurIn() != null) {
            StringBuilder comparator = new StringBuilder(" IN (");
            comparator.append(StringHelper.getQuestionMark(criteria.getEvenementEmetteurIn().size()));
            comparator.append(") ");

            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY,
                comparator.toString(),
                criteriaList,
                criteria.getEvenementEmetteurIn()
            );
        }

        if (StringUtils.isNotBlank(criteria.getEvenementDestinataire())) {
            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getEvenementDestinataire()
            );
        }

        if (criteria.getEvenementDestinataireIn() != null) {
            StringBuilder comparator = new StringBuilder(" IN (");
            comparator.append(StringHelper.getQuestionMark(criteria.getEvenementDestinataireIn().size()));
            comparator.append(") ");

            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY,
                comparator.toString(),
                criteriaList,
                criteria.getEvenementDestinataireIn()
            );
        }

        if (StringUtils.isNotBlank(criteria.getEvenementDestinataireCopie())) {
            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getEvenementDestinataireCopie()
            );
        }

        if (criteria.getEvenementDestinataireCopieIn() != null) {
            StringBuilder comparator = new StringBuilder(" IN (");
            comparator.append(StringHelper.getQuestionMark(criteria.getEvenementDestinataireCopieIn().size()));
            comparator.append(") ");

            addEvenementRestriction(
                SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY,
                comparator.toString(),
                criteriaList,
                criteria.getEvenementDestinataireCopieIn()
            );
        }

        if (StringUtils.isNotBlank(criteria.getVersionSenat())) {
            addVersionRestriction(
                SolonEppSchemaConstant.VERSION_SENAT_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getVersionSenat()
            );
        }

        if (StringUtils.isNotBlank(criteria.getVersionObjetLike())) {
            addVersionRestriction(
                SolonEppSchemaConstant.VERSION_OBJET_PROPERTY,
                " LIKE ? ",
                criteriaList,
                "%" + criteria.getVersionObjetLike() + "%"
            );
        }

        if (criteria.getVersionHorodatageMin() != null) {
            addVersionRestriction(
                SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY,
                " >= ? ",
                criteriaList,
                criteria.getVersionHorodatageMin()
            );
        }

        if (criteria.getVersionHorodatageMax() != null) {
            addVersionRestriction(
                SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY,
                " <= ? ",
                criteriaList,
                criteria.getVersionHorodatageMax()
            );
        }

        if (criteria.getVersionNiveauLectureNumero() != null) {
            addVersionRestriction(
                SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getVersionNiveauLectureNumero()
            );
        }

        if (StringUtils.isNotBlank(criteria.getVersionNiveauLecture())) {
            addVersionRestriction(
                SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getVersionNiveauLecture()
            );
        }

        if (criteria.getVersionPieceJointePresente() != null) {
            addVersionRestriction(
                SolonEppSchemaConstant.VERSION_PIECE_JOINTE_PRESENTE_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getVersionPieceJointePresente() ? 1 : 0
            );
        }

        if (criteria.getVersionNumeroDepotTexte() != null) {
            addVersionRestriction(
                SolonEppSchemaConstant.VERSION_NUMERO_DEPOT_TEXTE_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getVersionNumeroDepotTexte()
            );
        }

        if (criteria.getDossierNumeroDepotTexte() != null) {
            addDossierRestriction(
                SolonEppSchemaConstant.DOSSIER_NUMERO_DEPOT_TEXTE_PROPERTY,
                " = ? ",
                criteriaList,
                criteria.getDossierNumeroDepotTexte()
            );
        }

        if (criteria.getPieceJointeFichierFulltext() != null) {
            addRestriction(
                "f.",
                STSchemaConstant.ECM_SCHEMA_PREFIX,
                STSchemaConstant.ECM_FULLTEXT_PROPERTY,
                " = '" + criteria.getPieceJointeFichierFulltext() + "'",
                criteriaList,
                null
            );
        }

        if (criteria.getMetadonneeCriteria() != null) {
            String paramValue, property;
            Object value;
            for (Map.Entry<String, Object> entry : criteria.getMetadonneeCriteria().entrySet()) {
                property = entry.getKey();
                value = entry.getValue();

                if (value instanceof String && StringUtils.isNotBlank((String) value)) {
                    paramValue = StringUtils.trim((String) value);
                    // pour les options avec liste, il faut chercher la valeur exacte
                    if (
                        !property.equals(SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY) &&
                        !property.equals(SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY) &&
                        !property.equals(SolonEppSchemaConstant.VERSION_NATURE_RAPPORT_PROPERTY) &&
                        !property.equals(SolonEppSchemaConstant.VERSION_TYPE_LOI_PROPERTY) &&
                        !property.equals(SolonEppSchemaConstant.VERSION_TYPE_ACTE_PROPERTY) &&
                        !property.equals(SolonEppSchemaConstant.VERSION_RUBRIQUE)
                    ) {
                        paramValue = "%" + paramValue + "%";
                    }
                    addVersionRestriction(property, " LIKE ? ", criteriaList, paramValue);
                } else if (value instanceof Date) {
                    if (property.contains(SolonEppSchemaConstant.VERSION_DATE_LIST_PROPERTY)) {
                        String dateListSelect = this.getDateListSelect();
                        if (!extraSelect.contains(dateListSelect)) {
                            extraSelect.add(dateListSelect);
                        }
                    }

                    if (!property.endsWith("_fin")) {
                        addVersionRestriction(
                            property,
                            " >=? ",
                            criteriaList,
                            DateUtil.dateToGregorianCalendar((Date) value)
                        );
                    } else {
                        addVersionRestriction(
                            property.replaceAll("_fin", ""),
                            " <=? ",
                            criteriaList,
                            DateUtil.dateToGregorianCalendar((Date) value)
                        );
                    }
                }
            }
        }

        if (criteria.isCheckReadPermission()) {
            // pas de test direct sur les ACLs ça sert a rien, le parent du message est la
            // mailbox de l'institution
            EppPrincipal principal = (EppPrincipal) session.getPrincipal();
            String institutionId = principal.getInstitutionId();

            final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();
            DocumentModel mailboxDoc = mailboxInstitutionService.getMailboxInstitution(session, institutionId);

            if (mailboxDoc != null) {
                addRestriction(
                    "m.",
                    STSchemaConstant.ECM_SCHEMA_PREFIX,
                    STSchemaConstant.ECM_PARENT_ID_PROPERTY,
                    " = ? ",
                    criteriaList,
                    mailboxDoc.getId()
                );
            }
        }

        if (!criteriaList.isEmpty()) {
            sb.append(" WHERE ").append(StringUtils.join(criteriaList, " AND "));
        }

        if (orderBySb != null) {
            sb.append(orderBySb);
        } else if (criteria.isOrderByHorodatage() && joinVersion) {
            sb
                .append(" ORDER BY ")
                .append("v.")
                .append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX)
                .append(":")
                .append(SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY)
                .append(" DESC");
        }

        // Replace ExtraSelect
        StringBuilder extraSelectBuilder = new StringBuilder("");
        for (String str : extraSelect) {
            extraSelectBuilder.append(",").append(str);
        }

        queryString = sb.toString().replace(EXTRA_SELECT, extraSelectBuilder.toString());
    }

    private String getDateListSelect() {
        StringBuilder selectBuilder = new StringBuilder("");

        selectBuilder.append("v.");
        selectBuilder.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
        selectBuilder.append(":");
        selectBuilder.append(SolonEppSchemaConstant.VERSION_DATE_LIST_PROPERTY);
        return selectBuilder.toString();
    }

    private boolean isJoinVersion(boolean joinPieceJointe) {
        return (
            criteria.isJoinVersionForSorting() ||
            joinPieceJointe ||
            StringUtils.isNotBlank(criteria.getVersionSenat()) ||
            StringUtils.isNotBlank(criteria.getVersionObjetLike()) ||
            criteria.getVersionHorodatageMin() != null ||
            criteria.getVersionHorodatageMax() != null ||
            criteria.getVersionNiveauLectureNumero() != null ||
            StringUtils.isNotBlank(criteria.getVersionNiveauLecture()) ||
            criteria.getVersionPieceJointePresente() != null ||
            StringUtils.isNotBlank(criteria.getVersionNumeroDepotTexte()) ||
            criteria.getMetadonneeCriteria() != null
        );
    }

    private boolean isJoinPieceJointeFichier() {
        return StringUtils.isNotBlank(criteria.getPieceJointeFichierFulltext());
    }

    private boolean isJoinDossier() {
        return criteria.getDossierAlerte() != null || criteria.getDossierNumeroDepotTexte() != null;
    }

    private boolean isJoinEvenement(boolean joinDossier) {
        return (
            criteria.isJoinEvenementForSorting() ||
            joinDossier ||
            StringUtils.isNotBlank(criteria.getDossierId()) ||
            StringUtils.isNotBlank(criteria.getEvenementType()) ||
            criteria.getEvenementTypeIn() != null ||
            StringUtils.isNotBlank(criteria.getEvenementCurrentLifeCycleState()) ||
            StringUtils.isNotBlank(criteria.getEvenementEmetteur()) ||
            StringUtils.isNotBlank(criteria.getEvenementDestinataire()) ||
            StringUtils.isNotBlank(criteria.getEvenementDestinataireCopie()) ||
            criteria.getEvenementEmetteurIn() != null ||
            criteria.getEvenementDestinataireIn() != null ||
            criteria.getEvenementDestinataireCopieIn() != null
        );
    }

    private void addVersionRestriction(String property, String comparator, List<String> criteriaList, Object value) {
        addRestriction("v.", SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX, property, comparator, criteriaList, value);
    }

    private void addEvenementRestriction(String property, String comparator, List<String> criteriaList, Object value) {
        addRestriction("e.", SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX, property, comparator, criteriaList, value);
    }

    private void addMessageRestriction(String property, String comparator, List<String> criteriaList, Object value) {
        addRestriction("m.", STSchemaConstant.CASE_LINK_SCHEMA_PREFIX, property, comparator, criteriaList, value);
    }

    private void addDossierRestriction(String property, String comparator, List<String> criteriaList, Object value) {
        addRestriction("d.", SolonEppSchemaConstant.DOSSIER_SCHEMA_PREFIX, property, comparator, criteriaList, value);
    }

    @SuppressWarnings("unchecked")
    private void addRestriction(
        String alias,
        String prefix,
        String property,
        String comparator,
        List<String> criteriaList,
        Object value
    ) {
        StringBuilder criteriaSb = new StringBuilder(" ");
        criteriaSb.append(alias);
        criteriaSb.append(prefix);
        criteriaSb.append(":");
        criteriaSb.append(property);
        criteriaSb.append(comparator);

        criteriaList.add(criteriaSb.toString());

        if (value != null) {
            if (value instanceof Collection<?>) {
                paramList.addAll((Collection<? extends Object>) value);
            } else {
                paramList.add(value);
            }
        }
    }

    private void addJoinRestriction(
        List<String> criteriaList,
        String alias1,
        String prefix1,
        String property1,
        String alias2,
        String prefix2,
        String property2
    ) {
        StringBuilder criteriaSb = new StringBuilder(" ");
        criteriaSb.append(alias1);
        criteriaSb.append(prefix1);
        criteriaSb.append(":");
        criteriaSb.append(property1);
        criteriaSb.append(" = ");
        criteriaSb.append(alias2);
        criteriaSb.append(prefix2);
        criteriaSb.append(":");
        criteriaSb.append(property2);
        criteriaList.add(criteriaSb.toString());
    }

    private void addJoin(StringBuilder sb, String docType, String alias) {
        sb.append(", ");
        sb.append(docType);
        sb.append(" AS ");
        sb.append(alias);
        sb.append(" ");
    }

    private void addOrderClause(StringBuilder orderBySb, String order, String alias, String prefix, String property) {
        orderBySb.append(" ");
        orderBySb.append(alias);
        orderBySb.append(prefix);
        orderBySb.append(":");
        orderBySb.append(property);
        orderBySb.append(" ");
        orderBySb.append(order);
    }

    private List<String> getEtatMessageInclus(List<String> etatMessageExclus) {
        List<String> etatMessageInclus = new ArrayList<>();

        if (!etatMessageExclus.contains(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_AR_RECU)) {
            etatMessageInclus.add(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_AR_RECU);
        }
        if (!etatMessageExclus.contains(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EMIS)) {
            etatMessageInclus.add(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EMIS);
        }
        if (!etatMessageExclus.contains(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_ATTENTE_AR)) {
            etatMessageInclus.add(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_ATTENTE_AR);
        }
        if (!etatMessageExclus.contains(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_COURS_REDACTION)) {
            etatMessageInclus.add(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_COURS_REDACTION);
        }
        if (!etatMessageExclus.contains(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_COURS_TRAITEMENT)) {
            etatMessageInclus.add(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_COURS_TRAITEMENT);
        }
        if (!etatMessageExclus.contains(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_NON_TRAITE)) {
            etatMessageInclus.add(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_NON_TRAITE);
        }
        if (!etatMessageExclus.contains(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_TRAITE)) {
            etatMessageInclus.add(SolonEppLifecycleConstant.MESSAGE_ETAT_WS_TRAITE);
        }

        return etatMessageInclus;
    }

    /**
     * Retourne = ou Like suivant si le champ contient * ou %
     *
     * @param field
     * @return
     */
    private String getEqualsOrLikeClause(String field) {
        if (field.contains("*") || field.contains("%")) {
            return " LIKE ? ";
        } else {
            return " = ? ";
        }
    }

    /**
     * Recherche de messages par critères de recherche.
     *
     * @param criteria Critères de recherche
     * @return Liste de documents messages
     */
    public List<DocumentModel> list() {
        long pageSize = 0;
        long offset = 0;
        if (pageInfo != null) {
            pageSize = pageInfo.getPageSize();
            offset = pageInfo.getOffset();
        }

        long count = QueryHelper.doUfnxqlCountQuery(session, queryString, paramList.toArray());
        if (!QueryHelper.checkCount(session, count, queryString, paramList)) {
            // On ne retourne qu'un certain nombre de résultats
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            SolonEppConstant.MESSAGE_DOC_TYPE,
            queryString,
            paramList.toArray(),
            pageSize,
            offset
        );
    }

    /**
     * Compte le nombre de documents retournés par les critères de recherche.
     *
     * @param criteria Critères de recherche
     * @return Nombre de documents messages
     */
    public long count() {
        return QueryUtils.doCountQuery(
            session,
            FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + queryString,
            paramList.toArray()
        );
    }

    /**
     * Recherche de messages par critères de recherche. Retourne un unique résultat.
     *
     * @param criteria Critères de recherche
     * @return Document message
     */
    public DocumentModel getSingleResult() {
        List<DocumentModel> list = list();

        if (list == null || list.isEmpty()) {
            return null;
        }

        if (list.size() > 1) {
            throw new NuxeoException("La requête doit retourner résultat unique: " + criteria);
        }

        return list.get(0);
    }

    /**
     * Recherche de messages par critères de recherche. Retourne plusieurs
     * résultats.
     *
     * @param criteria Critères de recherche
     * @return List<DocumentModel> messages
     */
    public List<DocumentModel> getMultipleResults() {
        List<DocumentModel> list = list();

        if (list == null || list.isEmpty()) {
            return null;
        }

        return list;
    }

    /**
     * Retourne la chaîne de la requête après construction.
     *
     * @return queryString
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Retourne la liste des paramètres de la requête après construction.
     *
     * @return paramList
     */
    public List<Object> getParamList() {
        return paramList;
    }

    private Calendar getDateTraitementMinFromParam() {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        if (criteria.getDateTraitementMin() != null) {
            return criteria.getDateTraitementMin();
        }

        String nbJour = null;
        try {
            nbJour = paramService.getParametreValue(session, SolonEppParametreConstant.NB_JOUR_MESSAGE_AFFICHABLE);
        } catch (NuxeoException exc) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_GET_PARAM_TEC,
                SolonEppParametreConstant.NB_JOUR_MESSAGE_AFFICHABLE
            );
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_PARAM_TEC, exc);
        }
        Integer nb = null;
        if (StringUtils.isNotBlank(nbJour)) {
            nb = Integer.parseInt(nbJour);
        } else {
            nb = 10;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -nb);
        return cal;
    }
}
