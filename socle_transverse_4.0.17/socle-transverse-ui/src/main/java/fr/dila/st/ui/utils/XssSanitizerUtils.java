package fr.dila.st.ui.utils;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

/**
 * Taken from http://ricardozuasti.com/2012/stronger-anti-cross-site-scripting-xss-filter-for-java-web-apps/
 */
public final class XssSanitizerUtils {
    private static final STLogger LOGGER = STLogFactory.getLog(XssSanitizerUtils.class);
    public static final Encoder ESAPI_ENCODER = ESAPI.encoder();
    private static final Pattern NULL_CHARACTER_PATTERN = Pattern.compile("\0", Pattern.LITERAL);
    private static final List<String> SCRIPT_ATTRIBUTES = ImmutableList.of(
        "onabort",
        "onafterprint",
        "onbeforeprint",
        "onbeforeunload",
        "onblur",
        "oncanplay",
        "oncanplaythrough",
        "onchange",
        "onclick",
        "oncontextmenu",
        "oncopy",
        "oncuechange",
        "oncut",
        "ondblclick",
        "ondrag",
        "ondragend",
        "ondragenter",
        "ondragleave",
        "ondragover",
        "ondragstart",
        "ondrop",
        "ondurationchange",
        "onemptied",
        "onended",
        "onerror",
        "onfocus",
        "onhashchange",
        "oninput",
        "oninvalid",
        "onkeydown",
        "onkeypress",
        "onkeyup",
        "onload",
        "onloadeddata",
        "onloadedmetadata",
        "onloadstart",
        "onmousedown",
        "onmousemove",
        "onmouseout",
        "onmouseover",
        "onmouseup",
        "onmousewheel",
        "onoffline",
        "ononline",
        "onpagehide",
        "onpageshow",
        "onpaste",
        "onpause",
        "onplay",
        "onplaying",
        "onpopstate",
        "onprogress",
        "onratechange",
        "onreset",
        "onresize",
        "onscroll",
        "onsearch",
        "onseeked",
        "onseeking",
        "onselect",
        "onstalled",
        "onstorage",
        "onsubmit",
        "onsuspend",
        "ontimeupdate",
        "ontoggle",
        "onunload",
        "onvolumechange",
        "onwaiting",
        "onwheel"
    );

    private static final List<Pattern> SCRIPT_EXPRESSION_PATTERNS = SCRIPT_ATTRIBUTES
        .stream()
        .map(
            attr ->
                Pattern.compile(
                    "( " + attr + ".*?=\".*?\")",
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
                )
        )
        .collect(Collectors.toList());

    private static final List<Pattern> XSS_INPUT_PATTERNS = new ImmutableList.Builder<Pattern>()
        // Avoid on-based attributes expressions
        .addAll(SCRIPT_EXPRESSION_PATTERNS)
        // Avoid anything between script and iframe tags
        .add(Pattern.compile("<(script|iframe)(.*?)>(.*?)</(script|iframe)>", Pattern.CASE_INSENSITIVE))
        // Avoid src
        .add(
            Pattern.compile(
                "src[\r\n]*.?=[\r\n]*([^>]+)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
            )
        )
        // Remove any lonesome </script> tag
        .add(Pattern.compile("</script>", Pattern.CASE_INSENSITIVE))
        // Avoid eval(...) expressions
        .add(Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL))
        // Avoid expression(...) expressions
        .add(Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL))
        // Avoid javascript:/vbscript expressions
        .add(Pattern.compile("(javascript:|vbscript:)", Pattern.CASE_INSENSITIVE))
        .build();

    private XssSanitizerUtils() {
        //NOP
    }

    /**
     * This method takes a string and strips out any potential script injections.
     *
     * @param value the value to strip
     * @return String - the new "sanitized" string.
     */
    public static String stripXSS(String value) {
        String cleanValue = value;
        try {
            if (cleanValue != null) {
                // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to avoid
                // encoded attacks.
                cleanValue = ESAPI_ENCODER.canonicalize(cleanValue);

                // Avoid null characters
                cleanValue = NULL_CHARACTER_PATTERN.matcher(cleanValue).replaceAll("");

                // test against known XSS input patterns
                for (Pattern xssInputPattern : XSS_INPUT_PATTERNS) {
                    cleanValue = xssInputPattern.matcher(cleanValue).replaceAll("");
                }
            }
        } catch (Exception ex) {
            LOGGER.warn(
                STLogEnumImpl.LOG_EXCEPTION_TEC,
                ex,
                String.format("Impossible de valider la valeur : %s", value)
            );
        }

        return cleanValue;
    }
}
