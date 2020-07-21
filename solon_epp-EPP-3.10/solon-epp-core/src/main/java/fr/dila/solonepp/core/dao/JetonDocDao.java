package fr.dila.solonepp.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.JetonDocCriteria;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dao.pagination.PageInfo;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;

/**
 * DAO des JetonDoc.
 * 
 * @author jtremeaux
 */
public class JetonDocDao {
    /**
     * Session.
     */
    private final CoreSession session;

    /**
     * Paramètres de recherche.
     */
    private final JetonDocCriteria criteria;

    /**
     * Informations de pagination.
     */
    private final PageInfo pageInfo;

    /**
     * Chaîne de la requête après construction après construction de la requête.
     */
    private String queryString;

    /**
     * Paramètres après construction de la requête.
     */
    private List<Object> paramList;

    /**
     * Constructeur de JetonDocDao.
     * 
     * @param session Session
     * @throws ClientException
     */
    public JetonDocDao(final CoreSession session, final JetonDocCriteria criteria, final PageInfo pageInfo) throws ClientException {
        this.session = session;
        this.criteria = criteria;
        this.pageInfo = pageInfo;

        build();
    }

    /**
     * Construit la chaîne de la requete et la liste des paramètres.
     * 
     * @throws ClientException
     */
    protected void build() throws ClientException {
        final StringBuilder query = new StringBuilder("SELECT j.ecm:uuid as id, j.");
        query.append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
        query.append(":");
        query.append(STSchemaConstant.JETON_DOC_CREATED_PROPERTY);
        query.append(" FROM ");
        query.append(STConstant.JETON_DOC_TYPE);
        query.append(" AS j ");

        final List<String> criterialist = new ArrayList<String>();

        paramList = new ArrayList<Object>();
        if (StringUtils.isNotBlank(criteria.getJetonType())) {
            final StringBuilder sb = new StringBuilder(" j.");
            sb.append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.JETON_DOCUMENT_WEBSERVICE);
            sb.append(" = ? ");

            criterialist.add(sb.toString());

            paramList.add(criteria.getJetonType());
        }

        if (StringUtils.isNotBlank(criteria.getProprietaireId())) {
            final StringBuilder sb = new StringBuilder(" j.");
            sb.append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.JETON_DOCUMENT_ID_OWNER);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getProprietaireId());
        }

        if (criteria.getRetryGreaterThanZero() != null) {
            final StringBuilder sb = new StringBuilder(" j.");
            sb.append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.JETON_DOC_WS_RETRY_LEFT_PROPERTY);
            sb.append(" > 0 ");

            criterialist.add(sb.toString());
        }

        if (criteria.getCorbeille() != null) {
            final StringBuilder sb = new StringBuilder(" j.");
            sb.append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.JETON_DOC_MESSAGE_CORBEILLE_LIST_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getCorbeille());
        }

        if (criteria.getDateMax() != null) {
            final StringBuilder sb = new StringBuilder(" j.");
            sb.append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.JETON_DOC_CREATED_PROPERTY);
            sb.append(" > ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getDateMax());
        }

        if (criteria.getEvenementId() != null) {
            final StringBuilder sb = new StringBuilder(" j.");
            sb.append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.JETON_DOC_EVENEMENT_ID_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getEvenementId());
        }

        if (criteria.getJetonId() != null) {
            final StringBuilder sb = new StringBuilder(" j.");
            sb.append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.JETON_DOC_JETON_ID_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getJetonId());
        }

        if (!criterialist.isEmpty()) {
            query.append(" WHERE ").append(StringUtils.join(criterialist, " AND "));
        }

        query.append(" ORDER BY j.").append(STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX);
        query.append(":");
        query.append(STSchemaConstant.JETON_DOC_CREATED_PROPERTY);
        query.append(" DESC");

        queryString = query.toString();
    }

    /**
     * Recherche de jetons par critères de recherche.
     * 
     * @param criteria Critères de recherche
     * @return Liste de documents messages
     * @throws ClientException
     */
    public List<DocumentModel> list() throws ClientException {
        long pageSize = 0;
        long offset = 0;
        if (pageInfo != null) {
            pageSize = pageInfo.getPageSize();
            offset = pageInfo.getOffset();
        }
        final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, STConstant.JETON_DOC_TYPE, queryString,
                paramList.toArray(), pageSize, offset);

        return list;
    }

    /**
     * Compte le nombre de jetons par critères de recherche.
     * 
     * @return Nombre de jetons
     * @throws ClientException
     */
    public Long count() throws ClientException {
        return QueryUtils.doCountQuery(session, FlexibleQueryMaker.KeyCode.UFXNQL.key + queryString, paramList.toArray());
    }
}
