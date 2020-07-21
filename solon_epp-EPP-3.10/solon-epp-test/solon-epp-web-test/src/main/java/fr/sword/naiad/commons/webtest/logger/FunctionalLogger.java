package fr.sword.naiad.commons.webtest.logger;

import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.sword.naiad.commons.webtest.logger.WebLogger;

/**
 * 
 * @author SPL
 * 
 */
public final class FunctionalLogger implements WebLogger {

	private static final Log LOG = LogFactory.getLog(FunctionalLogger.class);

	private static final String KEY_TEST_STARTED = "TEST_STARTED";

	private static final String KEY_TEST_FINISHED = "TEST_FINISHED";

	private static final String KEY_START_ACTION = "START_ACTION";

	private static final String KEY_END_ACTION = "END_ACTION";

	private static final String KEY_START_CHECK = "START_CHECK";

	private static final String KEY_END_CHECK = "END_CHECK";

	private static final String KEY_ACTION = "ACTION";

	private static final String KEY_CHECK = "CHECK";

	private static final String KEY_CHECK_FAILED = "CHECK_FAILED";

	private static final String KEY_ACTION_FAILED = "ACTION_FAILED";

	private static final String KEY_ERROR = "ERROR";

	private static final String KEY_TEST_FAILED = "TEST_FAILED";

	private static final String MSG_FMT = "[%d][%s] %s";

	private static final FunctionalLogger FLOG = new FunctionalLogger();
	
	private final Stack<String> titleStack = new Stack<String>();

	private String currentTestName = null;
	

	/**
	 * default constructor
	 * private : use getInstance
	 */
	private FunctionalLogger() {
		// do nothing
	}

	public static WebLogger getInstance() {
		return FLOG;
	}

	/**
	 * Marque le début d'un test unitaire
	 * 
	 * @param nameTest
	 * @param descr
	 */
	public void testStarted(final String nameTest, final String descr) {
		if (StringUtils.isBlank(currentTestName)) {
			titleStack.clear();
			currentTestName = nameTest;
			info(KEY_TEST_STARTED, nameTest, descr);
		} else {
			throw new RuntimeException("Test " + currentTestName + " already started !");
		}
	}

	/**
	 * Marque le début d'un test unitaire
	 * 
	 * @param nameTest
	 */
	public void testStarted(final String nameTest) {
		testStarted(nameTest, null);
	}

	/**
	 * Marque la fin d'un test unitaire
	 * 
	 * nameTest is automatically set according to testStarted value
	 */
	public void testFinished() {
		if (currentTestName != null) {	// Seule cette ligne diffère de la version 1.13 (pour éviter le NPE dans "Test mechanism")
			info(KEY_TEST_FINISHED, currentTestName, null);
			currentTestName = null;
		}
	}

	public void handleTestNotFinished() {
		currentTestName = null;
	}

	/**
	 * Marque le début d'une séquence d'action utilisateur.
	 * 
	 * @param nameAction
	 */
	@Override
	public void startAction(final String nameAction, final String descr) {
		info(KEY_START_ACTION, nameAction, descr);
		titleStack.push(nameAction);
	}

	@Override
	public void startAction(final String nameAction) {
		startAction(nameAction, null);
	}

	/**
	 * Marque la fin d'une séquence d'action utilisateur.
	 * 
	 * nameAction is set automatically from startAction
	 */
	@Override
	public void endAction() {
		assert !titleStack.isEmpty();
		final String nameAction = titleStack.pop();
		info(KEY_END_ACTION, nameAction, null);
	}

	/**
	 * Marque le début d'une séquence de check.
	 * 
	 * @param nameCheck
	 */
	@Override
	public void startCheck(final String nameCheck, final String descr) {
		info(KEY_START_CHECK, nameCheck, descr);
		titleStack.push(nameCheck);
	}

	@Override
	public void startCheck(final String nameCheck) {
		startCheck(nameCheck, null);
	}

	/**
	 * Marque la fin d'une séquence de check.
	 * nameAction is automatically set from startCheck value
	 */
	@Override
	public void endCheck() {
		assert !titleStack.isEmpty();
		final String nameCheck = titleStack.pop();
		info(KEY_END_CHECK, nameCheck, null);
	}

	/**
	 * Marque le début d'une action utilisateur
	 * 
	 * @param nameTest
	 * @param descr
	 */
	@Override
	public void action(final String message) {
		info(KEY_ACTION, message);
	}

	/**
	 * Marque le début d'une action utilisateur consistant en un clic sur un bouton
	 * 
	 * @param nameTest
	 * @param descr
	 */
	@Override
	public void actionClickButton(final String buttonName) {
		info(KEY_ACTION, String.format("Clique sur le bouton [%s]", buttonName));
	}

	/**
	 * Marque le début d'une action utilisateur consistant en un clic sur un lien
	 * 
	 * @param nameTest
	 * @param descr
	 */
	@Override
	public void actionClickLink(final String linkName) {
		info(KEY_ACTION, String.format("Clique sur le lien [%s]", linkName));
	}

	@Override
	public void actionFillField(final String field, final String value) {
		info(KEY_ACTION, String.format("Remplit le champ [%s] avec la valeur [%s]", field, value));
	}

	/**
	 * Marque le début d'une vérification
	 * 
	 * @param message
	 */
	@Override
	public void check(final String message) {
		info(KEY_CHECK, message);
	}

	/**
	 * Marque l'échec d'une vérification
	 */
	@Override
	public void checkFailed(final String message) {
		warn(KEY_CHECK_FAILED, message);
	}

	@Override
	public void actionFailed(final String message) {
		warn(KEY_ACTION_FAILED, message);
	}

	public void error(final String message) {
		LOG.error(String.format(MSG_FMT, titleStack.size(), KEY_ERROR, message));
	}

	public void warn(final String message) {
		LOG.warn(String.format(MSG_FMT, titleStack.size(), KEY_TEST_FAILED, message));
	}

	private void info(final String prefix, final String name, final String descr) {
		final StringBuilder message = new StringBuilder(name);
		if (StringUtils.isNotEmpty(descr)) {
			message.append(" {");
			message.append(descr);
			message.append("}");
		}
		info(prefix, message.toString());
	}

	private void info(final String prefix, final String message) {
		LOG.info(String.format(MSG_FMT, titleStack.size(), prefix, message));
	}

	private void warn(final String prefix, final String message) {
		LOG.warn(String.format(MSG_FMT, titleStack.size(), prefix, message));
	}

}
