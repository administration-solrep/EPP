package fr.dila.st.api.util;

public final class XPathUtils {

    public static String xPath(String prefix, String property) {
        return prefix + ":" + property;
    }

    private XPathUtils() {}
}
