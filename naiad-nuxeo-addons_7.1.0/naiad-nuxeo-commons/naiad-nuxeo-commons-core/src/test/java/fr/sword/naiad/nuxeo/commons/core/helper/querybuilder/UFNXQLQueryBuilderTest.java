package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nuxeo.ecm.core.query.sql.NXQL;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.constant.NuxeoDocumentTypeConstant;

public class UFNXQLQueryBuilderTest {
	@Test
	public void testWhere() {
		String expectedWhere = "d.f1 = 'OK' AND (d.f2 < 7 OR d.f3 > 10)";

		WhereCriterion where = new WhereCriterion(QueryOperatorEnum.AND, "d.f1 = 'OK'", new WhereCriterion(
				QueryOperatorEnum.OR, "d.f2 < 7", "d.f3 > 10"));

		Assert.assertEquals(expectedWhere, where.where());

		where = new WhereCriterion("d.f1 = 'OK'", new WhereCriterion(QueryOperatorEnum.OR, "d.f2 < 7", "d.f3 > 10"));

		Assert.assertEquals(expectedWhere, where.where());

		where = new WhereCriterion("d.f1 = 'OK'", new WhereCriterion("d.f2 < 7", "d.f3 > 10"));

		Assert.assertFalse(expectedWhere.equals(where.where()));
	}

	@Test
	public void testWhereConstruct() {
		WhereCriterion where = new WhereCriterion();
		Assert.assertNull(where.where());

		where.addCriterion("a = a");
		Assert.assertEquals("a = a", where.where());

		where.addCriterion(new WhereCriterion(QueryOperatorEnum.OR));
		Assert.assertEquals("a = a", where.where());

		where.addCriterion(new WhereCriterion(QueryOperatorEnum.OR, "b = b", "c = c"));
		Assert.assertEquals("a = a AND (b = b OR c = c)", where.where());
	}

	@Test
	public void testParams() {
		String expectedWhere = "a = ? OR (b1 = b2 AND (c IN (?, ?, ?)))";
		List<Object> expectedParams = Arrays.asList((Object) "OK", 8, 9, 10);

		WhereCriterion where = new WhereCriterion(QueryOperatorEnum.OR, "a = ?");
		where.bind(new String("OK"));

		WhereCriterion where2 = new WhereCriterion(QueryOperatorEnum.AND, "b1 = b2");

		WhereCriterion where3 = new WhereCriterion(QueryOperatorEnum.OR, "c IN (?, ?, ?)");
		where3.bindAll(Arrays.asList((Object) 8, 9, 10));

		where2.addCriterion(where3);
		where.addCriterion(where2);

		Assert.assertEquals(expectedWhere, where.where());

		List<Object> params = where.getParams();
		Assert.assertEquals(expectedParams.size(), params.size());
		for (int i = 0; i < expectedParams.size(); ++i) {
			Assert.assertEquals(expectedParams.get(i), params.get(i));
		}
	}

	@Test
	public void testQueryParams() {
		UFNXQLQueryBuilder queryBuilder = new UFNXQLQueryBuilder();
		List<Object> expectedParams = Arrays.asList((Object) "OK", 8, 10, 11, 12);

		queryBuilder.bindSelect("OK");
		queryBuilder.bindSelect(8);

		WhereCriterion where = new WhereCriterion("");
		where.bind(10);
		queryBuilder.where(where);

		queryBuilder.bindGroup(11);

		queryBuilder.bindOrder(12);

		Assert.assertNotNull(queryBuilder.getSelectParams());
		Assert.assertEquals(2, queryBuilder.getSelectParams().length);
		Assert.assertEquals(expectedParams.get(0), queryBuilder.getSelectParams()[0]);
		Assert.assertEquals(expectedParams.get(1), queryBuilder.getSelectParams()[1]);

		Assert.assertNotNull(queryBuilder.getFromParams());
		Assert.assertEquals(0, queryBuilder.getFromParams().length);


		Assert.assertNotNull(queryBuilder.getWhereParams());
		Assert.assertEquals(1, queryBuilder.getWhereParams().length);
		Assert.assertEquals(expectedParams.get(2), queryBuilder.getWhereParams()[0]);

		Assert.assertNotNull(queryBuilder.getGroupParams());
		Assert.assertEquals(1, queryBuilder.getGroupParams().length);
		Assert.assertEquals(expectedParams.get(3), queryBuilder.getGroupParams()[0]);

		Assert.assertNotNull(queryBuilder.getOrderParams());
		Assert.assertEquals(1, queryBuilder.getOrderParams().length);
		Assert.assertEquals(expectedParams.get(4), queryBuilder.getOrderParams()[0]);

		List<Object> params = Arrays.asList(queryBuilder.getParams());
		Assert.assertEquals(expectedParams.size(), params.size());
		for (int i = 0; i < expectedParams.size(); ++i) {
			Assert.assertEquals(expectedParams.get(i), params.get(i));
		}
	}

	@Test
	public void testIn() {
		String expectedWhere = "a = a AND b IN (?, ?, ?) AND 0 = 1 AND d IN (?, ?)";
		List<?> expectedParams = Arrays.asList(1, 2, 3, "ok1", "ok2");

		WhereCriterion where = new WhereCriterion(QueryOperatorEnum.AND);
		where.addCriterion("a = a");
		where.addIn("b", Arrays.asList(1, 2, 3));
		where.addIn("c", Collections.emptyList());
		where.addIn("d", Arrays.asList("ok1", "ok2"));

		Assert.assertEquals(expectedWhere, where.where());

		List<Object> params = where.getParams();
		Assert.assertEquals(expectedParams.size(), params.size());
		for (int i = 0; i < expectedParams.size(); ++i) {
			Assert.assertEquals(expectedParams.get(i), params.get(i));
		}
	}

	@Test
	public void testQueryBuilder() {
		String expectedQuery = "SELECT DISTINCT doc.ecm:uuid AS id, doc.dc:title "
				+ "FROM Document AS doc, Document AS doc2 "
				+ "WHERE doc.dc:title = doc2.dc:title AND (doc.dc:title = 'ok' OR doc2.dc:title = 'ok') "
				+ "GROUP BY doc.ecm:uuid, doc2.ecm:uuid " + "ORDER BY doc.dc:title ASC, doc2.dc:title DESC";

		UFNXQLQueryBuilder queryBuilder = new UFNXQLQueryBuilder();

		queryBuilder
				.select("doc." + NXQL.ECM_UUID, "id")
				.select("doc." + NXQL.ECM_NAME, "name")
				.select("doc." + CommonSchemaConstant.XPATH_DUBLINCORE_TITLE)
				.distinct()
				.removeSelectField("name")
				.from(NuxeoDocumentTypeConstant.TYPE_DOCUMENT, "doc")
				.from(NuxeoDocumentTypeConstant.TYPE_DOCUMENT, "doc2")
				.where("doc." + CommonSchemaConstant.XPATH_DUBLINCORE_TITLE + " = doc2."
						+ CommonSchemaConstant.XPATH_DUBLINCORE_TITLE)
				.where(new WhereCriterion(QueryOperatorEnum.OR, "doc." + CommonSchemaConstant.XPATH_DUBLINCORE_TITLE
						+ " = 'ok'", "doc2." + CommonSchemaConstant.XPATH_DUBLINCORE_TITLE + " = 'ok'"))
				.groupBy("doc." + NXQL.ECM_UUID).groupBy("doc2." + NXQL.ECM_UUID)
				.orderBy("doc." + CommonSchemaConstant.XPATH_DUBLINCORE_TITLE)
				.orderBy("doc2." + CommonSchemaConstant.XPATH_DUBLINCORE_TITLE, false);

		String query = queryBuilder.query();

		Assert.assertNotNull(query);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testQueryBuilderCopy() {
		UFNXQLQueryBuilder queryBuilder = new UFNXQLQueryBuilder();
		String expectedQuery = "SELECT ecm:uuid AS id, count FROM Document AS doc "
				+ "WHERE (1 OR (2 AND 3)) AND (4 AND 5) GROUP BY ok ORDER BY dc:title DESC, ecm:uuid ASC";

		queryBuilder.select("ecm:uuid", "id").select("count").from("Document", "doc");

		WhereCriterion where1 = new WhereCriterion(QueryOperatorEnum.OR, "1", new WhereCriterion(QueryOperatorEnum.AND,
				"2", "3"));
		WhereCriterion where2 = new WhereCriterion(QueryOperatorEnum.AND, "4", "5");
		where1.bind(3);
		queryBuilder.where(where1).where(where2);

		queryBuilder.groupBy("ok").orderBy("dc:title", false).orderBy("ecm:uuid");

		queryBuilder.bindSelect(1);
		queryBuilder.bindGroup(4);
		queryBuilder.bindOrder(5);

		UFNXQLQueryBuilder queryBuilderCopy = new UFNXQLQueryBuilder(queryBuilder);

		Assert.assertEquals(expectedQuery, queryBuilder.toString());
		Assert.assertEquals(queryBuilder.toString(), queryBuilderCopy.toString());

		Assert.assertTrue(queryBuilder != queryBuilderCopy);
		Assert.assertTrue(queryBuilder.getSelectRows() != queryBuilderCopy.getSelectRows());
		Assert.assertTrue(queryBuilder.getSelectRows().get(0) == queryBuilderCopy.getSelectRows().get(0));

		Assert.assertTrue(queryBuilder.getFroms() != queryBuilderCopy.getFroms());

		Assert.assertTrue(queryBuilder.getWhere() != queryBuilderCopy.getWhere());
		Assert.assertTrue(queryBuilder.getWhere().getOperator() == queryBuilderCopy.getWhere().getOperator());
		Assert.assertTrue(queryBuilder.getWhere().getCriteria() != queryBuilderCopy.getWhere().getCriteria());
		Assert.assertTrue(queryBuilder.getWhere().getCriteria().get(0) != queryBuilderCopy.getWhere().getCriteria().get(0));

		Assert.assertTrue(queryBuilder.getGroups() != queryBuilderCopy.getGroups());
		Assert.assertTrue(queryBuilder.getOrders() != queryBuilderCopy.getOrders());

		List<Object> params = Arrays.asList(queryBuilder.getParams());
		List<Object> paramsCopy = Arrays.asList(queryBuilderCopy.getParams());
		Assert.assertEquals(4, params.size());
		Assert.assertEquals(4, paramsCopy.size());		
		for (int i = 0; i < params.size(); ++i) {
			Assert.assertEquals(params.get(i), paramsCopy.get(i));
		}
	}

	@Test
	public void testQueryBuilderSelectAll() {
		String expectedQuery = "SELECT * FROM Document";

		UFNXQLQueryBuilder queryBuilder = new UFNXQLQueryBuilder();

		queryBuilder.selectAll().from("Document");

		String query = queryBuilder.query();

		Assert.assertNotNull(query);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testQueryBuilderCount() {
		String expectedQuery = "SELECT COUNT(*) AS count, COUNT(ecm:uuid) AS nbrows FROM Document";

		UFNXQLQueryBuilder queryBuilder = new UFNXQLQueryBuilder();

		queryBuilder.count().count(NXQL.ECM_UUID, "nbrows").from("Document");

		String query = queryBuilder.query();

		Assert.assertNotNull(query);
		Assert.assertEquals(expectedQuery, query);
	}
}
