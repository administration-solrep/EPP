package fr.dila.st.ui.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class XssSanitizerUtilsTest {

    @Test
    public void testStripXSS() {
        String value =
            "<script>test1</script>" +
            "<iframe title=\"test avec iframe\" src=\"https://test.com>test 2</iframe>" +
            "<p>Hello!</p>" +
            "<script>" +
            "</script>" +
            "<script type=\"text/javascript\">function testLoadFunction() {alert('ok');}window.onload = testLoadFunction;</script>";

        String cleanValue = XssSanitizerUtils.stripXSS(value);

        assertThat(cleanValue).isEqualTo("<p>Hello!</p>");
    }

    @Test
    public void testStripXSSWithOnScriptExpressions() {
        String value =
            "<div><p onclick=\"alert('Injection')\">Test</p>" +
            "<span onload=\"doBadThings()\">On Load</span>" +
            "<span onmouseover=\"explode()\">Over It</span></div>";

        String cleanValue = XssSanitizerUtils.stripXSS(value);

        System.out.println(cleanValue);
        assertThat(cleanValue).isEqualTo("<div><p>Test</p><span>On Load</span><span>Over It</span></div>");
    }

    @Test
    public void testStripXSSWithOnSrcExpressions() {
        String value = "<img src=\'\"test'>";

        String cleanValue = XssSanitizerUtils.stripXSS(value);

        assertThat(cleanValue).isEqualTo("<img >");
    }

    @Test
    public void testStripXSSWithOnImgExpressions() {
        String value = "<img/ src = 'X'/onerror=\"alert('Coexya XSS img')\">";

        String cleanValue = XssSanitizerUtils.stripXSS(value);

        assertThat(cleanValue).isEqualTo("<img/ >");
    }

    @Test
    public void testStripXSSWithOnJavascriptExpressions() {
        String value = "vbscript:dumjs";

        String cleanValue = XssSanitizerUtils.stripXSS(value);

        assertThat(cleanValue).isEqualTo("dumjs");
    }
}
