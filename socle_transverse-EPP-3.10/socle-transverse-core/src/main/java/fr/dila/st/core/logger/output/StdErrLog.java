package fr.dila.st.core.logger.output;

import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class StdErrLog {

	private static final Log	LOGGER	= LogFactory.getLog(StdErrLog.class);

	/**
	 * utility class
	 */
	private StdErrLog() {
		// do nothing
	}

	public static void tieSystemErrToLog() {
		System.setErr(createProxy(System.err));
	}

	private static PrintStream createProxy(final PrintStream printStream) {
		return new PrintStream(printStream) {
			public void print(final String string) {
				printStream.print(string);
				LOGGER.error(string);
			}
		};
	}

}
