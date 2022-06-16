package fr.dila.st.core.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class RegexUtils {

    private RegexUtils() {}

    /**
     * Renvoie l'ensemble des matchs d'un pattern dans une String donnée, dans la
     * limite imposée.
     *
     * @param limit max de matchs retourné, -1 si pas de limite.
     */
    public static List<String> listMatches(final Pattern pattern, final String target, int limit) {
        return StreamSupport
            .stream(allMatches(pattern, target).spliterator(), false)
            .limit(limit)
            .map(matchResult -> matchResult.group(0))
            .collect(Collectors.toList());
    }

    private static Iterable<MatchResult> allMatches(final Pattern p, final CharSequence input) {
        return () ->
            new Iterator<MatchResult>() {
                // Use a matcher internally.
                private final Matcher matcher = p.matcher(input);

                // Keep a match around that supports any interleaving of
                // hasNext/next calls.
                private MatchResult pending;

                @Override
                public boolean hasNext() {
                    // Lazily fill pending, and avoid calling find() multiple times
                    // if the
                    // clients call hasNext() repeatedly before sampling via next().
                    if (pending == null && matcher.find()) {
                        pending = matcher.toMatchResult();
                    }
                    return pending != null;
                }

                @Override
                public MatchResult next() {
                    // Fill pending if necessary (as when clients call next()
                    // without
                    // checking hasNext()), throw if not possible.
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    // Consume pending so next call to hasNext() does a find().
                    MatchResult next = pending;
                    pending = null;
                    return next;
                }

                /** Required to satisfy the interface, but unsupported. */
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
    }
}
