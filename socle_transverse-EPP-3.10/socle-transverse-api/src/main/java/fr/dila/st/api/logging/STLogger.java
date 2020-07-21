package fr.dila.st.api.logging;

import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;

public interface STLogger {

	/* ************ LOGGER TRACE ************* */
	/**
	 * Methode trace spécifique à n'utiliser que si aucune session n'existe
	 */
	void trace(final STLogEnum codeEnum, final String message);

	/**
	 * Methode trace spécifique à n'utiliser que si aucune session n'existe
	 */
	void trace(final STLogEnum codeEnum);

	/**
	 * Permet de logger en mode trace
	 * 
	 * @param session
	 * @param codeEnum
	 * @param obj
	 *            L'objet ciblé par le log
	 * @param message
	 */
	void trace(final CoreSession session, final STLogEnum codeEnum, final Object obj, final String message);

	void trace(final CoreSession session, final STLogEnum codeEnum, final Object obj, final String message,
			final Throwable exc);

	void trace(final CoreSession session, final STLogEnum codeEnum);

	void trace(final CoreSession session, final STLogEnum codeEnum, final Object obj);

	void trace(final CoreSession session, final STLogEnum codeEnum, final List<Object> objs);

	void trace(final CoreSession session, final STLogEnum codeEnum, final Object[] objects);

	void trace(final CoreSession session, final STLogEnum codeEnum, final Object obj, final Throwable exc);

	void trace(final CoreSession session, final STLogEnum codeEnum, final Throwable exc);

	/* ************ LOGGER DEBUG ************* */
	/**
	 * Méthode debug spécifique à n'utiliser que si aucune session n'existe
	 */
	void debug(final STLogEnum codeEnum, final String message);

	/**
	 * Méthode debug spécifique à n'utiliser que si aucune session n'existe
	 */
	void debug(final STLogEnum codeEnum);

	void debug(final CoreSession session, final STLogEnum codeEnum, final Object obj, final String message);

	void debug(final CoreSession session, final STLogEnum codeEnum, final Object obj, final String message,
			final Throwable exc);

	void debug(final CoreSession session, final STLogEnum codeEnum);

	void debug(final CoreSession session, final STLogEnum codeEnum, final Object obj);

	void debug(final CoreSession session, final STLogEnum codeEnum, final List<Object> objs);

	void debug(final CoreSession session, final STLogEnum codeEnum, final Object[] objects);

	void debug(final CoreSession session, final STLogEnum codeEnum, final Object obj, final Throwable exc);

	void debug(final CoreSession session, final STLogEnum codeEnum, final Throwable exc);

	/* ************ LOGGER INFO ************* */
	/**
	 * Méthode info spécifique à n'utiliser que si aucune session n'existe
	 */
	void info(final STLogEnum codeEnum);

	/**
	 * Méthode info spécifique à n'utiliser que si aucune session n'existe
	 */
	void info(final STLogEnum codeEnum, final String message);

	void info(final CoreSession session, final STLogEnum codeEnum);

	void info(final CoreSession session, final STLogEnum codeEnum, final Object obj);

	void info(final CoreSession session, final STLogEnum codeEnum, final List<Object> objs);

	void info(final CoreSession session, final STLogEnum codeEnum, final Object[] objects);

	void info(final CoreSession session, final STLogEnum codeEnum, final Object obj, final Throwable exc);

	void info(final CoreSession session, final STLogEnum codeEnum, final Throwable exc);

	/* ************ LOGGER WARN ************* */
	/**
	 * Méthode warn spécifique à n'utiliser que si aucune session n'existe
	 */
	void warn(final STLogEnum codeEnum);

	/**
	 * Méthode warn spécifique à n'utiliser que si aucune session n'existe
	 */
	void warn(final STLogEnum codeEnum, final Throwable exc);
	
	/**
	 * Méthode warn spécifique à n'utiliser que si aucune session n'existe
	 */
	void warn(final STLogEnum codeEnum, final String message);

	void warn(final CoreSession session, final STLogEnum codeEnum);

	void warn(final CoreSession session, final STLogEnum codeEnum, final Object obj);

	void warn(final CoreSession session, final STLogEnum codeEnum, final List<Object> objs);

	void warn(final CoreSession session, final STLogEnum codeEnum, final Object[] objects);

	void warn(final CoreSession session, final STLogEnum codeEnum, final Object obj, final Throwable exc);

	void warn(final CoreSession session, final STLogEnum codeEnum, final Throwable exc);

	/* ************ LOGGER ERROR ************* */
	/**
	 * Méthode error spécifique à n'utiliser que si aucune session n'existe
	 */
	void error(final STLogEnum codeEnum);

	/**
	 * Méthode error spécifique à n'utiliser que si aucune session n'existe
	 */
	void error(final STLogEnum codeEnum, final Throwable exc);

	void error(final CoreSession session, final STLogEnum codeEnum);

	void error(final CoreSession session, final STLogEnum codeEnum, final Object obj);

	void error(final CoreSession session, final STLogEnum codeEnum, final List<Object> objs);

	void error(final CoreSession session, final STLogEnum codeEnum, final Object[] objects);

	void error(final CoreSession session, final STLogEnum codeEnum, final Object obj, final Throwable exc);

	void error(final CoreSession session, final STLogEnum codeEnum, final Throwable exc);

	/* ************ LOGGER FATAL ************* */
	/**
	 * Méthode fatal spécifique à n'utiliser que si aucune session n'existe
	 */
	void fatal(final STLogEnum codeEnum);

	/**
	 * Méthode fatal spécifique à n'utiliser que si aucune session n'existe
	 */
	void fatal(final STLogEnum codeEnum, final Throwable exc);

	void fatal(final CoreSession session, final STLogEnum codeEnum);

	void fatal(final CoreSession session, final STLogEnum codeEnum, final Object obj);

	void fatal(final CoreSession session, final STLogEnum codeEnum, final List<Object> objs);

	void fatal(final CoreSession session, final STLogEnum codeEnum, final Object[] objects);

	void fatal(final CoreSession session, final STLogEnum codeEnum, final Object obj, final Throwable exc);

	void fatal(final CoreSession session, final STLogEnum codeEnum, final Throwable exc);

	boolean isDebugEnable();

	boolean isInfoEnable();
}
