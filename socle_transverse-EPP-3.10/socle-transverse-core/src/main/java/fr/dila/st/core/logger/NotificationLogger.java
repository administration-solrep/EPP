package fr.dila.st.core.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;

import fr.dila.st.core.logger.output.StdErrLog;
import fr.dila.st.core.logger.output.StdOutLog;

/**
 * Surcharge de l'implementation Log4J par défaut. Ce logger remplace le logger de Log4J via le fichier
 * commons-logging.properties. Il permet d'ajouter à la volée les informations de contexte (login, identifiant de
 * session, identifiant du dossier en cours).
 * 
 * @author bby
 */
public abstract class NotificationLogger extends Log4JLogger implements Log {
	private static final long	serialVersionUID	= 1L;

	// SPL : ajout dans une classe utilisée
	// pour redefinir statiquement les sortie d'erreur et standard
	// et les rediriger vers des logger
	static {
		StdErrLog.tieSystemErrToLog();
		StdOutLog.tieSystemOutToLog();
	}

	/**
	 * Creates a new instance of Log.
	 * 
	 * @param name
	 *            Nom du logger
	 */
	public NotificationLogger(String name) {
		super(name);
	}

	@Override
	public void trace(Object message) {
		super.trace(buildMessage(message));
	}

	@Override
	public void trace(Object message, Throwable t) {
		super.trace(buildMessage(message), t);
	}

	@Override
	public void debug(Object message) {
		super.debug(buildMessage(message));
	}

	@Override
	public void debug(Object message, Throwable t) {
		super.debug(buildMessage(message), t);
	}

	@Override
	public void info(Object message) {
		super.info(buildMessage(message));
	}

	@Override
	public void info(Object message, Throwable t) {
		super.info(buildMessage(message), t);
	}

	@Override
	public void warn(Object message) {
		super.warn(buildMessage(message));
	}

	@Override
	public void warn(Object message, Throwable t) {
		super.warn(buildMessage(message), t);
	}

	@Override
	public void error(Object message) {
		super.error(buildMessage(message));
	}

	@Override
	public void error(Object message, Throwable t) {
		super.error(buildMessage(message), t);
	}

	@Override
	public void fatal(Object message) {
		super.fatal(buildMessage(message));
	}

	@Override
	public void fatal(Object message, Throwable t) {
		super.fatal(buildMessage(message), t);
	}

	/**
	 * Concatène le message provenant de l'impementation du listener, et le message logué.
	 * 
	 * @param message
	 *            Message passé au logger
	 * @return Message concaténé
	 */
	private String buildMessage(Object message) {
		StringBuilder sb = new StringBuilder(getMessage()).append(" - ").append(message);

		return sb.toString();
	}

	/**
	 * Retourne les informations de contexte générées à la volée.
	 */
	protected abstract String getMessage();
}
