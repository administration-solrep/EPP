package fr.dila.st.core.requeteur;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import fr.dila.st.core.util.DateUtil;

/**
 * Classe de test pour @see {@link STIncrementalSmartNXQLQuery} Teste la fonctionnalité de date dynamique
 * 
 * @author jgomez
 * 
 */
public class TestStSmartQuery {

	@Test
	public void testEmptyValues() {
		STIncrementalSmartNXQLQuery query = new STIncrementalSmartNXQLQuery("d.dos.numerorNor LIKE 'A*'");
		String resultQuery = query.buildQuery();
		Assert.assertEquals("d.dos.numerorNor LIKE 'A*'", resultQuery);
	}

	@Test
	public void testStringValue() {
		STIncrementalSmartNXQLQuery query = new STIncrementalSmartNXQLQuery("d.dos.numerorNor LIKE 'A*'");
		query.setConditionalOperator("=");
		query.setLogicalOperator("AND");
		query.setLeftExpression("d.dos:posteCreator");
		query.setStringValue("123");
		String resultQuery = query.buildQuery();
		Assert.assertEquals("d.dos.numerorNor LIKE 'A*' AND d.dos:posteCreator = \"123\"", resultQuery);
	}

	@Test
	public void testDateValue() {
		STIncrementalSmartNXQLQuery query = new STIncrementalSmartNXQLQuery("d.dos.numerorNor LIKE 'A*'");
		query.setLogicalOperator("AND");
		query.setConditionalOperator("BETWEEN");
		query.setLeftExpression("d.dos:date1");
		Date today = new Date();
		query.setDateValue(today);
		query.setOtherDateValue(today);
		String resultQuery = query.buildQuery();
		String dateStr = DateUtil.formatYYYYMMdd(today);
		// On ajoute un jour à la date pour l'inclusion
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.setTime(today);
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		String tomorrowStr = DateUtil.formatYYYYMMdd(tomorrow.getTime());

		Assert.assertEquals("d.dos.numerorNor LIKE 'A*' AND d.dos:date1 BETWEEN DATE \"" + dateStr + "\" AND DATE \""
				+ tomorrowStr + "\"", resultQuery);
	}

	@Test
	public void testDynDateValue() {
		STIncrementalSmartNXQLQuery query = new STIncrementalSmartNXQLQuery("d.dos.numerorNor LIKE 'A*'");
		query.setLogicalOperator("AND");
		query.setConditionalOperator("BETWEEN");
		query.setLeftExpression("d.dos:date1");

		query.setDynDynamicDateSelector("DYNAMIC");
		query.setDynDateDynamicValue("ufnxql_date:(NOW-10J)");

		query.setDynDynamicDateOtherSelector("DYNAMIC");
		query.setDynDateStaticOtherValue(new Date());
		query.setDynDateDynamicOtherValue("ufnxql_date:(NOW-3J)");

		String resultQuery = query.buildQuery();
		Assert.assertEquals(
				"d.dos.numerorNor LIKE 'A*' AND d.dos:date1 BETWEEN ufnxql_date:(NOW-10J) AND ufnxql_date:(NOW-3J)",
				resultQuery);
	}

	@Test
	public void testDynDateValue2() {
		STIncrementalSmartNXQLQuery query = new STIncrementalSmartNXQLQuery("d.dos.numerorNor LIKE 'A*'");
		Date today = new Date();

		query.setLogicalOperator("AND");
		query.setConditionalOperator("BETWEEN");
		query.setLeftExpression("d.dos:date1");

		query.setDynDynamicDateSelector("DYNAMIC");
		query.setDynDateDynamicValue("ufnxql_date:(NOW-10J)");

		query.setDynDynamicDateOtherSelector("STATIC");
		query.setDynDateStaticOtherValue(new Date());

		// On ajoute un jour à la date pour l'inclusion
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.setTime(today);
		tomorrow.add(Calendar.DAY_OF_MONTH, 1);
		String tomorrowStr = DateUtil.formatYYYYMMdd(tomorrow.getTime());

		String resultQuery = query.buildQuery();
		Assert.assertEquals("d.dos.numerorNor LIKE 'A*' AND d.dos:date1 BETWEEN ufnxql_date:(NOW-10J) AND DATE \""
				+ tomorrowStr + "\"", resultQuery);
	}

}
