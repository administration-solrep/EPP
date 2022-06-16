package fr.dila.st.core.service;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.util.CoreSessionUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.platform.audit.api.AuditLogger;
import org.nuxeo.ecm.platform.audit.api.AuditReader;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.service.DefaultAuditBackend;
import org.nuxeo.ecm.platform.audit.service.NXAuditEventsService;
import org.nuxeo.runtime.api.Framework;

/**
 * Service permettant de requêter le journal d'Audit.
 *
 * @author bby
 * @author SPL
 * @author ARN
 */
public class JournalServiceImpl implements JournalService {
    private static final Log LOGGER = LogFactory.getLog(JournalServiceImpl.class);

    /**
     * Default constructor
     */
    public JournalServiceImpl() {
        // do nothing
    }

    @Override
    public int getEventsCount(List<String> dossierIds, Map<String, Object> mapFilter) {
        // on construit la clause where à partir des paramètres
        WhereClause whereClause = buildQueryWhereClauseWithParameter(dossierIds, mapFilter);
        StringBuilder query = new StringBuilder("select count(log.id) from LogEntry log");
        if (StringUtils.isNotEmpty(whereClause.clause)) {
            query.append(" where ");
            query.append(whereClause.clause);
        }

        @SuppressWarnings("unchecked")
        List<Long> res = (List<Long>) getAuditReader().nativeQuery(query.toString(), whereClause.params, 1, 20);
        return res.get(0).intValue();
    }

    /*
     * Supprime les logs anterieur à dateLimit
     * @see fr.dila.st.api.service.JournalService#purger()
     */
    @Override
    public void purger(final Date dateLimit) {
        getAuditBackend()
            .getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    purger(entityManager, dateLimit);
                }
            );
    }

    /**
     * supprimer tous les logs d'un document
     *
     * @param docId
     *            the document Id
     * @throws ClientException
     */
    @Override
    public void purger(final String docId) {
        getAuditBackend()
            .getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    purger(entityManager, docId);
                }
            );
    }

    /*
     * Supprime les logs anterieur à dateLimit
     */
    private void purger(EntityManager entityManager, Date dateLimit) {
        LOGGER.info("PURGER : log before " + dateLimit);

        // String requete = "delete from LogEntry log where log.eventDate <= :dateLimit";
        //
        // Query q = em.createQuery(requete);
        // q.setParameter("dateLimit", dateLimit);
        // int nb = q.executeUpdate();
        // log.info("PURGER " + nb);
        // FAILED BECAUSE DELETE ON CASCASE MISSING

        String requete = "from LogEntry log where log.eventDate <= :dateLimit";
        Query query = entityManager.createQuery(requete);
        query.setParameter("dateLimit", dateLimit);

        @SuppressWarnings("unchecked")
        List<LogEntry> logEntries = query.getResultList();

        int cpt = 0;
        for (LogEntry entry : logEntries) {
            try {
                entityManager.remove(entry);
                ++cpt;
            } finally {
                // Commit toutes les suppressions par lot de 100.
                if (cpt % 100 == 0 || cpt == logEntries.size()) {
                    entityManager.flush();
                }
            }
        }
        LOGGER.info("PURGER " + cpt);
    }

    private static class WhereClause {
        private String clause;
        private String orderClause;
        private Map<String, Object> params;

        public WhereClause(String whereClause, String orderClause, Map<String, Object> params) {
            this.clause = whereClause;
            this.orderClause = orderClause;
            this.params = params;
        }

        public boolean hasParams() {
            return params != null && !params.isEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    private List<LogEntry> queryLogs(WhereClause whereClause, int pageNb, int pageSize) {
        StringBuilder request = new StringBuilder("from LogEntry log");
        if (whereClause != null) {
            if (whereClause.hasParams()) {
                if (whereClause.clause != null && !whereClause.clause.isEmpty()) {
                    request.append(" where " + whereClause.clause + " " + whereClause.orderClause);
                } else {
                    request.append(whereClause.orderClause);
                }
                return (List<LogEntry>) getAuditReader()
                    .nativeQuery(request.toString(), whereClause.params, pageNb, pageSize);
            } else {
                // HACK si la requete est vide, on renvoie une assertion WHERE toujours vraie pour obtenir tous les
                // éléments
                if (whereClause.clause == null || whereClause.clause.isEmpty()) {
                    request.append(" where 1=1" + " " + whereClause.orderClause);
                } else {
                    request.append(" where " + whereClause.clause + " " + whereClause.orderClause);
                }
            }
        }
        return (List<LogEntry>) nativeQuery(request.toString(), pageNb, pageSize);
    }

    private List<?> nativeQuery(final String query, final int pageNb, final int pageSize) {
        return getAuditReader().nativeQueryLogs(query, pageNb, pageSize);
    }

    /**
     * contruit la requête pour récupérer les événements du log de l'espace d'administration
     *
     * @param docPathParent
     * @param categories
     * @param dateStart
     * @param pageNb
     * @param pageSize
     * @return
     */
    private WhereClause buildQueryWhereClauseWithParameter(List<String> dossierIds, Map<String, Object> mapFilter) {
        return buildQueryWhereClauseWithParameterAndOrder(dossierIds, mapFilter, null);
    }

    private WhereClause buildQueryWhereClauseWithParameterAndOrder(
        List<String> dossierIds,
        Map<String, Object> mapFilter,
        List<SortInfo> sortInfos
    ) {
        Map<String, Object> params = new HashMap<>();
        // initialisations requete
        String queryStr = "";
        StringBuffer buffer = new StringBuffer(queryStr);
        Boolean isFirstElement = true;

        int idx = 0;

        // filtre sur les identifiants de dossier
        if (dossierIds != null && !dossierIds.isEmpty()) {
            for (String idDossier : dossierIds) {
                String paramKey = "param" + idx;
                if (isFirstElement) {
                    buffer.append("( log.docUUID = :");
                    isFirstElement = false;
                } else {
                    buffer.append("OR log.docUUID = :");
                }
                buffer.append(paramKey);
                buffer.append(' ');

                params.put(paramKey, idDossier);
                ++idx;
            }
            buffer.append(")");
        }

        // filtres à appliquer sur le dossier
        if (mapFilter != null) {
            Iterator<Entry<String, Object>> iteratorEntry = mapFilter.entrySet().iterator();
            Object value = null;
            String key = null;
            List<String> list = new ArrayList<>();
            while (iteratorEntry.hasNext()) {
                Entry<String, Object> entry = iteratorEntry.next();
                key = entry.getKey();
                value = entry.getValue();
                if (value instanceof String) {
                    String paramKey = "param" + idx;
                    String valueString = (String) value;
                    if (key.equals(STConstant.FILTER_COMMENT)) {
                        valueString = valueString.trim();
                    }
                    if (!valueString.isEmpty()) {
                        ajoutDebutFiltre(buffer, isFirstElement);
                        buffer.append(" log.");
                        buffer.append(key);
                        buffer.append(" LIKE :");
                        buffer.append(paramKey);
                        params.put(paramKey, valueString);
                        isFirstElement = false;
                    }
                } else if (value instanceof Date) {
                    // si on a pour filtre une date on regarder si il s'agit de la Date de debut ou de fin
                    Date valueDate = (Date) value;
                    if (key != null && key.equals(STConstant.FILTER_DATE_DEBUT)) {
                        ajoutDebutFiltre(buffer, isFirstElement);
                        buffer.append(" log.eventDate >= :dateDebut");
                        params.put("dateDebut", valueDate);
                        isFirstElement = false;
                    } else if (key != null && key.equals(STConstant.FILTER_DATE_FIN)) {
                        ajoutDebutFiltre(buffer, isFirstElement);
                        buffer.append(" log.eventDate <= :dateFin");
                        params.put("dateFin", valueDate);
                        isFirstElement = false;
                    }
                } else if (value instanceof Collection) {
                    // si on a pour filtre une list
                    list.clear();
                    @SuppressWarnings("unchecked")
                    Collection<String> values = (Collection<String>) value;
                    list.addAll(values);
                    if (!list.isEmpty()) {
                        ajoutDebutFiltre(buffer, isFirstElement);
                        int cpt = 0;
                        for (String valueString : list) {
                            String paramKey = "param" + idx;
                            if (cpt == 0) {
                                buffer.append(" ( ");
                            }
                            buffer.append(" log.");
                            buffer.append(STConstant.FILTER_CATEGORY);
                            buffer.append(" = :");
                            buffer.append(paramKey);
                            params.put(paramKey, valueString);
                            if (++cpt < list.size()) {
                                buffer.append(" OR ");
                            }
                            if (cpt == list.size()) {
                                buffer.append(" ) ");
                            }
                            ++idx;
                            isFirstElement = false;
                        }
                    }
                }
                ++idx;
            }
        }

        String orderStr = " ORDER BY log.eventDate DESC";

        if (sortInfos != null && !sortInfos.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder(" ORDER BY ");
            SortInfo sortInfo = null;
            for (int i = 0; i < sortInfos.size(); i++) {
                sortInfo = sortInfos.get(i);
                stringBuilder.append("log.");
                stringBuilder.append(sortInfo.getSortColumn());
                stringBuilder.append(" ");
                boolean sortAscending = sortInfo.getSortAscending();
                if (sortAscending) {
                    stringBuilder.append(" ASC ");
                } else {
                    stringBuilder.append(" DESC ");
                }
                if (i < sortInfos.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            orderStr = stringBuilder.toString();
        }

        queryStr = buffer.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queryLogs() =" + queryStr);
        }
        return new WhereClause(queryStr, orderStr, params);
    }

    /**
     * Construit le debut de la requete d'un élement du filtre.
     */
    private void ajoutDebutFiltre(StringBuffer buffer, Boolean isFirstElement) {
        if (!isFirstElement) {
            buffer.append(" AND");
        }
    }

    private AuditLogger getAuditLogger() {
        return ServiceUtil.getRequiredService(AuditLogger.class);
    }

    private AuditReader getAuditReader() {
        return ServiceUtil.getRequiredService(AuditReader.class);
    }

    /**
     * Access to the NXAuditEventsService that enable to do query on audit event
     */
    private DefaultAuditBackend getAuditBackend() {
        NXAuditEventsService auditService = (NXAuditEventsService) Framework
            .getRuntime()
            .getComponent(NXAuditEventsService.NAME);
        return (DefaultAuditBackend) auditService.getBackend();
    }

    /**
     * ajouter des logs journal
     *
     * @param entries
     *            leslogs à ajouter
     */
    @Override
    public void addLogEntries(List<LogEntry> entries) {
        getAuditLogger().addLogEntries(entries);
    }

    /**
     * supprimer tous les logs d'un document
     *
     * @param entityManager
     * @param docId
     *            the document Id
     */
    private void purger(EntityManager entityManager, String docId) {
        LOGGER.info("PURGER : log dossier " + docId);

        String requete = "from LogEntry log where log.docUUID='" + docId + "'";
        Query query = entityManager.createQuery(requete);

        @SuppressWarnings("unchecked")
        List<LogEntry> logEntries = query.getResultList();

        int cpt = 0;
        for (LogEntry entry : logEntries) {
            entityManager.remove(entry);
            ++cpt;
        }
        LOGGER.info("PURGER " + cpt);
    }

    @Override
    public List<LogEntry> queryDocumentAllLogs(
        List<String> dossierIds,
        Map<String, Object> mapFilter,
        int pageNb,
        int pageSize,
        List<SortInfo> sortInfos
    ) {
        // on construit la clause where à partir des paramètres
        WhereClause whereClause = buildQueryWhereClauseWithParameterAndOrder(dossierIds, mapFilter, sortInfos);

        return queryLogs(whereClause, pageNb, pageSize);
    }

    @Override
    public List<LogEntry> getLogEntries(String dossierId) {
        return queryDocumentAllLogs(Collections.singletonList(dossierId), null, 0, 0, null);
    }

    @Override
    public void journaliserActionFDR(CoreSession session, DocumentModel dossierDoc, String eventName, String comment) {
        journaliserAction(session, dossierDoc, eventName, comment, STEventConstant.CATEGORY_FEUILLE_ROUTE);
    }

    @Override
    public void journaliserActionBordereau(
        CoreSession session,
        DocumentModel dossierDoc,
        String eventName,
        String comment
    ) {
        journaliserAction(session, dossierDoc, eventName, comment, STEventConstant.CATEGORY_BORDEREAU);
    }

    @Override
    public void journaliserActionParapheur(
        CoreSession session,
        DocumentModel dossierDoc,
        String eventName,
        String comment
    ) {
        journaliserAction(session, dossierDoc, eventName, comment, STEventConstant.CATEGORY_PARAPHEUR);
    }

    @Override
    public void journaliserActionFDD(CoreSession session, DocumentModel dossierDoc, String eventName, String comment) {
        journaliserAction(session, dossierDoc, eventName, comment, STEventConstant.CATEGORY_FDD);
    }

    @Override
    public void journaliserActionAdministration(
        CoreSession session,
        DocumentModel dossierDoc,
        String eventName,
        String comment
    ) {
        journaliserActionForUser(
            session,
            session.getPrincipal(),
            dossierDoc,
            eventName,
            comment,
            STEventConstant.CATEGORY_ADMINISTRATION
        );
    }

    @Override
    public void journaliserActionAdministration(
        CoreSession session,
        NuxeoPrincipal principal,
        DocumentModel dossierDoc,
        String eventName,
        String comment
    ) {
        journaliserActionForUser(
            session,
            principal,
            dossierDoc,
            eventName,
            comment,
            STEventConstant.CATEGORY_ADMINISTRATION
        );
    }

    @Override
    public void journaliserActionAdministration(String username, String eventName, String comment) {
        CoreSessionUtil.doAction(
            username,
            session -> {
                journaliserActionAdministration(session, session.getPrincipal(), (String) null, eventName, comment);
            }
        );
    }

    @Override
    public void journaliserActionAdministration(CoreSession session, String eventName, String comment) {
        journaliserActionAdministration(session, session.getPrincipal(), (String) null, eventName, comment);
    }

    @Override
    public void journaliserActionAdministration(
        CoreSession session,
        String idDossier,
        String eventName,
        String comment
    ) {
        journaliserActionAdministration(session, session.getPrincipal(), idDossier, eventName, comment);
    }

    @Override
    public void journaliserActionAdministration(
        final CoreSession session,
        final NuxeoPrincipal principal,
        final String idDossier,
        final String eventName,
        final String comment
    ) {
        NuxeoPrincipal usedPrincipal = principal;
        if (principal == null) {
            usedPrincipal = session.getPrincipal();
        }
        EventContext evtContext = new EventContextImpl(session, usedPrincipal);
        if (StringUtils.isNotBlank(idDossier)) {
            evtContext.setProperty(STEventConstant.DOC_REF_PROPERTY, idDossier);
        }
        evtContext.setProperty(STEventConstant.COMMENT_PROPERTY, comment);
        evtContext.setProperty(STEventConstant.CATEGORY_PROPERTY, STEventConstant.CATEGORY_ADMINISTRATION);
        fireEventJournalisation(evtContext, eventName);
    }

    @Override
    public void journaliserAction(
        CoreSession session,
        DocumentModel dossierDoc,
        String eventName,
        String comment,
        String category
    ) {
        journaliserActionForUser(session, session.getPrincipal(), dossierDoc, eventName, comment, category);
    }

    @Override
    public void journaliserAction(
        String username,
        String idDossier,
        String eventName,
        String comment,
        String category
    ) {
        CoreSessionUtil.doAction(
            username,
            session -> {
                DocumentModel dossierDoc = session.getDocument(new IdRef(idDossier));
                journaliserActionForUser(session, session.getPrincipal(), dossierDoc, eventName, comment, category);
            }
        );
    }

    @Override
    public void journaliserActionForUser(
        CoreSession session,
        NuxeoPrincipal user,
        DocumentModel dossierDoc,
        String eventName,
        String comment,
        String category
    ) {
        DocumentEventContext evtContext = new DocumentEventContext(session, user, dossierDoc);
        evtContext.setComment(comment);
        evtContext.setCategory(category);
        fireEventJournalisation(evtContext, eventName);
    }

    private void fireEventJournalisation(EventContext evtContext, String eventName) {
        final EventProducer producer = STServiceLocator.getEventProducer();
        producer.fireEvent(evtContext.newEvent(eventName));
    }

    @Override
    public void journaliserActionPAN(
        CoreSession session,
        String idDossier,
        String eventName,
        String comment,
        String category
    ) {
        EventContext evtContext = new EventContextImpl(session, session.getPrincipal());
        evtContext.setProperty("idDossier", idDossier);
        evtContext.setProperty(STEventConstant.COMMENT_PROPERTY, comment);
        evtContext.setProperty(STEventConstant.CATEGORY_PROPERTY, category);
        fireEventJournalisation(evtContext, eventName);
    }
}
