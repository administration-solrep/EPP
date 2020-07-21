package fr.dila.solonepp.core.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.VersionCriteria;
import fr.dila.solonepp.api.dto.VersionSelectionDTO;
import fr.dila.solonepp.core.dto.VersionSelectionDTOImpl;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.StringUtil;

/**
 * DAO des versions des événements.
 * 
 * @author jtremeaux
 */
public class VersionDao {
    /**
     * Session.
     */
    private final CoreSession session;

    /**
     * Constructeur de VersionDao.
     * 
     * @param session Session
     */
    public VersionDao(final CoreSession session) {
        this.session = session;
    }

    /**
     * Recherche de versions par critères de recherche.
     * 
     * @param criteria Critères de recherche
     * @return Liste de versions
     * @throws ClientException
     */
    public List<DocumentModel> findVersionByCriteria(final VersionCriteria criteria) throws ClientException {
        final StringBuilder sb = new StringBuilder("SELECT v.ecm:uuid as id FROM ");
        final List<Object> paramList = getQuery(criteria, sb);

        final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, SolonEppConstant.VERSION_DOC_TYPE, sb.toString(),
                paramList.toArray());

        return list;
    }

    private List<Object> getQuery(final VersionCriteria criteria, final StringBuilder query) {
        query.append(SolonEppConstant.VERSION_DOC_TYPE);
        query.append(" AS v ");

        final List<Object> paramList = new ArrayList<Object>();

        final List<String> criterialist = new ArrayList<String>();

        if (StringUtils.isNotBlank(criteria.getEvenementId())) {
            final StringBuilder sb = new StringBuilder(" v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_EVENEMENT_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getEvenementId());
        }

        if (criteria.getNumeroVersionEquals() != null) {
            final StringBuilder sb = new StringBuilder(" v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_MAJOR_VERSION_PROPERTY);
            sb.append(" = ? AND v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_MINOR_VERSION_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());

            paramList.add(criteria.getNumeroVersionEquals().getMajorVersion());
            paramList.add(criteria.getNumeroVersionEquals().getMinorVersion());
        }

        if (criteria.getNumeroVersionNotEquals() != null) {
            final StringBuilder sb = new StringBuilder();

            sb.append(" (v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_MAJOR_VERSION_PROPERTY);
            sb.append(" != ? OR v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_MINOR_VERSION_PROPERTY);
            sb.append(" != ?) ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getNumeroVersionNotEquals().getMajorVersion());
            paramList.add(criteria.getNumeroVersionNotEquals().getMinorVersion());
        }

        if (criteria.getMajorVersionEquals() != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_MAJOR_VERSION_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getMajorVersionEquals());
        }

        if (criteria.getMinorVersionStrictlyGreater() != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_MINOR_VERSION_PROPERTY);
            sb.append(" > ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getMinorVersionStrictlyGreater());
        }

        if (criteria.isDateArNull()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_DATE_AR_PROPERTY);
            sb.append(" IS NULL ");

            criterialist.add(sb.toString());
        }

        if (criteria.isVersionCourante()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" v.");
            sb.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.VERSION_VERSION_COURANTE_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(1);
        }

        if (criteria.getCurrentLifeCycleState() != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" v.");
            sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getCurrentLifeCycleState());
        }

        if (criteria.getCurrentLifeCycleStateIn() != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" v.");
            sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE);
            sb.append(" IN (");
            sb.append(StringUtil.getQuestionMark(criteria.getCurrentLifeCycleStateIn().size()));
            sb.append(") ");

            criterialist.add(sb.toString());
            paramList.addAll(criteria.getCurrentLifeCycleStateIn());
        }

        if (!criterialist.isEmpty()) {
            query.append(" WHERE ").append(StringUtils.join(criterialist, " AND "));
        }

        if (criteria.isOrderVersionDesc()) {
            query.append(" ORDER BY v.");
            query.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX);
            query.append(":");
            query.append(SolonEppSchemaConstant.VERSION_MAJOR_VERSION_PROPERTY);
            query.append(" DESC, v.");
            query.append(SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX).append(":");
            query.append(SolonEppSchemaConstant.VERSION_MINOR_VERSION_PROPERTY);
            query.append(" DESC ");
        }

        return paramList;
    }

    /**
     * Recherche de messages par critères de recherche. Retourne un unique résultat.
     * 
     * @param criteria Critères de recherche
     * @return Liste de messages
     * @throws ClientException
     */
    public DocumentModel getSingleVersionByCriteria(final VersionCriteria criteria) throws ClientException {
        final List<DocumentModel> list = findVersionByCriteria(criteria);

        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.iterator().next();
    }

    public List<VersionSelectionDTO> findVersionSelectionnable(final VersionCriteria criteria) throws ClientException {
        final StringBuilder sb = new StringBuilder(
                "SELECT v.ecm:uuid as id, v.dc:title as title, v.ecm:currentLifeCycleState as etat, v.dc:description as description, v.ver:dateAr as dateAr FROM ");
        final List<Object> paramList = getQuery(criteria, sb);

        IterableQueryResult res = null;
        final List<VersionSelectionDTO> listResult = new ArrayList<VersionSelectionDTO>();
        try {
            res = QueryUtils.doUFNXQLQuery(session, sb.toString(), paramList.toArray());

            for (final Map<String, Serializable> result : res) {
                listResult.add(new VersionSelectionDTOImpl((String) result.get("id"), (String) result.get("title"), (String) result
                        .get("description"), (String) result.get("etat"), (Calendar) result.get("dateAr")));
            }
        } finally {
            if (res != null) {
                res.close();
            }
        }

        return listResult;
    }
}
