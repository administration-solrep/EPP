package fr.sword.naiad.nuxeo.commons.core.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class SlugUtils {

    // regex
    private static final String REGEX_ALPHA_DASHES_SPACES = "[^a-z0-9-\\s]";
    private static final String REGEX_N_SPACES = "\\s{2,}";
    private static final String REGEX_ALPHA_DASH = "[^a-z0-9-]";

    // replacement string
    private static final String NONE = "";
    private static final String SPACE = " ";
    private static final String DASH = "-";

    // slug incrementation format
    private static final String SLUG_INCR_FMT = "%s-%02d";
    private static final String SLUG_INCR_PATTERN = ".*-[0-9][0-9]";
    private static final int SLUG_INCR_SUFFIX_LEN = 3;

    private SlugUtils() {

    }

    public static String slugify(final String input) {

        String result = input;

        // --- replace accented characters
        result = unAccent(result);

        // --- convert to lowercase
        result = result.toLowerCase();

        // --- remove spécial characters
        result = result.replaceAll(REGEX_ALPHA_DASHES_SPACES, NONE);

        // remove space at end and beginning
        result = result.trim();

        // --- n spaces becomes one space
        result = result.replaceAll(REGEX_N_SPACES, SPACE);

        // --- replace invalid characters
        return result.replaceAll(REGEX_ALPHA_DASH, DASH);

    }

    public static String unAccent(final String input) {

        final String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public static String extractBaseSlug(final String slug) {
        if (slug != null && slug.matches(SLUG_INCR_PATTERN)) {
            return slug.substring(0, slug.length() - SLUG_INCR_SUFFIX_LEN);
        } else {
            return slug;
        }
    }

    public static String incrBaseSlug(final String baseSlug, final int index) {
        if (baseSlug == null) {
            throw new IllegalArgumentException("baseSlug should not be null");
        }
        return String.format(SLUG_INCR_FMT, baseSlug, index);
    }

}
