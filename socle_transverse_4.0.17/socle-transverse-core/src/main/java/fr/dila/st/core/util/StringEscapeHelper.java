package fr.dila.st.core.util;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class StringEscapeHelper {

    private StringEscapeHelper() {
        // NOP
    }

    private static final List<Pair<String, String>> CHARACTERS_TO_DECODE_MAIL = ImmutableList.of(
        Pair.of("&bsol;", "\\\\"),
        Pair.of("&percnt;", "%")
    );

    private static final List<Pair<String, String>> CHARACTERS_TO_DECODE_HTML = ImmutableList.of(
        Pair.of("'", "&apos;")
    );

    /**
     * Certains caractères sont encodés par TinyMCE mais ne sont alors plus lisibles par Outlook. On les décode spécifiquement avant l'envoi de mail.
     */
    public static String decodeSpecialCharactersMail(String content) {
        return decodeSpecialCharacters(content, CHARACTERS_TO_DECODE_MAIL);
    }

    public static String decodeSpecialCharactersHtmlParams(String content) {
        return decodeSpecialCharacters(content, CHARACTERS_TO_DECODE_HTML);
    }

    private static String decodeSpecialCharacters(String content, List<Pair<String, String>> list) {
        for (Pair<String, String> pair : list) {
            content = content.replaceAll(pair.getLeft(), pair.getRight());
        }
        return content;
    }
}
