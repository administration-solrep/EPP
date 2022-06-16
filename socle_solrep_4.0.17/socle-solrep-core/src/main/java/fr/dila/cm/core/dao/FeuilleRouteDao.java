package fr.dila.cm.core.dao;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.criteria.FeuilleRouteCriteria;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * DAO des étapes de feuilles de routes.
 *
 * @author jtremeaux
 */
public class FeuilleRouteDao {
    /**
     * Critères de recherche.
     */
    protected FeuilleRouteCriteria criteria;

    /**
     * Chaîne de la requête après construction après construction de la requête.
     */
    protected String queryString;

    /**
     * Paramètres après construction de la requête.
     */
    protected List<Object> paramList;

    /**
     * Constructeur de FeuilleRouteDao.
     *
     */
    public FeuilleRouteDao(CoreSession session, FeuilleRouteCriteria criteria) {
        this.criteria = criteria;
        build(session);
    }

    /**
     * Construit la chaîne de la requete et la liste des paramètres.
     *
     */
    protected void build(final CoreSession session) {
        paramList = new ArrayList<>();
        StringBuilder sb = buildSelectFrom();

        boolean joinRouteStep =
            StringUtils.isNotBlank(criteria.getRoutingTaskType()) ||
            StringUtils.isNotBlank(criteria.getDistributionMailboxId()) ||
            criteria.getDeadline() != null ||
            criteria.getAutomaticValidation() != null ||
            criteria.getObligatoireSGG() != null ||
            criteria.getObligatoireMinistere() != null;

        if (joinRouteStep) {
            sb.append(", ").append(SSConstant.ROUTE_STEP_DOCUMENT_TYPE).append(" AS s ");
        }

        buildWhere(session, sb);

        if (joinRouteStep) {
            buildWhereJoin(sb);
        }

        queryString = sb.toString();
    }

    private void buildWhereJoin(StringBuilder sb) {
        sb
            .append(" AND r.")
            .append(STSchemaConstant.ECM_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.ECM_UUID_PROPERTY)
            .append(" = s.")
            .append(SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX)
            .append(":")
            .append(SSFeuilleRouteConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY);

        if (StringUtils.isNotBlank(criteria.getRoutingTaskType())) {
            sb
                .append(" AND s.")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_TYPE_PROPERTY)
                .append(" = ? ");
            paramList.add(criteria.getRoutingTaskType());
        }

        if (StringUtils.isNotBlank(criteria.getDistributionMailboxId())) {
            sb.append(" AND s.").append(SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_XPATH).append(" = ? ");
            paramList.add(criteria.getDistributionMailboxId());
        }

        if (criteria.getDeadline() != null) {
            sb
                .append(" AND s.")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_DEADLINE_PROPERTY)
                .append(" = ? ");
            paramList.add(criteria.getDeadline());
        }

        if (criteria.getAutomaticValidation() != null) {
            sb
                .append(" AND s.")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY)
                .append(" = ")
                .append(criteria.getAutomaticValidation() ? "1" : "0");
        }

        if (criteria.getObligatoireSGG() != null) {
            sb
                .append(" AND s.")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY)
                .append(" = ")
                .append(criteria.getObligatoireSGG() ? "1" : "0");
        }

        if (criteria.getObligatoireMinistere() != null) {
            sb
                .append(" AND s.")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY)
                .append(" = ")
                .append(criteria.getObligatoireMinistere() ? "1" : "0");
        }
    }

    protected void buildWhere(final CoreSession session, StringBuilder sb) {
        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        final DocumentModel feuilleRouteModelFolder = feuilleRouteModelService.getFeuilleRouteModelFolder(session);

        sb
            .append(" WHERE r.")
            .append(STSchemaConstant.ECM_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.ECM_PARENT_ID_PROPERTY)
            .append(" = ? ");
        paramList.add(feuilleRouteModelFolder.getId());

        if (criteria.isCheckReadPermission()) {
            final StringBuilder sb2 = new StringBuilder("r.")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY);

            SSPrincipal principal = (SSPrincipal) session.getPrincipal();
            sb.append(feuilleRouteModelService.getMinistereCriteria(principal, sb2.toString()));
        }

        if (criteria.isDemandeValidation() != null) {
            sb
                .append(" AND r.")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY)
                .append(" = ")
                .append(criteria.isDemandeValidation() ? "1" : "0");
        }

        if (StringUtils.isNotBlank(criteria.getCurrentLifeCycleState())) {
            sb
                .append(" AND r.")
                .append(STSchemaConstant.ECM_SCHEMA_PREFIX)
                .append(":")
                .append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE)
                .append(" = ? ");
            paramList.add(criteria.getCurrentLifeCycleState());
        }

        if (StringUtils.isNotBlank(criteria.getCreationUtilisateur())) {
            sb
                .append(" AND r.")
                .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
                .append(":")
                .append(STSchemaConstant.DUBLINCORE_CREATOR_PROPERTY)
                .append(" = ? ");
            paramList.add(criteria.getCreationUtilisateur());
        }

        if (criteria.getCreationDateMin() != null) {
            sb
                .append(" AND r.")
                .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
                .append(":")
                .append(STSchemaConstant.DUBLINCORE_CREATED_PROPERTY)
                .append(" >= ? ");
            paramList.add(criteria.getCreationDateMin());
        }

        if (criteria.getCreationDateMax() != null) {
            sb
                .append(" AND r.")
                .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
                .append(":")
                .append(STSchemaConstant.DUBLINCORE_CREATED_PROPERTY)
                .append(" <= ? ");
            paramList.add(criteria.getCreationDateMax());
        }

        if (criteria.isFeuilleRouteDefaut()) {
            sb
                .append(" AND r.")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_DEFAUT_PROPERTY)
                .append(" = 1 ");
        }

        if (StringUtils.isNotBlank(criteria.getMinistere())) {
            sb
                .append(" AND r.")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY)
                .append(" = ? ");
            paramList.add(criteria.getMinistere());
        } else if (criteria.getMinistereNull()) {
            sb
                .append(" AND ( r.")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY)
                .append(" IS NULL");
            sb
                .append(" OR r.")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY)
                .append(" ='' )");
        }

        if (StringUtils.isNotBlank(criteria.getIntitule())) {
            sb
                .append(" AND r.")
                .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
                .append(":")
                .append(STSchemaConstant.DUBLINCORE_TITLE_PROPERTY)
                .append(" ILIKE ? ");
            paramList.add(criteria.getIntitule().replace("*", "%"));
        } else if (criteria.getIntituleNull()) {
            sb
                .append(" AND ( r.")
                .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
                .append(":")
                .append(STSchemaConstant.DUBLINCORE_TITLE_PROPERTY)
                .append(" IS NULL ");
            sb
                .append(" OR r.")
                .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
                .append(":")
                .append(STSchemaConstant.DUBLINCORE_TITLE_PROPERTY)
                .append(" ='' )");
        }
    }

    protected StringBuilder buildSelectFrom() {
        return buildSelect().append(" FROM ").append(SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE).append(" AS r ");
    }

    protected StringBuilder buildSelect() {
        return new StringBuilder("SELECT DISTINCT r.")
            .append(STSchemaConstant.ECM_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.ECM_UUID_PROPERTY)
            .append(" AS id, r.")
            .append(STSchemaConstant.ECM_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE)
            .append(", r.")
            .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.DUBLINCORE_TITLE_PROPERTY)
            .append(", r.")
            .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
            .append(":")
            .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY)
            .append(", r.")
            .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.DUBLINCORE_CREATOR_PROPERTY)
            .append(", r.")
            .append(STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.DUBLINCORE_MODIFIED_PROPERTY)
            .append(", r.")
            .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
            .append(":")
            .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY);
    }

    // /**
    // * Recherche de feuilles de route par critères de recherche.
    // *
    // * @param criteria Critères de recherche
    // * @return Liste de feuilles de route
    // */
    // public List<DocumentModel> list(final CoreSession session){
    // return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, queryString, paramList.toArray());
    // }

    // /**
    // * Recherche d'étapes de feuille de route par critères de recherche.
    // * Retourne un unique résultat.
    // *
    // * @param criteria Critères de recherche
    // * @return Liste de feuilles de route
    // */
    // public DocumentModel getSingleResult(final CoreSession session){
    // List<DocumentModel> list = list(session);
    //
    // if (list == null || list.isEmpty()) {
    // return null;
    // }
    //
    // if (list.size() > 1) {
    // throw new NuxeoException("La requête doit retourner résultat unique: " + criteria);
    // }
    //
    // return list.iterator().next();
    // }

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
}
