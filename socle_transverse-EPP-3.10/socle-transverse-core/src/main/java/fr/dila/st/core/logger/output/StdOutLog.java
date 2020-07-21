package fr.dila.st.core.logger.output;

import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class StdOutLog {

	private static final Log	LOGGER	= LogFactory.getLog(StdOutLog.class);

	/**
	 * utility class
	 */
	private StdOutLog() {
		// do nothing
	}

	public static void tieSystemOutToLog() {
		System.setOut(createProxy(System.out));
	}

	private static PrintStream createProxy(final PrintStream printStream) {
		return new PrintStream(printStream) {
			public void print(final String string) {
				printStream.print(string);
				LOGGER.info(string);
			}
		};
	}

}
