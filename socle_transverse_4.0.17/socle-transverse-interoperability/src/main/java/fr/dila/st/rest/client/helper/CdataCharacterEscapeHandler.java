package fr.dila.st.rest.client.helper;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import fr.dila.st.utils.WhoCalledMe;

/**
 * A dummy CharacterEscapeHandler that does not escape anything
 * 
 * @author fbarmes
 * 
 */
public class CdataCharacterEscapeHandler implements CharacterEscapeHandler {

	private static final Logger	LOGGER	= LogManager.getLogger(CdataCharacterEscapeHandler.class);

	public CdataCharacterEscapeHandler() {
		super();

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("CdataCharacterEscapeHandler constructed");
		}

	}

	/**
	 * @param characters
	 *            The array of characters.
	 * @param start
	 *            The starting position.
	 * @param length
	 *            The number of characters to use.
	 * @param isAttVal
	 *            true if this is an attribute value literal.
	 */
	@Override
	public void escape(char[] characters, int start, int length, boolean isAttVal, Writer writer) throws IOException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("WhoCalledMe:");
			LOGGER.trace(WhoCalledMe.getCallStack());
			LOGGER.trace("CdataCharacterEscapeHandler escape data " + Arrays.toString(characters));
		}

		if (CDataAdapter.isCdata(new String(characters))) {

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("CdataCharacterEscapeHandler escape data -> found CDATA no escaping");
			}

			writer.write(characters, start, length);
		} else {

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("CdataCharacterEscapeHandler escape data -> escaping");
			}

			useStandardEscape(characters, start, length, isAttVal, writer);
		}
	}

	private void useStandardEscape(char[] characters, int start, int length, boolean isAttVal, Writer writer)
			throws IOException {
		CharacterEscapeHandler escapeHandler = StandardEscapeHandler.getInstance();

		escapeHandler.escape(characters, start, length, isAttVal, writer);
	}

	/**
	 * 
	 * @author fbarmes
	 * 
	 */
	private static final class StandardEscapeHandler implements CharacterEscapeHandler {

		private static StandardEscapeHandler	instance;

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
		public void escape(char[] characters, int start, int length, boolean isAttVal, Writer out) throws IOException {

			int limit = start + length;
			for (int i = start; i < limit; i++) {
				char c = characters[i];

				if (c == '&' || c == '<' || c == '>' || (c == '\"' && isAttVal) || (c == '\'' && isAttVal)) {

					if (i != start) {
						out.write(characters, start, i - start);
					}

					start = i + 1;

					switch (characters[i]) {
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
					}
				}
			}

			if (start != limit) {
				out.write(characters, start, limit - start);
			}
		}

	}

}
