package fr.sword.naiad.nuxeo.ufnxql.core.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.core.versioning.VersioningService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import fr.sword.naiad.nuxeo.commons.test.runner.OrderedRunner;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.CountMapper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.ListMapper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.mapper.RowMapper;

@RunWith(OrderedRunner.class)
@RepositoryConfig(init = FlexibleQueryMakerRepositoryInit.class, cleanup = Granularity.METHOD)
@Features({FlexibleQueryMakerFeature.class})
public class FlexibleQueryMakerTest {
    private static final Log LOG = LogFactory.getLog(FlexibleQueryMakerTest.class);

    protected CoreSession session;

    public static final String DOC_TYPE = "File";

    @Before
    public void init() throws NuxeoException {
        session = SessionUtil.openSession();
    }

    @After
    public void end() throws NuxeoException {
        SessionUtil.closeSession(session);
    }

    @Test
    public void a01testCountDocument() throws NuxeoException {
        long nbDocInserted = 0;
        final DocumentModel doc1 = session.createDocumentModel("/", "doc1", DOC_TYPE);
        session.createDocument(doc1);
        nbDocInserted++;

        DocumentModel doc2 = session.createDocumentModel("/", "doc2", DOC_TYPE);
        doc2 = session.createDocument(doc2);
        nbDocInserted++;
        session.save();

        final String selectQuery = "select * from " + DOC_TYPE;
        final DocumentModelList dml = session.query(selectQuery);
        assertEquals(nbDocInserted, dml.totalSize());

        Long nbDocCounted = QueryUtils.doCountQuery(session, selectQuery);
        assertNotNull(nbDocCounted);
        assertEquals(nbDocInserted, nbDocCounted.longValue());

        nbDocCounted = QueryUtils.doUnrestrictedCountQuery(session, selectQuery);
        assertNotNull(nbDocCounted);
        assertEquals(nbDocInserted, nbDocCounted.longValue());
    }

    @Test
    public void b01testDocument() throws NuxeoException {
        long nbDocInserted = 0;
        long nbDistinctTitle = 0;

        DocumentModel doc1 = session.createDocumentModel("/", "doc1", DOC_TYPE);
        doc1.setPropertyValue("dc:title", "doc1");
        doc1 = session.createDocument(doc1);
        nbDocInserted++;
        nbDistinctTitle++;

        DocumentModel doc2 = session.createDocumentModel("/", "doc2", DOC_TYPE);
        doc2.setPropertyValue("dc:title", "doc2");
        doc2 = session.createDocument(doc2);
        nbDocInserted++;
        nbDistinctTitle++;

        DocumentModel doc3 = session.createDocumentModel("/", "doc3", DOC_TYPE);
        doc3.setPropertyValue("dc:title", "doc2"); // same title as doc2
        doc3 = session.createDocument(doc3);
        nbDocInserted++;
        // nbDistinctTitle++; // same title
        session.save();

        {
            final String selectQuery = "select dc:title from " + DOC_TYPE;
            final IterableQueryResult res = session.queryAndFetch(selectQuery, FlexibleQueryMaker.QUERY_TYPE);

            long nbtitle = 0;
            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                assertEquals(1, m.keySet().size());
                // for (String s : m.keySet()) {
                // log.info("--> " + s + " = " + m.get(s));
                // }
                nbtitle++;
            }
            res.close();
            assertEquals(nbDocInserted, nbtitle);
        }

        {
            final String selectQuery = "select distinct dc:title from " + DOC_TYPE;
            final IterableQueryResult res = session.queryAndFetch(selectQuery, FlexibleQueryMaker.QUERY_TYPE);

            long nbtitle = 0;
            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                assertEquals(1, m.keySet().size());
                // for (String s : m.keySet()) {
                // log.info("--> " + s + " = " + m.get(s));
                // }
                nbtitle++;
            }
            res.close();
            assertEquals(nbDistinctTitle, nbtitle);
        }

        {
            final String selectQuery = "select title, count(*) from DUBLINCORE group by title";
            final String params = "[dc:title," + FlexibleQueryMaker.COL_COUNT + "]";
            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.SQL_CODE.getKey() + params + selectQuery, FlexibleQueryMaker.QUERY_TYPE);

            long nbtitle = 0;
            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                assertEquals(2, m.keySet().size());
                // for (String s : m.keySet()) {
                // log.info("-*-> " + s + " = " + m.get(s));
                // }
                final String title = (String) m.get("dc:title");
                final Long nb = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
                assertTrue(title.equals("doc1") || title.equals("doc2"));
                if (title.equals("doc1")) {
                    assertEquals(1, nb.longValue());
                } else if (title.equals("doc2")) {
                    assertEquals(2, nb.longValue());
                }
                nbtitle++;
            }
            res.close();
            assertEquals(nbDistinctTitle, nbtitle);
        }

        {

            final String selectQuery = "select title, count(*) from DUBLINCORE group by title";
            final IterableQueryResult res = QueryUtils.doSqlQuery(session,
                    new String[] { "dc:title", FlexibleQueryMaker.COL_COUNT }, selectQuery, null);

            long nbtitle = 0;
            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                assertEquals(2, m.keySet().size());
                // for (String s : m.keySet()) {
                // log.info("-*-> " + s + " = " + m.get(s));
                // }
                final String title = (String) m.get("dc:title");
                final Long nb = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
                assertTrue(title.equals("doc1") || title.equals("doc2"));
                if (title.equals("doc1")) {
                    assertEquals(1, nb.longValue());
                } else if (title.equals("doc2")) {
                    assertEquals(2, nb.longValue());
                }
                nbtitle++;
            }
            res.close();
            assertEquals(nbDistinctTitle, nbtitle);
        }

        {
            final String selectQuery = "select ecm:uuid, ecm:currentLifeCycleState, dc:title from " + DOC_TYPE;
            final IterableQueryResult res = session.queryAndFetch(selectQuery, FlexibleQueryMaker.QUERY_TYPE);

            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                for (final String s : m.keySet()) {
                    LOG.info("--> " + s + " = " + m.get(s));
                }
            }
            res.close();
        }

        {
            final String selectQuery = "select * from " + DOC_TYPE;
            final IterableQueryResult res = session.queryAndFetch(selectQuery, FlexibleQueryMaker.QUERY_TYPE);

            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                for (final String s : m.keySet()) {
                    LOG.info("--> " + s + " = " + m.get(s));
                }
            }
            res.close();
        }

        {
            final String selectQuery = "select distinct dc:title from " + DOC_TYPE;
            final Long nbtitle = QueryUtils.doCountQuery(session, selectQuery);
            assertEquals(nbDistinctTitle, nbtitle.longValue());
        }

        Assert.assertTrue(session.query("SELECT * FROM MaNote").isEmpty());
        
        long nbNoteInserted = 0;
        DocumentModel doc4 = session.createDocumentModel("/", "doc4", "MaNote");
        doc4.setPropertyValue("dc:title", "doc4");
        doc4 = session.createDocument(doc4);
        nbNoteInserted++;
        nbDistinctTitle++;
        session.save();
        
        Assert.assertEquals(1, session.query("SELECT * FROM MaNote").size());

        {
            final String selectQuery = "select count(*) from (({select * from MaNote}))";
            // String selectQuery =
            // "select count(*) from (select id from hierarchy) union (select id from dublincore))";
            final String params = "[" + FlexibleQueryMaker.COL_COUNT + "]";

            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.getKey() + params + selectQuery,
                    FlexibleQueryMaker.QUERY_TYPE);

            try {
                Long value = new Long(-1);
                final Iterator<Map<String, Serializable>> it = res.iterator();
                if (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.keySet().size());
                    value = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
                }
                assertEquals(nbNoteInserted, value.longValue());
            } finally {
                res.close();
            }
        }

        {
            final String selectQuery = "select count(*) from ({select * from File})";
            // String selectQuery =
            // "select count(*) from (select id from hierarchy) union (select id from dublincore))";
            final String params = "[" + FlexibleQueryMaker.COL_COUNT + "]";

            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.getKey() + params + selectQuery,
                    FlexibleQueryMaker.QUERY_TYPE);

            try {
                Long value = new Long(-1);
                final Iterator<Map<String, Serializable>> it = res.iterator();
                if (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.keySet().size());
                    value = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
                }
                assertEquals(nbDocInserted, value.longValue());
            } finally {
                res.close();
            }
        }

        {
            final String selectQuery = "select count(*) from (({select * from MaNote}) union ({select * from File}))";
            final String params = "[" + FlexibleQueryMaker.COL_COUNT + "]";

            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.getKey() + params + selectQuery,
                    FlexibleQueryMaker.QUERY_TYPE);

            try {
                Long value = new Long(-1);
                final Iterator<Map<String, Serializable>> it = res.iterator();
                if (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.keySet().size());
                    value = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
                }
                assertEquals(nbNoteInserted + nbDocInserted, value.longValue());
            } finally {
                res.close();
            }
        }

        {
            final String selectQuery = "select count(*) from (({select * from MaNote}) union ({select * from File}) union (select id from hierarchy))";
            final String params = "[" + FlexibleQueryMaker.COL_COUNT + "]";

            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.getKey() + params + selectQuery,
                    FlexibleQueryMaker.QUERY_TYPE);

            try {
                Long value = new Long(-1);
                final Iterator<Map<String, Serializable>> it = res.iterator();
                if (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.keySet().size());
                    value = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
                }
                assertEquals(2 * (nbNoteInserted + nbDocInserted), value.longValue());
            } finally {
                res.close();
            }
        }
    }

    @Test
    public void c01testPagination() throws NuxeoException {
        long nbDocInserted = 0;
        // long nbDistinctTitle = 0;
        DocumentModel doc1 = session.createDocumentModel("/", "doc1", DOC_TYPE);
        doc1.setPropertyValue("dc:title", "doc1");
        doc1 = session.createDocument(doc1);
        nbDocInserted++;
        // nbDistinctTitle++;

        DocumentModel doc2 = session.createDocumentModel("/", "doc2", DOC_TYPE);
        doc2.setPropertyValue("dc:title", "doc2");
        doc2 = session.createDocument(doc2);
        nbDocInserted++;
        // nbDistinctTitle++;

        DocumentModel doc3 = session.createDocumentModel("/", "doc3", DOC_TYPE);
        doc3.setPropertyValue("dc:title", "doc2"); // same title as doc2
        doc3 = session.createDocument(doc3);
        nbDocInserted++;
        // nbDistinctTitle++; // same title
        session.save();

        final String selectQuery = "select * from Document";
        {
            final IterableQueryResult res = session.queryAndFetch(selectQuery, FlexibleQueryMaker.QUERY_TYPE);
            assertEquals(nbDocInserted, res.size());
            res.close();
        }

        {
            final long limit = nbDocInserted - 1;
            final long offset = 0;
            assertTrue(limit > 0);
            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.PAGE.getKey() + "[" + limit + "]" + "[" + offset + "]" + selectQuery,
                    FlexibleQueryMaker.QUERY_TYPE);
            assertEquals(limit, res.size());
            res.close();
        }

        {
            final long limit = nbDocInserted;
            final long offset = 0;
            assertTrue(limit > 0);
            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.PAGE.getKey() + "[" + limit + "]" + "[" + offset + "]" + selectQuery,
                    FlexibleQueryMaker.QUERY_TYPE);
            assertEquals(limit, res.size());
            res.close();
        }

        {
            final long limit = nbDocInserted + 100;
            final long offset = 0;
            assertTrue(limit > 0);
            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.PAGE.getKey() + "[" + limit + "]" + "[" + offset + "]" + selectQuery,
                    FlexibleQueryMaker.QUERY_TYPE);
            assertEquals(nbDocInserted, res.size());
            res.close();
        }

        {
            final long limit = nbDocInserted + 100;
            final long offset = nbDocInserted - 2;
            assertTrue(limit > 0);
            final IterableQueryResult res = session.queryAndFetch(
                    FlexibleQueryMaker.KeyCode.PAGE.getKey() + "[" + limit + "]" + "[" + offset + "]" + selectQuery,
                    FlexibleQueryMaker.QUERY_TYPE);
            assertEquals(nbDocInserted - offset, res.size());
            res.close();
        }

        {
            final long limit = nbDocInserted + 100;
            final long offset = nbDocInserted - 2;
            assertTrue(limit > 0);
            final IterableQueryResult res = QueryUtils.doQueryAndFetchPaginated(session, selectQuery, limit, offset);
            assertEquals(nbDocInserted - offset, res.size());
            res.close();
        }

        {
            final String sqlquery = "SELECT * FROM HIERARCHY";
            final long limit = 2;
            final IterableQueryResult res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_ID },
                    sqlquery, null, limit, 0);
            assertEquals(limit, res.size());
            res.close();

        }
    }

    @Test
    public void d01testParam() throws NuxeoException {
        final String a_title = "doc'2";
        // long nbDocInserted = 0;
        // long nbDistinctTitle = 0;
        DocumentModel doc1 = session.createDocumentModel("/", "doc1", DOC_TYPE);
        doc1.setPropertyValue("dc:title", "doc1");
        doc1 = session.createDocument(doc1);
        // nbDocInserted++;
        // nbDistinctTitle++;

        DocumentModel doc2 = session.createDocumentModel("/", "doc2", DOC_TYPE);
        doc2.setPropertyValue("dc:title", a_title);
        doc2 = session.createDocument(doc2);
        // nbDocInserted++;
        // nbDistinctTitle++;

        DocumentModel doc3 = session.createDocumentModel("/", "doc3", DOC_TYPE);
        doc3.setPropertyValue("dc:title", a_title); // same title as doc2
        doc3 = session.createDocument(doc3);
        // nbDocInserted++;
        // nbDistinctTitle++; // same title
        session.save();

        final String selectQuery = "select count(*) from DUBLINCORE where title LIKE ?";

        final IterableQueryResult res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_COUNT },
                selectQuery, new Object[] { a_title });

        final Iterator<Map<String, Serializable>> it = res.iterator();
        while (it.hasNext()) {
            final Map<String, Serializable> m = it.next();
            assertEquals(1, m.keySet().size());
            for (final String s : m.keySet()) {
                LOG.info("-*-> " + s + " = " + m.get(s));
            }
            final Long nb = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
            assertEquals(2, nb.longValue());
        }
        res.close();
    }

    @Test
    public void e01testUFNXQL() throws NuxeoException {
        final String titreUnique = "doc1";
        long nbDocInserted = 0;
        long nbDistinctTitle = 0;

        DocumentModel doc1 = session.createDocumentModel("/", "doc1", DOC_TYPE);
        doc1.setPropertyValue("dc:title", titreUnique);
        doc1 = session.createDocument(doc1);
        nbDocInserted++;
        nbDistinctTitle++;

        DocumentModel doc2 = session.createDocumentModel("/", "doc2", DOC_TYPE);
        doc2.setPropertyValue("dc:title", "doc2");
        doc2 = session.createDocument(doc2);
        nbDocInserted++;
        nbDistinctTitle++;

        DocumentModel doc3 = session.createDocumentModel("/", "doc3", DOC_TYPE);
        doc3.setPropertyValue("dc:title", "doc2"); // same title as doc2
        doc3 = session.createDocument(doc3);
        nbDocInserted++;
        // nbDistinctTitle++; // same title
        session.save();

        // String query = FlexibleQueryMaker.KeyCode.UFXNQL.key +
        // "select q.ecm:uuid from Question AS q where q.qu:num_question > P";
        // String query = FlexibleQueryMaker.KeyCode.UFXNQL.key +
        // "select q.ecm:uuid,q.dc:title from File AS q where (q.dc:title='toto' OR q.dc:title=P) AND q.dc:title IS NOT NULL ORDER BY q.dc:title";
        // String query = FlexibleQueryMaker.KeyCode.UFXNQL.key +
        // "select q.ecm:uuid,q.dc:title from File AS q where (q.dc:title='toto' OR q.dc:title=P) AND q.dc:title IS NOT NULL ";
        // String query = FlexibleQueryMaker.KeyCode.UFXNQL.key +
        // "select q.ecm:uuid,q.dc:title,count() from File AS q where q.dc:title=10 and f() = 1 ORDER BY q.dc:title DESC";
        {
            final String query = "select count() AS c from File AS f";
            final Object params[] = null;
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }
                    assertEquals(new Long(nbDocInserted), m.get("c"));
                }
            } finally {
                res.close();
            }
        }

        {
            final String query = "select distinct f.dc:title from File AS f";
            final Object params[] = null;
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }
                }
                assertEquals(nbDistinctTitle, res.size());
            } finally {
                res.close();
            }
        }

        {
            final String query = "select f.dc:title, count() from File AS f group by f.dc:title";
            final Object params[] = null;
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(2, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }
                }
                assertEquals(nbDistinctTitle, res.size());
            } finally {
                res.close();
            }
        }

        {
            // retourne les doc File dont le titre match le parametre est dont
            // l'état est different de deleted
            final String query = "select f.ecm:uuid as id from File AS f where f.dc:title = ? and f.ecm:currentLifeCycleState != 'deleted'";
            final Object params[] = new Object[] { titreUnique };
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }
                    assertEquals(doc1.getId(), m.get("id"));
                }
                assertEquals(1, res.size());
            } finally {
                res.close();
            }
        }

        {
            // retourne les doc File dont le titre match le parametre est dont
            // l'état est deleted
            final String query = "select f.ecm:uuid as id from File AS f where f.dc:title = ? and f.ecm:currentLifeCycleState = 'deleted'";
            final Object params[] = new Object[] { titreUnique };
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                assertEquals(0, res.size());
            } finally {
                res.close();
            }
        }

        {
            // retourne les combinaison de doc File qui ont le même titre
            final String query = "select f1.ecm:uuid as id1, f2.ecm:uuid as id2 from File AS f1, File AS f2 where f1.dc:title = f2.dc:title and f1.ecm:uuid != f2.ecm:uuid";
            final Object params[] = null;// new Object[]{titreUnique};
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(2, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                    assertTrue(m.get("id1").equals(doc2.getId()) || m.get("id1").equals(doc3.getId()));
                    assertTrue(m.get("id2").equals(doc2.getId()) || m.get("id2").equals(doc3.getId()));
                }
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }

        final DocumentModelList dml = session.query("select * from Document");
        LOG.error("nb doc " + dml.size());

        DocumentModel note1 = session.createDocumentModel("/", "note1", "MaNote");
        note1.setPropertyValue("dc:title", "docNote1");
        note1 = session.createDocument(note1);

        DocumentModel note2 = session.createDocumentModel("/", "note2", "MaNote");
        note2.setPropertyValue("dc:title", "doc1");
        note2 = session.createDocument(note2);

        DocumentModel note3 = session.createDocumentModel("/", "note3", "MaNote");
        note3.setPropertyValue("dc:title", "docNote3");
        note3 = session.createDocument(note3);
        final int nbNote = 3;
        session.save();
        ServiceUtil.getRequiredService(EventService.class).waitForAsyncCompletion();

        TransactionHelper.commitOrRollbackTransaction();
        SessionUtil.closeSession(session);

        TransactionHelper.startTransaction();
        session = SessionUtil.openSession();

        {
            // retourne le nombre de Note
            final String query = "select count() as c from MaNote AS n";
            final Object params[] = null;// new Object[]{titreUnique};
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }
                    assertEquals(new Long(nbNote), m.get("c"));
                }
                assertEquals(1, res.size());
            } finally {
                res.close();
            }
        }

        {
            // retrourne les doc File dont le titre est commun avec celui d'un
            // doc Note
            final String query = "select f.ecm:uuid as id from File AS f, MaNote AS n WHERE n.dc:title = f.dc:title";
            final Object params[] = null;
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }
                    assertEquals(doc1.getId(), m.get("id"));
                }
                assertEquals(1, res.size());
            } finally {
                res.close();
            }
        }

        {
            // retrourne les doc File dont le titre est commun avec celui d'un
            // doc Note
            final String query = "select f.ecm:uuid as id from File AS f, MaNote AS n WHERE n.dc:title = f.dc:title";
            final Object params[] = null;
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }
                    assertEquals(doc1.getId(), m.get("id"));
                }
                assertEquals(1, res.size());
            } finally {
                res.close();
            }
        }

        {
            // retrourne les doc File dont le titre est commun avec celui d'un
            // doc Note
            final String query = "select n.dc:title from MaNote AS n WHERE n.ecm:uuid IN (?, ?)";// '"+note1.getId()+",
            // "+note3.getId()+"')";
            final Object params[] = new Object[] { note1.getId(), note3.getId() };
            final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                    FlexibleQueryMaker.QUERY_TYPE, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }
                    final String title = (String) m.get("n.dc:title");
                    assertNotNull(title);
                    // should get note1 and note3 : theri title begin with
                    // docNote
                    assertTrue(title.startsWith("docNote"));
                }
                // should get 2 elements note1 and note3
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }

        {
            // recupere des doc en se basant sur le debut du titre
            // recupere note1 et note3

            final String query = "select d.ecm:uuid as id, d.dc:title from Document AS d where d.dc:title ILIKE 'docnote%'";
            final Object params[] = null;
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(2, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }

        // SPL indexation fulltext appelé en post comit , apres commit d'une transaction
        // ici pas de transaction, l'indexation fulltext n'est pas faite => pas de résultat
        {
            LOG.debug("test");
            final String query = "select d.ecm:uuid from Document AS d where d.ecm:fulltext = 'doc1'";
            final Object params[] = null;
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.error(e.getKey() + "-=->" + e.getValue() + "--"
                                + session.getDocument(new IdRef((String) e.getValue())).getType());
                    }

                }
                // on devrait avoir note2 et doc1
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }

        {
            final String query = "select d1.ecm:uuid, d2.ecm:uuid, d1.dc:description from Document AS d1, Document AS d2 where d1.ecm:fulltext = 'doc1' AND d2.ecm:fulltext = 'doc1' AND d1.dc:description IS NULL";
            final Object params[] = null;
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(3, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(4, res.size());
            } finally {
                res.close();
            }
        }

    }

    /**
     * test de requete avec isChildOf
     * 
     * @throws NuxeoException
     */
    @Test
    public void f01testRecQueryOnPath() throws NuxeoException {
        DocumentModel folder1 = session.createDocumentModel("Folder");
        folder1.setPathInfo("/", "nom_workspace_folder1");
        folder1.setPropertyValue("dc:title", "MyFolder1");
        folder1 = session.createDocument(folder1);

        DocumentModel folder2 = session.createDocumentModel("Folder");
        folder2.setPathInfo("/", "nom_workspace_folder2");
        folder2.setPropertyValue("dc:title", "MyFolder2");
        folder2 = session.createDocument(folder2);

        LOG.debug(folder1.getPathAsString());
        LOG.debug(folder2.getPathAsString());

        DocumentModel note1 = session.createDocumentModel("/", "note1", "MaNote");
        note1.setPropertyValue("dc:title", "docNote1");
        note1 = session.createDocument(note1);

        DocumentModel note11 = session.createDocumentModel(folder1.getPathAsString(), "note11", "MaNote");
        note11.setPropertyValue("dc:title", "docNote1");
        note11 = session.createDocument(note11);

        DocumentModel note12 = session.createDocumentModel(folder1.getPathAsString(), "note12", "MaNote");
        note12.setPropertyValue("dc:title", "docNote2");
        note12 = session.createDocument(note12);

        DocumentModel note21 = session.createDocumentModel(folder2.getPathAsString(), "note21", "MaNote");

        note21.setPropertyValue("dc:title", "docNote1");
        note21 = session.createDocument(note21);

        DocumentModel note22 = session.createDocumentModel(folder2.getPathAsString(), "note22", "MaNote");

        note22.setPropertyValue("dc:title", "docNote2");
        note22 = session.createDocument(note22);

        folder1 = session.saveDocument(folder1);
        folder2 = session.saveDocument(folder2);

        session.save();

        LOG.debug(session.getParentDocument(note11.getRef()));

        DocumentModelList l = session.getChildren(folder1.getRef());
        assertEquals(2, l.size());

        l = session.getChildren(folder2.getRef());
        assertEquals(2, l.size());

        {
            final String query = "select d.ecm:uuid from Folder AS d WHERE d.dc:title LIKE 'MyFolder%'";
            final Object params[] = null;
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }

        {
            final String query = "select d.ecm:uuid from MaNote AS d";
            final Object params[] = null;
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(5, res.size());
            } finally {
                res.close();
            }
        }

        {
            final String query = "select d.ecm:uuid from MaNote AS d WHERE isChildOf(d.ecm:uuid, select f.ecm:uuid from Folder AS f WHERE f.dc:title LIKE ?) = 1";
            final Object params[] = new Object[] { "MyFolder1" };
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }

        {
            final String query = "select d.ecm:uuid from MaNote AS d WHERE isChildOf(d.ecm:uuid, d.ecm:parentId, select f.ecm:uuid from Folder AS f WHERE f.dc:title LIKE ?) = 1";
            final Object params[] = new Object[] { "MyFolder1" };
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(2, res.size());
            } finally {
                res.close();
            }
        }

        {
            final String query = "select d.ecm:uuid from MaNote AS d WHERE d.dc:title = ?";
            final Object params[] = new Object[] { "docNote1" };
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(3, res.size());
            } finally {
                res.close();
            }
        }

        {
            final String query = "select d.ecm:uuid from MaNote AS d WHERE d.dc:title = ? AND isChildOf(d.ecm:uuid, select f.ecm:uuid from Folder AS f WHERE f.dc:title LIKE ?) = 1";
            final Object params[] = new Object[] { "docNote1", "MyFolder1" };
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(1, res.size());
            } finally {
                res.close();
            }
        }

        {
            final String query = "select d.ecm:uuid from MaNote AS d WHERE isChildOf(d.ecm:uuid, select f.ecm:uuid from Folder AS f WHERE f.dc:title LIKE ?) = 1";
            final Object params[] = new Object[] { "MyFolder1" };
            final IterableQueryResult res = QueryUtils.doQueryAndFetchPaginatedForUFNXQL(session, query, 1, 0, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(1, res.size());
            } finally {
                res.close();
            }
        }

        // test appel de la methode pour le test des acls
        {
            final String query = "select d.ecm:uuid from MaNote AS d WHERE d.dc:title = ? and testAcl(d.ecm:uuid) = 1";
            final Object params[] = new Object[] { "docNote1" };
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(3, res.size());
            } finally {
                res.close();
            }
        }
    }

    @Test
    public void g01testIsVersionInUFNXQL() throws NuxeoException {
        DocumentModel note1 = session.createDocumentModel("/", "note1", "MaNote");
        note1 = session.createDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 v2");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 v3");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 alive");
        note1 = session.saveDocument(note1);

        session.save();

        final List<DocumentModel> versionList = session.getVersions(note1.getRef());

        assertEquals(3, versionList.size());

        {
            final String query = "select n.ecm:uuid from MaNote AS n";
            final Object params[] = null;
            final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
            try {
                final Iterator<Map<String, Serializable>> it = res.iterator();
                while (it.hasNext()) {
                    final Map<String, Serializable> m = it.next();
                    assertEquals(1, m.size());
                    for (final Entry<String, Serializable> e : m.entrySet()) {
                        LOG.info(e.getKey() + "-=->" + e.getValue());
                    }

                }
                assertEquals(4, res.size());
            } finally {
                res.close();
            }
        }

        // Manipulation en NXQL
        {
            String query = "select * from MaNote";

            DocumentModelList dml = session.query(query);
            assertEquals(4, dml.size());

            query = "select * from MaNote WHERE ecm:isCheckedInVersion = 1";

            dml = session.query(query);
            assertEquals(3, dml.size());

            query = "select * from MaNote WHERE ecm:isCheckedInVersion = 0";

            dml = session.query(query);
            assertEquals(1, dml.size());
            DocumentModel n = dml.get(0);
            assertEquals("docNote1 alive", n.getPropertyValue("dc:title"));

            query = "select * from MaNote WHERE ecm:isVersion = 1";

            dml = session.query(query);
            assertEquals(3, dml.size());

            query = "select * from MaNote WHERE ecm:isVersion = 0";

            dml = session.query(query);
            assertEquals(1, dml.size());
            n = dml.get(0);
            assertEquals("docNote1 alive", n.getPropertyValue("dc:title"));
        }

        // Manipulation en UFNXQL
        {
            String query = "select n.ecm:uuid AS id from MaNote AS n";

            List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
            assertEquals(4, ids.size());

            query = "select n.ecm:uuid AS id  from MaNote AS n WHERE n.ecm:isCheckedInVersion = 1";

            ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
            assertEquals(3, ids.size());

            query = "select n.ecm:uuid AS id from MaNote AS n WHERE n.ecm:isCheckedInVersion = 0";

            List<DocumentModel> dml = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, query, null);
            assertEquals(1, dml.size());
            DocumentModel n = dml.get(0);
            assertEquals("docNote1 alive", n.getPropertyValue("dc:title"));

            query = "select n.ecm:uuid AS id  from MaNote AS n WHERE n.ecm:isVersion = 1";

            ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
            assertEquals(3, ids.size());

            query = "select n.ecm:uuid AS id from MaNote AS n WHERE n.ecm:isVersion = 0";

            dml = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, query, null);
            assertEquals(1, dml.size());
            n = dml.get(0);
            assertEquals("docNote1 alive", n.getPropertyValue("dc:title"));
        }

    }

    @Test
    public void h01testMissingAsId() throws NuxeoException {
        DocumentModel note1 = session.createDocumentModel("/", "note1", "MaNote");
        note1 = session.createDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1");
        note1 = session.saveDocument(note1);

        session.save();

        // Test la levee d'une exception si la requete renvoie des resultat mais
        // que le champ
        // id est manquant (car 'AS id' manquant)
        // exception pour aider à la mise au point de la requete
        {

            final String query = "select n.ecm:uuid from MaNote AS n";

            try {
                QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
                fail();
            } catch (final NuxeoException e) {
                LOG.error(e);
            }
        }

    }

    @Test
    public void i01testExist() throws NuxeoException {
        DocumentModel note1 = session.createDocumentModel("MaNote");
        note1.setPropertyValue("dc:title", "docNote1 bis");
        note1 = session.createDocument(note1);

        DocumentModel note2 = session.createDocumentModel("MaNote");
        note2.setPropertyValue("dc:title", "docNote2");
        note2 = session.createDocument(note2);

        DocumentModel file1 = session.createDocumentModel("File");
        file1.setPropertyValue("dc:title", "docNote1 bis");
        file1 = session.createDocument(file1);

        DocumentModel file2 = session.createDocumentModel("File");
        file2.setPropertyValue("dc:title", "toto");
        file2 = session.createDocument(file2);

        session.save();

        // note ou existe pas fichier avec ce nom
        String query = "select n.ecm:uuid AS id, n.dc:title from MaNote as n where exist(select f.ecm:uuid from File AS f WHERE f.dc:title = n.dc:title) = 0";
        List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);

        assertEquals(1, ids.size());
        assertEquals(note2.getId(), ids.get(0));

        // note ou existe fichier avec ce nom
        query = "select n.ecm:uuid AS id, n.dc:title from MaNote as n where exist(select f.ecm:uuid from File AS f WHERE f.dc:title = n.dc:title) = 1";
        ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);

        assertEquals(1, ids.size());
        assertEquals(note1.getId(), ids.get(0));

        // note ou existe un fichier qui n'a pas ce nom
        query = "select n.ecm:uuid AS id, n.dc:title from MaNote as n where exist(select f.ecm:uuid from File AS f WHERE f.dc:title != n.dc:title) = 1";
        ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);

        assertEquals(2, ids.size());

    }

    @Test
    public void j01testCallFunction() throws NuxeoException {
        final String function_call = "NX_IN_TREE('a', 'b')";
        final String query = "CALL " + function_call;

        IterableQueryResult res = null;
        try {
            res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_ID }, query, null);
            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                for (final String key : m.keySet()) {
                    LOG.info(" - " + key + " = " + m.get(key));
                }
            }
        } finally {
            if (res != null) {
                res.close();
            }
        }

        QueryUtils.execSqlFunction(session, function_call, null);
    }

    @Test
    public void k01testTypeCombinationInFrom() throws NuxeoException {
        DocumentModel parent = session.createDocumentModel("/", "monparent", "Folder");
        parent = session.createDocument(parent);
        final String parentId = parent.getId();

        DocumentModel note1 = session.createDocumentModel("/monparent", "note1", "MaNote");
        note1.setPropertyValue("dc:title", "docNote1 bis");
        note1 = session.createDocument(note1);

        DocumentModel note2 = session.createDocumentModel("/monparent", "note2", "MaNote");
        note2.setPropertyValue("dc:title", "docNote2");
        note2 = session.createDocument(note2);

        DocumentModel file1 = session.createDocumentModel("/monparent", "file1", "File");
        file1.setPropertyValue("dc:title", "docNote1 bis");
        file1 = session.createDocument(file1);

        DocumentModel file2 = session.createDocumentModel("/monparent", "file2", "File");
        file2.setPropertyValue("dc:title", "toto");
        file2 = session.createDocument(file2);

        DocumentModel file3 = session.createDocumentModel("/monparent", "file3", "File");
        file3.setPropertyValue("dc:title", "titi");
        file3 = session.createDocument(file3);

        DocumentModel folder1 = session.createDocumentModel("/monparent", "folder1", "Folder");
        folder1 = session.createDocument(folder1);

        DocumentModel docType1 = session.createDocumentModel("/monparent", "DocType1", "DocType1");
        docType1.setPropertyValue("dc:title", "DocType1");
        docType1 = session.createDocument(docType1);

        DocumentModel docType2 = session.createDocumentModel("/monparent", "DocType2", "DocType2");
        docType2.setPropertyValue("dc:title", "DocType2");
        docType2 = session.createDocument(docType2);

        session.save();

        // compte tous les documents de monparent
        String query = QueryUtils
                .ufnxqlToFnxqlQuery("select d.ecm:uuid as id from Document AS d WHERE d.ecm:parentId = ?");
        Long count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(8L), count);

        // compte tous les fichier de monparent
        query = QueryUtils.ufnxqlToFnxqlQuery("select d.ecm:uuid as id from File AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(3L), count);

        // compte toutes les notes de monparent
        query = QueryUtils.ufnxqlToFnxqlQuery("select d.ecm:uuid as id from MaNote AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(2L), count);

        // compte tous les document autres que les fichier
        query = QueryUtils
                .ufnxqlToFnxqlQuery("select d.ecm:uuid as id from Document!File AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(5L), count);

        // compte tous les document autres que les notes
        query = QueryUtils
                .ufnxqlToFnxqlQuery("select d.ecm:uuid as id from Document!MaNote AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(6L), count);

        // compte tous les fichiers et notes
        query = QueryUtils.ufnxqlToFnxqlQuery("select d.ecm:uuid as id from File|MaNote AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(5L), count);

        // compte tous les document folderish
        query = QueryUtils
                .ufnxqlToFnxqlQuery("select d.ecm:uuid as id from facet:Folderish AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(1L), count);

        // compte tous les document folderish ou File
        query = QueryUtils
                .ufnxqlToFnxqlQuery("select d.ecm:uuid as id from facet:Folderish|File AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(4L), count);

        // compte tous les document folderish ou File
        query = QueryUtils.ufnxqlToFnxqlQuery(
                "select d.ecm:uuid as id from facet:Folderish|strict:File AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(4L), count);

        // compte tous les documents DocType1 et DocType2
        query = QueryUtils.ufnxqlToFnxqlQuery("select d.ecm:uuid as id from DocType1 AS d ");
        count = QueryUtils.doCountQuery(session, query, null);
        Assert.assertEquals(new Long(2L), count);

        // compte tous les documents DocType1 seulement (DocType2 héritant dans DocType1)
        query = QueryUtils.ufnxqlToFnxqlQuery("select d.ecm:uuid as id from strict:DocType1 AS d ");
        count = QueryUtils.doCountQuery(session, query, null);
        Assert.assertEquals(new Long(1L), count);

        // compte tous les documents sauf file et DocType1 (et DocType2 par héritage)
        query = QueryUtils.ufnxqlToFnxqlQuery(
                "select d.ecm:uuid as id from Document!File!DocType1 AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(3L), count);

        // compte tous les documents sauf file et strictement DocType1 (donc avec DocType2)
        query = QueryUtils.ufnxqlToFnxqlQuery(
                "select d.ecm:uuid as id from Document!File!strict:DocType1 AS d WHERE d.ecm:parentId = ?");
        count = QueryUtils.doCountQuery(session, query, new Object[] { parentId });
        Assert.assertEquals(new Long(4L), count);

    }

    @Test
    public void l01testVersionableIdInUFNXQL() throws NuxeoException {
        DocumentModel note1 = session.createDocumentModel("/", "note1", "MaNote");
        note1 = session.createDocument(note1);
        final String id = note1.getId();

        note1.setPropertyValue("dc:title", "docNote1");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 v2");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 v3");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 alive");
        note1 = session.saveDocument(note1);

        session.save();

        final List<DocumentModel> versionList = session.getVersions(note1.getRef());

        assertEquals(3, versionList.size());

        final String query = "select n.ecm:uuid as id FROM MaNote AS n WHERE n.ecm:versionableId = ? ";
        final Object params[] = new Object[] { id };
        final List<DocumentModel> docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, query, params);

        assertEquals(3, docs.size());
    }

    @Test
    public void m01testRetrieveDoc() throws NuxeoException {
        DocumentModel note1 = session.createDocumentModel("/", "note1", "MaNote");
        note1 = session.createDocument(note1);
        final String id = note1.getId();

        note1.setPropertyValue("dc:title", "docNote1");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 v2");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 v3");
        note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
        note1 = session.saveDocument(note1);
        note1.setPropertyValue("dc:title", "docNote1 alive");
        note1 = session.saveDocument(note1);

        session.save();

        final List<DocumentModel> versionList = session.getVersions(note1.getRef());

        assertEquals(3, versionList.size());

        final String query = "select n.ecm:uuid as id FROM MaNote AS n WHERE n.ecm:versionableId = ? ";
        final Object params[] = new Object[] { id };
        final List<DocumentModel> docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, "MaNote", query, params);

        assertEquals(3, docs.size());
    }

    @Test
    public void n01testSQLMixWithUFNXQL() throws NuxeoException {
        DocumentModel note1 = session.createDocumentModel("/", "note1", "MaNote");
        note1 = session.createDocument(note1);
        final String noteId = note1.getId();

        DocumentModel file1 = session.createDocumentModel("/", "file1", "File");
        file1 = session.createDocument(file1);
        final String fileId = file1.getId();

        session.save();

        final String query1 = "select n.ecm:uuid as id FROM MaNote AS n WHERE n.ecm:name = ?";
        final Object params1[] = new Object[] { "note1" };
        List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query1, params1);
        Assert.assertEquals(1, ids.size());
        Assert.assertEquals(noteId, ids.get(0));

        final String query2 = "select n.ecm:uuid as id FROM File AS n WHERE n.ecm:name = ?";
        final Object params2[] = new Object[] { "file1" };
        ids = QueryUtils.doUFNXQLQueryForIdsList(session, query2, params2);
        Assert.assertEquals(1, ids.size());
        Assert.assertEquals(fileId, ids.get(0));

        final String mixquery = FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.getKey() + "[" + FlexibleQueryMaker.COL_ID + "]"
                + "[1,1]" + "SELECT id FROM (({ufnxql:" + query1 + "}) UNION ({ufnxql:" + query2 + "})) AS q";
        final Object[] mixparams = new Object[] { "note1", "file1" };
        ids = QueryUtils.doFNXQLQueryAndMapping(session, mixquery, mixparams,
                new ListMapper<String>(new RowMapper<String>() {

                    @Override
                    public String doMapping(final Map<String, Serializable> rowData) throws NuxeoException {
                        return (String) rowData.get("id");
                    }

                }));
        Assert.assertEquals(2, ids.size());
        Assert.assertTrue(ids.contains(noteId));
        Assert.assertTrue(ids.contains(fileId));

    }

    @Test
    public void o01testUnionInUFNXQL() throws NuxeoException {
        DocumentModel note1 = session.createDocumentModel("/", "note1", "MaNote");
        note1 = session.createDocument(note1);
        final String noteId = note1.getId();

        DocumentModel file1 = session.createDocumentModel("/", "file1", "File");
        file1 = session.createDocument(file1);
        final String fileId = file1.getId();

        session.save();

        final String query = "select a.id AS id FROM ((select n.ecm:uuid as id FROM MaNote AS n WHERE n.ecm:name = ?) UNION (select f.ecm:uuid as id FROM File AS f WHERE f.ecm:name = ?)) AS a";
        final Object params[] = new Object[] { "note1", "file1" };
        final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, params);
        Assert.assertEquals(2, ids.size());
        Assert.assertTrue(ids.contains(noteId));
        Assert.assertTrue(ids.contains(fileId));
    }

    @Test
    public void p01testUnionInUFNXQL2() throws NuxeoException {
        final int MAX = 5;
        final String[] expectedIds = new String[2 * MAX];

        int idx = 0;
        for (int i = 0; i < MAX; ++i) {
            final String name = String.format("name-%d", i);

            String title = name + "-1";
            DocumentModel note1 = session.createDocumentModel("/", title, "MaNote");
            DublincorePropertyUtil.setTitle(note1, title);
            note1 = session.createDocument(note1);

            title = name + "-2";
            DocumentModel file1 = session.createDocumentModel("/", title, "File");
            DublincorePropertyUtil.setTitle(file1, title);
            file1 = session.createDocument(file1);

            expectedIds[idx] = note1.getId();
            ++idx;
            expectedIds[idx] = file1.getId();
            ++idx;
        }

        session.save();

        {
            final String query = "select d.ecm:uuid AS id FROM ((select n.ecm:uuid as id FROM MaNote AS n WHERE n.ecm:name LIKE ?) UNION (select f.ecm:uuid as id FROM File AS f WHERE f.ecm:name LIKE ?)) AS a, Document AS d WHERE a.id = d.ecm:uuid order by d.ecm:name DESC";
            final Object params[] = new Object[] { "name%", "name%" };
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, params);
            Assert.assertEquals(expectedIds.length, ids.size());
            idx = expectedIds.length - 1;
            for (final String id : ids) {
                Assert.assertEquals(expectedIds[idx], id);
                --idx;
            }
        }
        {
            final String query = "select a.id AS id FROM ((select n.ecm:uuid as id FROM MaNote AS n WHERE n.ecm:name LIKE ?) UNION (select f.ecm:uuid as id FROM File AS f WHERE f.ecm:name LIKE ?)) AS a, Document AS d WHERE a.id = d.ecm:uuid order by d.ecm:name DESC";
            final Object params[] = new Object[] { "name%", "name%" };
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, params);
            Assert.assertEquals(expectedIds.length, ids.size());
            idx = expectedIds.length - 1;
            for (final String id : ids) {
                Assert.assertEquals(expectedIds[idx], id);
                --idx;
            }
        }
        {
            final String query = "select a.id AS id FROM ((select n.ecm:uuid as id, n.ecm:name AS name FROM MaNote AS n) UNION (select n.ecm:uuid as id, n.ecm:name AS name FROM File AS n)) AS a WHERE a.name LIKE ? order by a.name DESC";
            final Object params[] = new Object[] { "name%" };
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, params);
            Assert.assertEquals(expectedIds.length, ids.size());
            idx = expectedIds.length - 1;
            for (final String id : ids) {
                Assert.assertEquals(expectedIds[idx], id);
                --idx;
            }
        }
        {
            final String query = "select a.id AS id FROM ((select n.ecm:uuid as id, n.ecm:name AS name FROM MaNote AS n) UNION (select n.ecm:uuid as id, n.ecm:name AS name FROM File AS n GROUP BY n.ecm:uuid)) AS a WHERE a.name LIKE ? order by a.name DESC";
            final Object params[] = new Object[] { "name%" };
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, params);
            Assert.assertEquals(expectedIds.length, ids.size());
            idx = expectedIds.length - 1;
            for (final String id : ids) {
                Assert.assertEquals(expectedIds[idx], id);
                --idx;
            }
        }
    }

    @Test
    public void a01testCoalesce() throws NuxeoException {
        DocumentModel parent = session.createDocumentModel("/", "folder", "Folder");
        parent = session.createDocument(parent);
        final String parentId = parent.getId();
        DocumentModel note1 = session.createDocumentModel(parent.getPathAsString(), "note", "MaNote");
        note1 = session.createDocument(note1);
        final String note1Id = note1.getId();

        session.save();

        {
            final String query = "select coalesce(d.ecm:uuid,d.ecm:uuid) AS id FROM MaNote AS d WHERE d.ecm:parentId=?";
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, new Object[] { parentId });
            Assert.assertEquals(1, ids.size());
            Assert.assertEquals(note1Id, ids.get(0));

        }

        {
            final String rootId = session.getRootDocument().getId();
            final String query = "select d.ecm:uuid AS id FROM Root AS d";
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
            Assert.assertEquals(1, ids.size());
            Assert.assertEquals(rootId, ids.get(0));

        }

        // {
        // FAILED : no result for key Id
        // String rootId = session.getRootDocument().getId();
        // final String query = "select d.ecm:parentId AS id FROM Root AS d";
        // List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
        // Assert.assertEquals(1, ids.size());
        // Assert.assertEquals(rootId, ids.get(0));
        // }

        {
            final String rootId = session.getRootDocument().getId();
            final String query = "select coalesce(d.ecm:parentId,d.ecm:uuid) AS id FROM Root AS d";
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
            Assert.assertEquals(1, ids.size());
            Assert.assertEquals(rootId, ids.get(0));

        }
    }

    @Test
    public void r01testCountDistinct() throws NuxeoException {
        final String name = "noteTestCountDistinct";
        DocumentModel parent1 = session.createDocumentModel("/", "folderTestCountDistinct1", "Folder");
        parent1 = session.createDocument(parent1);
        DocumentModel parent2 = session.createDocumentModel("/", "folderTestCountDistinct2", "Folder");
        parent2 = session.createDocument(parent2);

        for (int i = 0; i < 2; ++i) {
            DocumentModel note1 = session.createDocumentModel(parent1.getPathAsString(), name + "1-" + i, "MaNote");
            note1 = session.createDocument(note1);

            note1 = session.createDocumentModel(parent2.getPathAsString(), name + "2-" + i, "MaNote");
            note1 = session.createDocument(note1);
        }

        session.save();

        // {
        // Long expected = 4L;
        // final String query = FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + "select count() AS count FROM Note AS d WHERE d.ecm:name LIKE ?";
        // Long count = QueryUtils.doFNXQLQueryAndMapping(session, query, new Object[]{name+"%"}, new CountMapper());
        // Assert.assertEquals(expected, count);
        // }
        //
        // {
        // final String query = "select distinct d.ecm:parentId AS id FROM Note AS d WHERE d.ecm:name LIKE ?";
        // List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, new Object[]{name+"%"});
        // Assert.assertEquals(2, ids.size());
        // Assert.assertTrue(ids.contains(parent1.getId()));
        // Assert.assertTrue(ids.contains(parent2.getId()));
        // }

        {
            final Long expected = 2L;
            final String query = FlexibleQueryMaker.KeyCode.UFXNQL.getKey()
                    + "select countDistinct(d.ecm:parentId) AS count FROM MaNote AS d WHERE d.ecm:name LIKE ?";
            final Long count = QueryUtils.doFNXQLQueryAndMapping(session, query, new Object[] { name + "%" },
                    new CountMapper());
            Assert.assertEquals(expected, count);
        }
    }

    @Test
    public void s01testGroupByOnFunction() throws NuxeoException {
        final String titre1 = "Doc1";
        final String titre2 = "doc1";
        final String descr = "descrtest";

        DocumentModel doc1 = session.createDocumentModel("/", "doc1", DOC_TYPE);
        doc1.setPropertyValue("dc:title", titre1);
        doc1.setPropertyValue("dc:description", descr);
        doc1 = session.createDocument(doc1);

        DocumentModel doc2 = session.createDocumentModel("/", "doc2", DOC_TYPE);
        doc2.setPropertyValue("dc:title", titre1);
        doc2.setPropertyValue("dc:description", descr);
        doc2 = session.createDocument(doc2);

        DocumentModel doc3 = session.createDocumentModel("/", "doc3", DOC_TYPE);
        doc3.setPropertyValue("dc:title", titre2);
        doc3.setPropertyValue("dc:description", descr);
        doc3 = session.createDocument(doc3);
        session.save();

        Assert.assertEquals(Long.valueOf(3), QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(
                "select d.ecm:uuid As id from " + DOC_TYPE + " AS d where d.dc:description = '" + descr + "'")));

        {
            final String selectQuery = QueryUtils.ufnxqlToFnxqlQuery("select d.dc:title, count() as count from "
                    + DOC_TYPE + " AS d where d.dc:description = '" + descr + "' group by d.dc:title");
            final IterableQueryResult res = session.queryAndFetch(selectQuery, FlexibleQueryMaker.QUERY_TYPE);

            long nbtitle = 0;
            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                assertEquals(2, m.keySet().size());
                // for (String s : m.keySet()) {
                // log.info("-*-> " + s + " = " + m.get(s));
                // }
                final String title = (String) m.get("d.dc:title");
                final Long nb = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
                if (titre1.equals(title)) {
                    Assert.assertEquals(Long.valueOf(2), nb);
                } else {
                    Assert.assertEquals(Long.valueOf(1), nb);
                }
                ++nbtitle;
            }
            res.close();
            Assert.assertEquals(2L, nbtitle);
        }
        {
            final String selectQuery = QueryUtils.ufnxqlToFnxqlQuery("select lower(d.dc:title), count() as count from "
                    + DOC_TYPE + " AS d where d.dc:description = '" + descr + "' group by lower(d.dc:title)");
            final IterableQueryResult res = session.queryAndFetch(selectQuery, FlexibleQueryMaker.QUERY_TYPE);

            long nbtitle = 0;
            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                assertEquals(2, m.keySet().size());
                final String title = (String) m.get("lower(d.dc:title)");
                final Long nb = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
                if (titre2.equals(title)) {
                    Assert.assertEquals(Long.valueOf(3), nb);
                } else {
                    Assert.fail("unexpected title : " + title);
                }
                ++nbtitle;
            }
            res.close();
            assertEquals(1, nbtitle);
        }
    }

    @Test
    public void s02testSelectFunc() throws NuxeoException {
        final String titre1 = "Doc1";
        final String titre2 = "doc1";
        final String descr = "descrtestSelFunc";

        DocumentModel doc1 = session.createDocumentModel("/", "doc1", DOC_TYPE);
        doc1.setPropertyValue("dc:title", titre1);
        doc1.setPropertyValue("dc:description", descr);
        doc1.setPropertyValue("dc:created", Calendar.getInstance());
        doc1 = session.createDocument(doc1);

        DocumentModel doc2 = session.createDocumentModel("/", "doc2", DOC_TYPE);
        doc2.setPropertyValue("dc:title", titre2);
        doc2.setPropertyValue("dc:description", descr);
        doc2 = session.createDocument(doc2);

        session.save();

        {
            final String selectQuery = QueryUtils
                    .ufnxqlToFnxqlQuery("select d.dc:title as titre, dateadd('YEAR', 1, d.dc:created) as year from "
                            + DOC_TYPE + " AS d where d.dc:description = '" + descr + "'");
            final IterableQueryResult res = session.queryAndFetch(selectQuery, FlexibleQueryMaker.QUERY_TYPE);

            final int curYear = Calendar.getInstance().get(Calendar.YEAR);
            final Iterator<Map<String, Serializable>> it = res.iterator();
            while (it.hasNext()) {
                final Map<String, Serializable> m = it.next();
                assertEquals(2, m.keySet().size());
                final String title = (String) m.get("titre");
                final Calendar dateadd = (Calendar) m.get("year");
                if (titre1.equals(title)) {
                    Assert.assertEquals(curYear + 1, dateadd.get(Calendar.YEAR));
                } else if (titre2.equals(title)) {
                    Assert.assertNull(dateadd);
                } else {
                    Assert.fail("unexpected title : " + title);
                }
            }
            res.close();
        }
    }

}
