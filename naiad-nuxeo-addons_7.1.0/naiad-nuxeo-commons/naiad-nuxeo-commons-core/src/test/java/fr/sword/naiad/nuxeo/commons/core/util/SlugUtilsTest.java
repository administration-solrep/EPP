package fr.sword.naiad.nuxeo.commons.core.util;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class SlugUtilsTest {

    protected static final Logger LOGGER = Logger.getLogger(SlugUtilsTest.class);

    @Test
    public void testUnAccent() throws Exception {
        String input = "È,É,Ê,Ë,Û,Ù,Ï,Î,À,Â,Ô,è,é,ê,ë,û,ù,ï,î,à,â,ô";
        String expected = "E,E,E,E,U,U,I,I,A,A,O,e,e,e,e,u,u,i,i,a,a,o";
        String actual = SlugUtils.unAccent(input);
        Assert.assertEquals(expected, actual);

        input = "ö,ò,ñ,ÿ,ü,ä,ì,õ,ã,Ö,Ò,Ñ,Ü,Â,Ì,Õ,Ã,ç,Ç";
        expected = "o,o,n,y,u,a,i,o,a,O,O,N,U,A,I,O,A,c,C";
        actual = SlugUtils.unAccent(input);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSlugify() throws Exception {

        String title = null;
        String actual = null;
        String expected = null;

        //--- check spaces are replaced by -
        title = "this is a simple title";
        expected = "this-is-a-simple-title";
        actual = SlugUtils.slugify(title);
        Assert.assertEquals(expected, actual);

        //--- check uppercases are transformed
        title = "This is a Title WITH UpperCases and Lowercases";
        expected = "this-is-a-title-with-uppercases-and-lowercases";
        actual = SlugUtils.slugify(title);
        Assert.assertEquals(expected, actual);

        //--- check special characters are replaces
        title = "titre avec des éléments accentués";
        expected = "titre-avec-des-elements-accentues";
        actual = SlugUtils.slugify(title);
        Assert.assertEquals(expected, actual);

        //--- check special characters are replaces
        title = "Ceci & Cela avec deux-un et CODE_TYPE et des caractères spéciaux (£$ %µ) à la fin";
        expected = "ceci-cela-avec-deux-un-et-codetype-et-des-caracteres-speciaux-a-la-fin";
        actual = SlugUtils.slugify(title);
        Assert.assertEquals(expected, actual);

        //--- question mark
        title = "une question comme titre ?";
        expected = "une-question-comme-titre";
        actual = SlugUtils.slugify(title);
        Assert.assertEquals(expected, actual);

        //--- space at end and begin
        title = "  des espaces au debut et en fin   ";
        expected = "des-espaces-au-debut-et-en-fin";
        actual = SlugUtils.slugify(title);
        Assert.assertEquals(expected, actual);

        //--- check accent, tabulation, number
        title = "\t²,È,É,Ê,Ë,Û,Ù,Ï,Î,À,Â,Ô,è,é,ê,ë,û,ù,ï,î,à,â,ô,ö,ò,\t,ñ,ÿ,ü,ä,ì,õ,ã,Ö,Ò,Ñ,Ü,Â,Ì,Õ,Ã,123,---,ç,Ç,€,@,µ ? \t ";
        expected = "eeeeuuiiaaoeeeeuuiiaaooo-nyuaioaoonuaioa123---cc";
        actual = SlugUtils.slugify(title);
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void testExtractBaseSlug() {

        Assert.assertNull(SlugUtils.extractBaseSlug(null));

        String slug = "mon-slug";
        String expectedSlug = "mon-slug";
        Assert.assertEquals(expectedSlug, SlugUtils.extractBaseSlug(slug));

        slug = "mon-slug-50";
        expectedSlug = "mon-slug";
        Assert.assertEquals(expectedSlug, SlugUtils.extractBaseSlug(slug));

        slug = "mon-slug-505";
        expectedSlug = "mon-slug-505";
        Assert.assertEquals(expectedSlug, SlugUtils.extractBaseSlug(slug));

        slug = "mon-55-slug-00";
        expectedSlug = "mon-55-slug";
        Assert.assertEquals(expectedSlug, SlugUtils.extractBaseSlug(slug));

        slug = "mon-55-slug-0";
        expectedSlug = "mon-55-slug-0";
        Assert.assertEquals(expectedSlug, SlugUtils.extractBaseSlug(slug));
    }

    @Test
    public void testIncrSlug() {
        final String baseSlug = "mon-slug";
        int index = 1;
        String expected = "mon-slug-01";
        Assert.assertEquals(expected, SlugUtils.incrBaseSlug(baseSlug, index));

        index = 55;
        expected = "mon-slug-55";
        Assert.assertEquals(expected, SlugUtils.incrBaseSlug(baseSlug, index));

        index = 155;
        expected = "mon-slug-155";
        Assert.assertEquals(expected, SlugUtils.incrBaseSlug(baseSlug, index));

        try {
            SlugUtils.incrBaseSlug(null, 1);
        } catch (final IllegalArgumentException e) {
            // expected exception
        }
    }

}
