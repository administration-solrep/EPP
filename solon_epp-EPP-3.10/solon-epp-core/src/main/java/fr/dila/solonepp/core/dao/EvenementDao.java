package fr.dila.solonepp.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.EvenementCriteria;
import fr.dila.st.core.query.QueryUtils;

/**
 * DAO des événements.
 * 
 * @author jtremeaux
 */
public class EvenementDao {
    /**
     * Session.
     */
    private final CoreSession session;

    /**
     * Constructeur de EvenementDao.
     * 
     * @param session Session
     */
    public EvenementDao(final CoreSession session) {
        this.session = session;
    }

    /**
     * Recherche de versions par critères de recherche.
     * 
     * @param criteria Critères de recherche
     * @return Liste de versions
     * @throws ClientException
     */
    public List<DocumentModel> findEvenementByCriteria(final EvenementCriteria criteria) throws ClientException {
        final StringBuilder sb = new StringBuilder("SELECT e.ecm:uuid as id FROM ").append(SolonEppConstant.EVENEMENT_DOC_TYPE).append(" AS e ");

        final List<String> criterialist = new ArrayList<String>();

        final List<Object> paramList = new ArrayList<Object>();
        if (StringUtils.isNotBlank(criteria.getEvenementId())) {

            final StringBuilder query = new StringBuilder(" e.");
            query.append(SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX);
            query.append(":");
            query.append(SolonEppSchemaConstant.ID_EVENEMENT);
            query.append(" = ? ");

            criterialist.add(query.toString());
            paramList.add(criteria.getEvenementId());
        }

        if (StringUtils.isNotBlank(criteria.getEvenementParentId())) {

            final StringBuilder query = new StringBuilder(" e.");
            query.append(SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX);
            query.append(":");
            query.append(SolonEppSchemaConstant.EVENEMENT_EVENEMENT_PARENT_PROPERTY);
            query.append(" = ? ");

            criterialist.add(query.toString());
            paramList.add(criteria.getEvenementParentId());
        }

        if (StringUtils.isNotBlank(criteria.getDossierId())) {

            final StringBuilder query = new StringBuilder(" e.");
            query.append(SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX);
            query.append(":");
            query.append(SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY);
            query.append(" = ? ");

            criterialist.add(query.toString());
            paramList.add(criteria.getDossierId());
        }

        if (!criterialist.isEmpty()) {
            sb.append(" WHERE ").append(StringUtils.join(criterialist, " AND "));
        }

        if (criteria.isOrderByIdEvenement()) {
            sb.append(" ORDER BY e.").append(SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.ID_EVENEMENT);
        }

        final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, SolonEppConstant.EVENEMENT_DOC_TYPE, sb.toString(),
                paramList.toArray());

        return list;
    }

    /**
     * Recherche de messages par critères de recherche. Retourne un unique résultat.
     * 
     * @param criteria Critères de recherche
     * @return Liste de messages
     * @throws ClientException
     */
    public DocumentModel getSingleEvenementByCriteria(final EvenementCriteria criteria) throws ClientException {
        final List<DocumentModel> list = findEvenementByCriteria(criteria);

        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.iterator().next();
    }

    /**
     * Recherche de les évènements racines.
     * 
     * @param criteria Critères de recherche
     * @return Liste d'évènements racines
     * @throws ClientException
     */
    public List<DocumentModel> getEvenementsRacine(final EvenementCriteria criteria) throws ClientException {

        if (criteria == null || StringUtils.isEmpty(criteria.getDossierId())) {
            throw new ClientException("dossierTitle ne doit pas être null");
        }

        final StringBuilder query = new StringBuilder("SELECT e.ecm:uuid as id FROM ");
        query.append(SolonEppConstant.EVENEMENT_DOC_TYPE);
        query.append(" AS e WHERE  ");

        query.append(" e.");
        query.append(SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX);
        query.append(":");
        query.append(SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY);
        query.append(" = ? ");

        query.append(" AND e.");
        query.append(SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX);
        query.append(":");
        query.append(SolonEppSchemaConstant.EVENEMENT_EVENEMENT_PARENT_PROPERTY);
        query.append(" IS NULL ");

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, SolonEppConstant.EVENEMENT_DOC_TYPE, query.toString(),
                new Object[] { criteria.getDossierId() });
    }
}
