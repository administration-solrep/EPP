package fr.dila.st.api.exception;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Exception de Nuxeo qui permet d'afficher un message d'erreur à partir d'une clé dans messages.properties.
 * 
 * @author jtremeaux
 */
public class LocalizedClientException extends ClientException {

	/**
	 * Serial Version UID
	 */
	private static final long	serialVersionUID	= -6970014920070503914L;
	/**
	 * Paramètres du message.
	 */
	private Object[]			params;

	/**
	 * Constructeur de LocalizedClientException.
	 * 
	 * @param messageKey
	 *            Clé du message d'erreur
	 */
	public LocalizedClientException(String messageKey) {
		super(messageKey);
	}

	/**
	 * Constructeur de LocalizedClientException.
	 * 
	 * @param messageKey
	 *            Clé du message d'erreur
	 * @param params
	 *            Paramètres du message
	 */
	public LocalizedClientException(String messageKey, Object... params) {
		super(messageKey);

		this.params = params;
	}

	/**
	 * Constructeur de LocalizedClientException.
	 * 
	 * @param messageKey
	 *            Clé du message d'erreur
	 * @param cause
	 *            Exception parente
	 */
	public LocalizedClientException(String messageKey, ClientException cause) {
		super(messageKey, cause);
	}

	/**
	 * Constructeur de LocalizedClientException.
	 * 
	 * @param messageKey
	 *            Clé du message d'erreur
	 * @param cause
	 *            Exception parente
	 * @param params
	 *            Paramètres du message
	 */
	public LocalizedClientException(String messageKey, ClientException cause, Object... params) {
		super(messageKey, cause);

		this.params = params;
	}

	/**
	 * Getter de params.
	 * 
	 * @return params
	 */
	public Object[] getParams() {
		return params.clone();
	}
}
