package fr.dila.st.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test de la classe StringHelper.
 *
 * @author jtremeaux
 */
public class StringHelperTest {

    @Test
    public void testJoin() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("java");
        list.add("c#");
        String[] strings = { "java", "c#" };

        String res = StringUtil.join(list, ",", "'");
        Assert.assertEquals("'java','c#'", res);
        res = StringHelper.join(strings, ",", "'");
        Assert.assertEquals("'java','c#'", res);
    }

    @Test
    public void testGetQuestionMark() {
        Assert.assertEquals("", StringHelper.getQuestionMark(-1));
        Assert.assertEquals("", StringHelper.getQuestionMark(0));
        Assert.assertEquals("?,?,?", StringHelper.getQuestionMark(3));
    }

    @Test
    public void testStringFormatWithLitteralMonthDateToDate() {
        Calendar calendar = new GregorianCalendar();
        Date date = StringHelper.stringFormatWithLitteralMonthDateToDate("22   Septembre  2010");
        calendar.setTime(date);
        Assert.assertEquals(22, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(8, calendar.get(Calendar.MONTH));
        Assert.assertEquals(2010, calendar.get(Calendar.YEAR));

        calendar = new GregorianCalendar();
        date = StringHelper.stringFormatWithLitteralMonthDateToDate("22   DÉCEMBRE  2010");
        calendar.setTime(date);
        Assert.assertEquals(22, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(11, calendar.get(Calendar.MONTH));
        Assert.assertEquals(2010, calendar.get(Calendar.YEAR));
    }

    @Test
    public void testDeleteHtmlComment() {
        String patternWithHtmlComment = "La <strong>petite</strong> <i>maison</i> <!-- dans la prairie --> est bleue";
        String patternWithoutHtmlComment = "La <strong>petite</strong> <i>maison</i>  est bleue";
        Assert.assertEquals(patternWithoutHtmlComment, StringHelper.deleteHtmlComment(patternWithHtmlComment));

        String patternWithMultipleLineHtmlComment =
            "La <strong>petite</strong> <i>maison</i> <!-- dans la prairie --> est bleue<!-- Encore un commentaire \n" +
            "on en a marre -->";
        Assert.assertEquals(
            patternWithoutHtmlComment,
            StringHelper.deleteHtmlComment(patternWithMultipleLineHtmlComment)
        );
    }

    @Test
    public void testSubstring() {
        final String stringToClean =
            "Du bruit avant [BETWEEN] Ce qu'on veut [/BETWEEN] avec des trucs à après qu'on veut pas";
        final String stringCleaned = "[BETWEEN] Ce qu'on veut [/BETWEEN]";
        final String stringWithoutInfos = "Pas de marqueurs dans cette phrase";
        final String marqueurDebut = "[BETWEEN]";
        final String marqueurFin = "[/BETWEEN]";

        String result = StringHelper.substring(stringToClean, marqueurDebut, marqueurFin);
        Assert.assertEquals(stringCleaned, result);
        result = StringHelper.substring(stringWithoutInfos, marqueurDebut, marqueurFin);
        Assert.assertEquals("", result);
    }

    @Test
    public void testDeleteCharNotUTF8() {
        final String strWithWrongChar =
            "String avec : 我喜歡麵食, et puis : 我想移动它移动 et enfin un truc en français";
        final String stringCleaned = "String avec :      , et puis :         et enfin un truc en français";

        String result = StringHelper.deleteCharNotUTF8(strWithWrongChar);
        Assert.assertEquals(stringCleaned, result);
        result = StringHelper.deleteCharNotUTF8(null);
        Assert.assertNull(result);
    }

    @Test
    public void testRemoveSpaces() {
        final String stringWithSpaces = "La petite maison dans la prairie";
        final String stringWithoutSpaces = "La_petite_maison_dans_la_prairie";

        String result = StringHelper.removeSpaces(stringWithSpaces);
        assertThat(stringWithoutSpaces).isEqualTo(result);
    }

    @Test
    public void testRemoveSpacesAndAccent() {
        final String stringWithSpacesAndAccent = "Un éléphant ça trompe énormément";
        final String stringWithoutSpacesAndAccent = "Un_elephant_ca_trompe_enormement";

        String result = StringHelper.removeSpacesAndAccent(stringWithSpacesAndAccent);
        assertThat(stringWithoutSpacesAndAccent).isEqualTo(result);
    }

    @Test
    public void testConvertToId() {
        final String stringWithSpecialCharacters = "AÂÀÆ-EËÉ-IÏ-NÑ-aàäuùü2021_% @)=}";
        final String stringWithoutSpecialCharacters = "AAA-EEE-II-NN-aaauuu2021";

        String result = StringHelper.convertToId(stringWithSpecialCharacters);
        assertThat(stringWithoutSpecialCharacters).isEqualTo(result);
    }

    @Test
    public void testEscapeQuotes() {
        final String stringWithQuotes = "L'alarme du \"voisin\"";
        final String stringWithEscapedQuotes = "L\\'alarme du \\'\\'voisin\\'\\'";

        String result = StringHelper.escapeQuotes(stringWithQuotes);
        assertThat(stringWithEscapedQuotes).isEqualTo(result);
    }

    @Test
    public void testContainsIgnoreCase() {
        List<String> strings = ImmutableList.of("CHAT", "CHien", "Tortue");

        assertThat(StringHelper.containsIgnoreCase(strings, "chat")).isTrue();
        assertThat(StringHelper.containsIgnoreCase(strings, "CHien")).isTrue();
        assertThat(StringHelper.containsIgnoreCase(strings, "TORTUE")).isTrue();
        assertThat(StringHelper.containsIgnoreCase(strings, "lapin")).isFalse();
    }
}
