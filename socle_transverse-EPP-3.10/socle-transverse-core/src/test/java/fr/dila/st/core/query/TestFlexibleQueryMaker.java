package fr.dila.st.core.query;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.core.versioning.VersioningService;

import fr.dila.st.core.schema.DublincoreSchemaUtils;

public class TestFlexibleQueryMaker extends SQLRepositoryTestCase {

	private static final Log	log	= LogFactory.getLog(TestFlexibleQueryMaker.class);

	@Override
	public void setUp() throws Exception {

		super.setUp();

		deployContrib("fr.dila.st.core", "OSGI-INF/querymaker-contrib.xml");
	}

	public static final String	DOC_TYPE	= "File";

	public void testCountDocument() throws PropertyException, ClientException {

		openSession();

		long nbDocInserted = 0;
		DocumentModel doc1 = new DocumentModelImpl(DOC_TYPE);
		doc1 = session.createDocument(doc1);
		doc1 = session.saveDocument(doc1);
		nbDocInserted++;

		DocumentModel doc2 = new DocumentModelImpl(DOC_TYPE);
		doc2 = session.createDocument(doc2);
		doc2 = session.saveDocument(doc2);
		nbDocInserted++;
		session.save();

		final String selectQuery = "select * from " + DOC_TYPE;
		final DocumentModelList dml = session.query(selectQuery);
		assertEquals(nbDocInserted, dml.totalSize());

		Long nbDocCounted = QueryUtils.doCountQuery(session, selectQuery);
		assertNotNull(nbDocCounted);
		assertEquals(nbDocInserted, nbDocCounted.longValue());

		nbDocCounted = QueryUtils.doUnrestrictedCountQuery(session, selectQuery, new Object[] {});
		assertNotNull(nbDocCounted);
		assertEquals(nbDocInserted, nbDocCounted.longValue());

		closeSession();
	}

	public void testDocument() throws PropertyException, ClientException {

		openSession();

		long nbDocInserted = 0;
		long nbDistinctTitle = 0;
		DocumentModel doc1 = new DocumentModelImpl(DOC_TYPE);
		doc1 = session.createDocument(doc1);
		DublincoreSchemaUtils.setTitle(doc1, "doc1");
		doc1 = session.saveDocument(doc1);
		nbDocInserted++;
		nbDistinctTitle++;

		DocumentModel doc2 = new DocumentModelImpl(DOC_TYPE);
		doc2 = session.createDocument(doc2);
		DublincoreSchemaUtils.setTitle(doc2, "doc2");
		doc2 = session.saveDocument(doc2);
		nbDocInserted++;
		nbDistinctTitle++;

		DocumentModel doc3 = new DocumentModelImpl(DOC_TYPE);
		doc3 = session.createDocument(doc3);
		DublincoreSchemaUtils.setTitle(doc3, "doc2"); // same title as doc2
		doc3 = session.saveDocument(doc3);
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
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.SQL_CODE.key + params
					+ selectQuery, FlexibleQueryMaker.QUERY_TYPE);

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
			final IterableQueryResult res = QueryUtils.doSqlQuery(session, new String[] { "dc:title",
					FlexibleQueryMaker.COL_COUNT }, selectQuery, null);

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
					log.info("--> " + s + " = " + m.get(s));
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
					log.info("--> " + s + " = " + m.get(s));
				}
			}
			res.close();
		}

		{
			final String selectQuery = "select distinct dc:title from " + DOC_TYPE;
			final Long nbtitle = QueryUtils.doCountQuery(session, selectQuery);
			assertEquals(nbDistinctTitle, nbtitle.longValue());
		}

		long nbNoteInserted = 0;
		DocumentModel doc4 = new DocumentModelImpl("Note");
		doc4 = session.createDocument(doc4);
		DublincoreSchemaUtils.setTitle(doc4, "doc4"); // same title as doc2
		doc4 = session.saveDocument(doc4);
		nbNoteInserted++;
		nbDistinctTitle++;
		session.save();

		{
			final String selectQuery = "select count(*) from (({select * from Note}))";
			// String selectQuery =
			// "select count(*) from (select id from hierarchy) union (select id from dublincore))";
			final String params = "[" + FlexibleQueryMaker.COL_COUNT + "]";

			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.key + params
					+ selectQuery, FlexibleQueryMaker.QUERY_TYPE);

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

			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.key + params
					+ selectQuery, FlexibleQueryMaker.QUERY_TYPE);

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
			final String selectQuery = "select count(*) from (({select * from Note}) union ({select * from File}))";
			final String params = "[" + FlexibleQueryMaker.COL_COUNT + "]";

			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.key + params
					+ selectQuery, FlexibleQueryMaker.QUERY_TYPE);

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
			final String selectQuery = "select count(*) from (({select * from Note}) union ({select * from File}) union (select id from hierarchy))";
			final String params = "[" + FlexibleQueryMaker.COL_COUNT + "]";

			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.SQL_MIX_CODE.key + params
					+ selectQuery, FlexibleQueryMaker.QUERY_TYPE);

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

		closeSession();
	}

	public void testPagination() throws PropertyException, ClientException {

		openSession();

		long nbDocInserted = 0;
		// long nbDistinctTitle = 0;
		DocumentModel doc1 = new DocumentModelImpl(DOC_TYPE);
		doc1 = session.createDocument(doc1);
		DublincoreSchemaUtils.setTitle(doc1, "doc1");
		doc1 = session.saveDocument(doc1);
		nbDocInserted++;
		// nbDistinctTitle++;

		DocumentModel doc2 = new DocumentModelImpl(DOC_TYPE);
		doc2 = session.createDocument(doc2);
		DublincoreSchemaUtils.setTitle(doc2, "doc2");
		doc2 = session.saveDocument(doc2);
		nbDocInserted++;
		// nbDistinctTitle++;

		DocumentModel doc3 = new DocumentModelImpl(DOC_TYPE);
		doc3 = session.createDocument(doc3);
		DublincoreSchemaUtils.setTitle(doc3, "doc2"); // same title as doc2
		doc3 = session.saveDocument(doc3);
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
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.PAGE.key + "[" + limit
					+ "]" + "[" + offset + "]" + selectQuery, FlexibleQueryMaker.QUERY_TYPE);
			assertEquals(limit, res.size());
			res.close();
		}

		{
			final long limit = nbDocInserted;
			final long offset = 0;
			assertTrue(limit > 0);
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.PAGE.key + "[" + limit
					+ "]" + "[" + offset + "]" + selectQuery, FlexibleQueryMaker.QUERY_TYPE);
			assertEquals(limit, res.size());
			res.close();
		}

		{
			final long limit = nbDocInserted + 100;
			final long offset = 0;
			assertTrue(limit > 0);
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.PAGE.key + "[" + limit
					+ "]" + "[" + offset + "]" + selectQuery, FlexibleQueryMaker.QUERY_TYPE);
			assertEquals(nbDocInserted, res.size());
			res.close();
		}

		{
			final long limit = nbDocInserted + 100;
			final long offset = nbDocInserted - 2;
			assertTrue(limit > 0);
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.PAGE.key + "[" + limit
					+ "]" + "[" + offset + "]" + selectQuery, FlexibleQueryMaker.QUERY_TYPE);
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

		closeSession();
	}

	public void testParam() throws PropertyException, ClientException {
		openSession();

		final String a_title = "doc'2";
		// long nbDocInserted = 0;
		// long nbDistinctTitle = 0;
		DocumentModel doc1 = new DocumentModelImpl(DOC_TYPE);
		doc1 = session.createDocument(doc1);
		DublincoreSchemaUtils.setTitle(doc1, "doc1");
		doc1 = session.saveDocument(doc1);
		// nbDocInserted++;
		// nbDistinctTitle++;

		DocumentModel doc2 = new DocumentModelImpl(DOC_TYPE);
		doc2 = session.createDocument(doc2);
		DublincoreSchemaUtils.setTitle(doc2, a_title);
		doc2 = session.saveDocument(doc2);
		// nbDocInserted++;
		// nbDistinctTitle++;

		DocumentModel doc3 = new DocumentModelImpl(DOC_TYPE);
		doc3 = session.createDocument(doc3);
		DublincoreSchemaUtils.setTitle(doc3, a_title); // same title as doc2
		doc3 = session.saveDocument(doc3);
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
				log.info("-*-> " + s + " = " + m.get(s));
			}
			final Long nb = (Long) m.get(FlexibleQueryMaker.COL_COUNT);
			assertEquals(2, nb.longValue());
		}
		res.close();

		closeSession();
	}

	public void testUFNXQL() throws ClientException {

		openSession();

		final String titreUnique = "doc1";
		long nbDocInserted = 0;
		long nbDistinctTitle = 0;
		DocumentModel doc1 = new DocumentModelImpl(DOC_TYPE);
		doc1 = session.createDocument(doc1);
		DublincoreSchemaUtils.setTitle(doc1, titreUnique);
		doc1 = session.saveDocument(doc1);
		nbDocInserted++;
		nbDistinctTitle++;

		DocumentModel doc2 = new DocumentModelImpl(DOC_TYPE);
		doc2 = session.createDocument(doc2);
		DublincoreSchemaUtils.setTitle(doc2, "doc2");
		doc2 = session.saveDocument(doc2);
		nbDocInserted++;
		nbDistinctTitle++;

		DocumentModel doc3 = new DocumentModelImpl(DOC_TYPE);
		doc3 = session.createDocument(doc3);
		DublincoreSchemaUtils.setTitle(doc3, "doc2"); // same title as doc2
		doc3 = session.saveDocument(doc3);
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
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
					FlexibleQueryMaker.QUERY_TYPE, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
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
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
					FlexibleQueryMaker.QUERY_TYPE, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
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
						log.info(e.getKey() + "-=->" + e.getValue());
					}
				}
				assertEquals(nbDistinctTitle, res.size());
			} finally {
				res.close();
			}
		}

		{
			// retourne les doc File dont le titre match le parametre est dont l'état est different de deleted
			final String query = "select f.ecm:uuid as id from File AS f where f.dc:title = ? and f.ecm:currentLifeCycleState != 'deleted'";
			final Object params[] = new Object[] { titreUnique };
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
					FlexibleQueryMaker.QUERY_TYPE, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}
					assertEquals(doc1.getId(), m.get("id"));
				}
				assertEquals(1, res.size());
			} finally {
				res.close();
			}
		}

		{
			// retourne les doc File dont le titre match le parametre est dont l'état est deleted
			final String query = "select f.ecm:uuid as id from File AS f where f.dc:title = ? and f.ecm:currentLifeCycleState = 'deleted'";
			final Object params[] = new Object[] { titreUnique };
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
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
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
					FlexibleQueryMaker.QUERY_TYPE, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(2, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

					assertTrue(m.get("id1").equals(doc2.getId()) || m.get("id1").equals(doc3.getId()));
					assertTrue(m.get("id2").equals(doc2.getId()) || m.get("id2").equals(doc3.getId()));
				}
				assertEquals(2, res.size());
			} finally {
				res.close();
			}
		}

		DocumentModel note1 = new DocumentModelImpl("Note");
		note1 = session.createDocument(note1);
		DublincoreSchemaUtils.setTitle(note1, "docNote1");
		note1 = session.saveDocument(note1);
		DocumentModel note2 = new DocumentModelImpl("Note");
		note2 = session.createDocument(note2);
		DublincoreSchemaUtils.setTitle(note2, "doc1");
		note2 = session.saveDocument(note2);
		DocumentModel note3 = new DocumentModelImpl("Note");
		note3 = session.createDocument(note3);
		DublincoreSchemaUtils.setTitle(note3, "docNote3");
		note3 = session.saveDocument(note3);
		final int nbNote = 3;
		session.save();

		{
			// retourne le nombre de Note
			final String query = "select count() as c from Note AS n";
			final Object params[] = null;// new Object[]{titreUnique};
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
					FlexibleQueryMaker.QUERY_TYPE, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}
					assertEquals(new Long(nbNote), m.get("c"));
				}
				assertEquals(1, res.size());
			} finally {
				res.close();
			}
		}

		{
			// retrourne les doc File dont le titre est commun avec celui d'un doc Note
			final String query = "select f.ecm:uuid as id from File AS f, Note AS n WHERE n.dc:title = f.dc:title";
			final Object params[] = null;
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
					FlexibleQueryMaker.QUERY_TYPE, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}
					assertEquals(doc1.getId(), m.get("id"));
				}
				assertEquals(1, res.size());
			} finally {
				res.close();
			}
		}

		{
			// retrourne les doc File dont le titre est commun avec celui d'un doc Note
			final String query = "select f.ecm:uuid as id from File AS f, Note AS n WHERE n.dc:title = f.dc:title";
			final Object params[] = null;
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
					FlexibleQueryMaker.QUERY_TYPE, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}
					assertEquals(doc1.getId(), m.get("id"));
				}
				assertEquals(1, res.size());
			} finally {
				res.close();
			}
		}

		{
			// retrourne les doc File dont le titre est commun avec celui d'un doc Note
			final String query = "select n.dc:title from Note AS n WHERE n.ecm:uuid IN (?, ?)";// '"+note1.getId()+",
																								// "+note3.getId()+"')";
			final Object params[] = new Object[] { note1.getId(), note3.getId() };
			final IterableQueryResult res = session.queryAndFetch(FlexibleQueryMaker.KeyCode.UFXNQL.key + query,
					FlexibleQueryMaker.QUERY_TYPE, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}
					final String title = (String) m.get("n.dc:title");
					assertNotNull(title);
					// should get note1 and note3 : theri title begin with docNote
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
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(2, res.size());
			} finally {
				res.close();
			}
		}

		{
			final String query = "select d.ecm:uuid from Document AS d where d.ecm:fulltext = 'doc1'";
			final Object params[] = null;
			final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
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
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(4, res.size());
			} finally {
				res.close();
			}
		}

		closeSession();
	}

	/**
	 * test de requete avec isChildOf
	 * 
	 * @throws ClientException
	 */
	public void testRecQueryOnPath() throws ClientException {
		openSession();

		DocumentModel folder1 = session.createDocumentModel("Folder");
		folder1.setPathInfo("/", "nom_workspace_folder1");
		DublincoreSchemaUtils.setTitle(folder1, "MyFolder1");
		folder1 = session.createDocument(folder1);

		DocumentModel folder2 = session.createDocumentModel("Folder");
		folder2.setPathInfo("/", "nom_workspace_folder2");
		DublincoreSchemaUtils.setTitle(folder2, "MyFolder2");
		folder2 = session.createDocument(folder2);

		log.debug(folder1.getPathAsString());
		log.debug(folder2.getPathAsString());

		DocumentModel note1 = session.createDocumentModel("Note");
		DublincoreSchemaUtils.setTitle(note1, "docNote1");
		note1 = session.createDocument(note1);

		DocumentModel note11 = session.createDocumentModel(folder1.getPathAsString(), "note11", "Note");
		DublincoreSchemaUtils.setTitle(note11, "docNote1");
		note11 = session.createDocument(note11);

		DocumentModel note12 = session.createDocumentModel(folder1.getPathAsString(), "note12", "Note");
		DublincoreSchemaUtils.setTitle(note12, "docNote2");
		note12 = session.createDocument(note12);

		DocumentModel note21 = session.createDocumentModel(folder2.getPathAsString(), "note21", "Note");

		DublincoreSchemaUtils.setTitle(note21, "docNote1");
		note21 = session.createDocument(note21);

		DocumentModel note22 = session.createDocumentModel(folder2.getPathAsString(), "note22", "Note");

		DublincoreSchemaUtils.setTitle(note22, "docNote2");
		note22 = session.createDocument(note22);

		folder1 = session.saveDocument(folder1);
		folder2 = session.saveDocument(folder2);

		session.save();

		log.debug(session.getParentDocument(note11.getRef()));

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
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(2, res.size());
			} finally {
				res.close();
			}
		}

		{
			final String query = "select d.ecm:uuid from Note AS d";
			final Object params[] = null;
			final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(5, res.size());
			} finally {
				res.close();
			}
		}

		{
			final String query = "select d.ecm:uuid from Note AS d WHERE isChildOf(d.ecm:uuid, select f.ecm:uuid from Folder AS f WHERE f.dc:title LIKE ?) = 1";
			final Object params[] = new Object[] { "MyFolder1" };
			final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(2, res.size());
			} finally {
				res.close();
			}
		}

		{
			final String query = "select d.ecm:uuid from Note AS d WHERE isChildOf(d.ecm:uuid, d.ecm:parentId, select f.ecm:uuid from Folder AS f WHERE f.dc:title LIKE ?) = 1";
			final Object params[] = new Object[] { "MyFolder1" };
			final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(2, res.size());
			} finally {
				res.close();
			}
		}

		{
			final String query = "select d.ecm:uuid from Note AS d WHERE d.dc:title = ?";
			final Object params[] = new Object[] { "docNote1" };
			final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(3, res.size());
			} finally {
				res.close();
			}
		}

		{
			final String query = "select d.ecm:uuid from Note AS d WHERE d.dc:title = ? AND isChildOf(d.ecm:uuid, select f.ecm:uuid from Folder AS f WHERE f.dc:title LIKE ?) = 1";
			final Object params[] = new Object[] { "docNote1", "MyFolder1" };
			final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(1, res.size());
			} finally {
				res.close();
			}
		}

		{
			final String query = "select d.ecm:uuid from Note AS d WHERE isChildOf(d.ecm:uuid, select f.ecm:uuid from Folder AS f WHERE f.dc:title LIKE ?) = 1";
			final Object params[] = new Object[] { "MyFolder1" };
			final IterableQueryResult res = QueryUtils.doQueryAndFetchPaginatedForUFNXQL(session, query, 1, 0, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(1, res.size());
			} finally {
				res.close();
			}
		}

		// test appel de la methode pour le test des acls
		{
			final String query = "select d.ecm:uuid from Note AS d WHERE d.dc:title = ? and testAcl(d.ecm:uuid) = 1";
			final Object params[] = new Object[] { "docNote1" };
			final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(3, res.size());
			} finally {
				res.close();
			}
		}

		closeSession();
	}

	public void testIsVersionInUFNXQL() throws ClientException {

		openSession();

		DocumentModel note1 = new DocumentModelImpl("Note");
		note1 = session.createDocument(note1);
		DublincoreSchemaUtils.setTitle(note1, "docNote1");
		note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
		note1 = session.saveDocument(note1);
		DublincoreSchemaUtils.setTitle(note1, "docNote1 v2");
		note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
		note1 = session.saveDocument(note1);
		DublincoreSchemaUtils.setTitle(note1, "docNote1 v3");
		note1.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MAJOR);
		note1 = session.saveDocument(note1);
		DublincoreSchemaUtils.setTitle(note1, "docNote1 alive");
		note1 = session.saveDocument(note1);

		session.save();

		final List<DocumentModel> versionList = session.getVersions(note1.getRef());

		assertEquals(3, versionList.size());

		{
			final String query = "select n.ecm:uuid from Note AS n";
			final Object params[] = null;
			final IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, query, params);
			try {
				final Iterator<Map<String, Serializable>> it = res.iterator();
				while (it.hasNext()) {
					final Map<String, Serializable> m = it.next();
					assertEquals(1, m.size());
					for (final Entry<String, Serializable> e : m.entrySet()) {
						log.info(e.getKey() + "-=->" + e.getValue());
					}

				}
				assertEquals(4, res.size());
			} finally {
				res.close();
			}
		}

		// Manipulation en NXQL
		{
			String query = "select * from Note";

			DocumentModelList dml = session.query(query);
			assertEquals(4, dml.size());

			query = "select * from Note WHERE ecm:isCheckedInVersion = 1";

			dml = session.query(query);
			assertEquals(3, dml.size());

			query = "select * from Note WHERE ecm:isCheckedInVersion = 0";

			dml = session.query(query);
			assertEquals(1, dml.size());
			final DocumentModel n = dml.get(0);
			assertEquals("docNote1 alive", DublincoreSchemaUtils.getTitle(n));
		}

		// Manipulation en UFNXQL
		{
			String query = "select n.ecm:uuid AS id from Note AS n";

			List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
			assertEquals(4, ids.size());

			query = "select n.ecm:uuid AS id  from Note AS n WHERE n.ecm:isCheckedInVersion = 1";

			ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
			assertEquals(3, ids.size());

			query = "select n.ecm:uuid AS id from Note AS n WHERE n.ecm:isCheckedInVersion = 0";

			final List<DocumentModel> dml = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, "Note", query, null);
			assertEquals(1, dml.size());
			final DocumentModel n = dml.get(0);
			assertEquals("docNote1 alive", DublincoreSchemaUtils.getTitle(n));
		}

		closeSession();

	}

	public void testMissingAsId() throws ClientException {

		openSession();

		DocumentModel note1 = new DocumentModelImpl("Note");
		note1 = session.createDocument(note1);
		DublincoreSchemaUtils.setTitle(note1, "docNote1");
		note1 = session.saveDocument(note1);

		session.save();

		// Test la levee d'une exception si la requete renvoie des resultat mais que le champ
		// id est manquant (car 'AS id' manquant)
		// exception pour aider à la mise au point de la requete
		{

			final String query = "select n.ecm:uuid from Note AS n";

			try {
				QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
				fail();
			} catch (final ClientException e) {
				// expect an exception
				log.error(e);
			}
		}

		closeSession();

	}

	public void testExist() throws ClientException {

		openSession();

		DocumentModel note1 = session.createDocumentModel("Note");
		DublincoreSchemaUtils.setTitle(note1, "docNote1");
		note1 = session.createDocument(note1);

		DocumentModel note2 = session.createDocumentModel("Note");
		DublincoreSchemaUtils.setTitle(note2, "docNote2");
		note2 = session.createDocument(note2);

		DocumentModel file1 = session.createDocumentModel("File");
		DublincoreSchemaUtils.setTitle(file1, "docNote1");
		file1 = session.createDocument(file1);

		DocumentModel file2 = session.createDocumentModel("File");
		DublincoreSchemaUtils.setTitle(file2, "toto");
		file2 = session.createDocument(file2);

		session.save();

		// note ou existe pas fichier avec ce nom
		String query = "select n.ecm:uuid AS id, n.dc:title from Note as n where exist(select f.ecm:uuid from File AS f WHERE f.dc:title = n.dc:title) = 0";
		List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);

		assertEquals(1, ids.size());
		assertEquals(note2.getId(), ids.get(0));

		// note ou existe fichier avec ce nom
		query = "select n.ecm:uuid AS id, n.dc:title from Note as n where exist(select f.ecm:uuid from File AS f WHERE f.dc:title = n.dc:title) = 1";
		ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);

		assertEquals(1, ids.size());
		assertEquals(note1.getId(), ids.get(0));

		// note ou existe un fichier qui n'a pas ce nom
		query = "select n.ecm:uuid AS id, n.dc:title from Note as n where exist(select f.ecm:uuid from File AS f WHERE f.dc:title != n.dc:title) = 1";
		ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);

		assertEquals(2, ids.size());

		closeSession();

	}

	public void testCallFunction() throws ClientException {
		openSession();

		final String function_call = "NX_IN_TREE('a', 'b')";
		final String query = "CALL " + function_call;

		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_ID }, query, null);
			final Iterator<Map<String, Serializable>> it = res.iterator();
			while (it.hasNext()) {
				final Map<String, Serializable> m = it.next();
				for (final String key : m.keySet()) {
					log.info(" - " + key + " = " + m.get(key));
				}
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}

		QueryUtils.execSqlFunction(session, function_call, null);

		closeSession();
	}

}
