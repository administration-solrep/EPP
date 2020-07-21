package fr.dila.st.core.factory;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.logger.STLoggerImpl;

public class STLogFactory {

	public static STLogger getLog(Class<?> classLoggante) {
		return new STLoggerImpl(classLoggante);
	}

	public static STLogger getLog(String classLoggante) {
		return new STLoggerImpl(classLoggante);
	}
}
