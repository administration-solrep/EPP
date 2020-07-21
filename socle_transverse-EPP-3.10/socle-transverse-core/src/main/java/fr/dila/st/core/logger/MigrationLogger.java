package fr.dila.st.core.logger;

import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;

public final class MigrationLogger extends AbstractLogger {

	private static final String				FR_DILA_REPONSES_MIGRATION_LOGGER	= "fr.dila.st.migration.logger";

	private static final STLogger			LOGGER								= STLogFactory
																						.getLog(FR_DILA_REPONSES_MIGRATION_LOGGER);

	private static volatile MigrationLogger	instance;

	/**
	 * Constructeur privé.
	 */
	private MigrationLogger() {
		initLogger();
	}

	public static MigrationLogger getInstance() {
		if (instance == null) {
			// Le mot-clé synchronized sur ce bloc empêche toute instanciation
			// multiple même par différents "threads".
			synchronized (MigrationLogger.class) {
				if (instance == null) {
					instance = new MigrationLogger();
				}
			}
		}
		return instance;
	}

	public void logMigration(STLogEnum log, String message) {
		LOGGER.info(log, message);
	}

	public void logErrorMigration(STLogEnum codeLog, String message, Exception e) {
		LOGGER.error(null, codeLog, message, e);
	}
}
