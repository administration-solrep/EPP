package fr.sword.naiad.commons.xml.jaxb;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import fr.sword.naiad.commons.core.utils.WhoCalledMe;

/**
 * A dummy CharacterEscapeHandler that does not escape anything
 * @author fbarmes 
 *
 */
public class CdataCharacterEscapeHandler implements CharacterEscapeHandler {

    private static final Logger LOGGER = Logger.getLogger(CdataCharacterEscapeHandler.class);

    public CdataCharacterEscapeHandler() {
        super();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("CdataCharacterEscapeHandler constructed");
        }

    }

    /**
     * @param ch The array of characters.
     * @param start The starting position.
     * @param length The number of characters to use.
     * @param isAttVal true if this is an attribute value literal.
     */
    @Override
    public void escape(final char[] ch, final int start, final int length, final boolean isAttVal, final Writer writer)
            throws IOException {

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("WhoCalledMe:");
            LOGGER.trace(WhoCalledMe.getCallStack());
            LOGGER.trace("CdataCharacterEscapeHandler escape data " + Arrays.toString(ch));
        }

        if (CDataAdapter.isCdata(new String(ch))) {

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("CdataCharacterEscapeHandler escape data -> found CDATA no escaping");
            }

            writer.write(ch, start, length);
        } else {

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("CdataCharacterEscapeHandler escape data -> escaping");
            }

            useStandardEscape(ch, start, length, isAttVal, writer);
        }
    }

    private void useStandardEscape(final char[] ch, final int start, final int length, final boolean isAttVal,
            final Writer writer) throws IOException {
        final CharacterEscapeHandler escapeHandler = StandardEscapeHandler.getInstance();

        escapeHandler.escape(ch, start, length, isAttVal, writer);
    }

    /**
     * 
     * @author fbarmes
     *
     */
    private static final class StandardEscapeHandler implements CharacterEscapeHandler {

        private static StandardEscapeHandler instance;

        public static StandardEscapeHandler getInstance() {

            if (instance == null) {
                instance = new StandardEscapeHandler();
            }

            return instance;
        }

        private StandardEscapeHandler() {
            super();
        }

        @Override
        public void escape(final char[] ch, int start, final int length, final boolean isAttVal, final Writer out)
                throws IOException {

            final int limit = start + length;
            for (int i = start; i < limit; i++) {
                final char c = ch[i];

                if (c == '&' || c == '<' || c == '>' || c == '\"' && isAttVal || c == '\'' && isAttVal) {

                    if (i != start) {
                        out.write(ch, start, i - start);
                    }

                    start = i + 1;

                    switch (ch[i]) {
                        case '&':
                            out.write("&amp;");
                            break;

                        case '<':
                            out.write("&lt;");
                            break;

                        case '>':
                            out.write("&gt;");
                            break;

                        case '\"':
                            out.write("&quot;");
                            break;

                        case '\'':
                            out.write("&apos;");
                            break;

                        default:
                    }
                }
            }

            if (start != limit) {
                out.write(ch, start, limit - start);
            }
        }

    }

}
