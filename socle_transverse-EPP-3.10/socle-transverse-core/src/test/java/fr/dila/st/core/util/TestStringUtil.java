package fr.dila.st.core.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.nuxeo.ecm.core.api.ClientException;

import junit.framework.TestCase;

/**
 * Test de la classe StringUtil.
 * 
 * @author jtremeaux
 */
public class TestStringUtil extends TestCase {

	public void testJoin() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("java");
		list.add("c#");
		String[] strings = { "java", "c#" };

		String res = StringUtil.join(list, ",", "'");
		assertEquals("'java','c#'", res);
		res = StringUtil.join(strings, ",", "'");
		assertEquals("'java','c#'", res);
	}

	public void testGetQuestionMark() {
		assertEquals("", StringUtil.getQuestionMark(-1));
		assertEquals("", StringUtil.getQuestionMark(0));
		assertEquals("?,?,?", StringUtil.getQuestionMark(3));
	}

	public void testStringToDate() throws ClientException {
		Calendar calendar = new GregorianCalendar();
		Date date = StringUtil.stringToDate("22/10/2010");
		calendar.setTime(date);
		assertEquals(22, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(9, calendar.get(Calendar.MONTH));
		assertEquals(2010, calendar.get(Calendar.YEAR));
	}

	public void testStringFormatWithLitteralMonthDateToDate() throws ClientException {
		Calendar calendar = new GregorianCalendar();
		Date date = StringUtil.stringFormatWithLitteralMonthDateToDate("22   Septembre  2010");
		calendar.setTime(date);
		assertEquals(22, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(8, calendar.get(Calendar.MONTH));
		assertEquals(2010, calendar.get(Calendar.YEAR));

		calendar = new GregorianCalendar();
		date = StringUtil.stringFormatWithLitteralMonthDateToDate("22   DÉCEMBRE  2010");
		calendar.setTime(date);
		assertEquals(22, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(11, calendar.get(Calendar.MONTH));
		assertEquals(2010, calendar.get(Calendar.YEAR));

	}

	public void testDeleteHtmlComment() {
		String patternWithHtmlComment = "La <strong>petite</strong> <i>maison</i> <!-- dans la prairie --> est bleue";
		String patternWithoutHtmlComment = "La <strong>petite</strong> <i>maison</i>  est bleue";
		assertEquals(patternWithoutHtmlComment, StringUtil.deleteHtmlComment(patternWithHtmlComment));
	}

	public void testDeletePattern() {
		final String stringToClean = "Une phrase [DELETE]lkjgfdjlhglkfdjhlkj[/DELETE] avec des trucs à supprimer";
		final String stringCleaned = "Une phrase  avec des trucs à supprimer";
		final String marqueurDebut = "[DELETE]";
		final String marqueurFin = "[/DELETE]";

		String result = StringUtil.deletePattern(stringToClean, marqueurDebut, marqueurFin);
		assertEquals(stringCleaned, result);
		result = StringUtil.deletePattern(stringCleaned, marqueurDebut, marqueurFin);
		assertEquals(stringCleaned, result);
	}

	public void testSubstring() {
		final String stringToClean = "Du bruit avant [BETWEEN] Ce qu'on veut [/BETWEEN] avec des trucs à après qu'on veut pas";
		final String stringCleaned = "[BETWEEN] Ce qu'on veut [/BETWEEN]";
		final String stringWithoutInfos = "Pas de marqueurs dans cette phrase";
		final String marqueurDebut = "[BETWEEN]";
		final String marqueurFin = "[/BETWEEN]";

		String result = StringUtil.substring(stringToClean, marqueurDebut, marqueurFin);
		assertEquals(stringCleaned, result);
		result = StringUtil.substring(stringWithoutInfos, marqueurDebut, marqueurFin);
		assertEquals("", result);
	}

	public void testDeleteCharNotUTF8() {
		final String strWithWrongChar = "String avec : 我喜歡麵食, et puis : 我想移动它移动 et enfin un truc en français";
		final String stringCleaned = "String avec :      , et puis :         et enfin un truc en français";

		String result = StringUtil.deleteCharNotUTF8(strWithWrongChar);
		assertEquals(stringCleaned, result);
		result = StringUtil.deleteCharNotUTF8(null);
		assertNull(result);
	}

	public void testGetShorterName() {
		String longString = "A very very very very very long string";
		String shorterString = "A v... string";

		String result = StringUtil.getShorterName(longString);
		assertEquals(shorterString, result);
		result = StringUtil.getShorterName(shorterString);
		assertEquals(shorterString, result);
	}

	public void testRemoveAccent() {
		final String stringWithAccent = "àÀâÂäÄéÉèÈêÊëËîÎïÏôÔöÖûÛüÜùÙçÇŷŶÿŸ";
		final String stringWithoutAccent = "aAaAaAeEeEeEeEiIiIoOoOuUuUuUcCyYyY";

		String result = StringUtil.removeAccent(stringWithAccent);
		assertEquals(stringWithoutAccent, result);
	}

	public void testRemoveSpaces() {
		final String stringWithSpaces = "La petite maison dans la prairie";
		final String stringWithoutSpaces = "La_petite_maison_dans_la_prairie";

		String result = StringUtil.removeSpaces(stringWithSpaces);
		assertEquals(stringWithoutSpaces, result);
	}

	public void testRemoveSpacesAndAccent() {
		final String stringWithSpacesAndAccent = "Un éléphant ça trompe énormément";
		final String stringWithoutSpacesAndAccent = "Un_elephant_ca_trompe_enormement";

		String result = StringUtil.removeSpacesAndAccent(stringWithSpacesAndAccent);
		assertEquals(stringWithoutSpacesAndAccent, result);
	}

	public void testEscapeQuotes() {
		final String stringWithQuotes = "L'alarme du \"voisin\"";
		final String stringWithEscapedQuotes = "L\\'alarme du \\'\\'voisin\\'\\'";

		String result = StringUtil.escapeQuotes(stringWithQuotes);
		assertEquals(stringWithEscapedQuotes, result);
	}
}
