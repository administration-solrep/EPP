package fr.dila.st.utils.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

/**
 * @author fbarmes
 */
public class AliasSelectorKeyManager implements X509KeyManager {

	private X509KeyManager	sourceKeyManager	= null;
	private String			alias;

	/**
	 * Default constructor
	 * 
	 * @param keyManager
	 *            the wrapped keyManager
	 * @param alias
	 *            the alias to use in the communications
	 */
	public AliasSelectorKeyManager(X509KeyManager keyManager, String alias) {
		this.sourceKeyManager = keyManager;
		this.alias = alias;

	}

	/**
	 * Get all aliases from the key manager. If any matches with the managed alias, then return it. If the alias has not
	 * been found, return null (and let the API to handle it, causing the handshake to fail).
	 * 
	 * @param keyType
	 * @param issuers
	 * @param socket
	 * @return
	 */
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		boolean aliasFound = false;

		for (int i = 0; i < keyType.length && !aliasFound; i++) {
			String[] validAliases = sourceKeyManager.getClientAliases(keyType[i], issuers);
			if (validAliases != null) {
				for (int j = 0; j < validAliases.length && !aliasFound; j++) {
					if (validAliases[j].equals(alias)) {
						aliasFound = true;
					}
				}
			}
		}

		if (aliasFound) {
			return alias;
		} else {
			return null;
		}
	}

	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		return sourceKeyManager.chooseServerAlias(keyType, issuers, socket);
	}

	public X509Certificate[] getCertificateChain(String alias) {
		return sourceKeyManager.getCertificateChain(alias);
	}

	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return sourceKeyManager.getClientAliases(keyType, issuers);
	}

	public PrivateKey getPrivateKey(String alias) {
		return sourceKeyManager.getPrivateKey(alias);
	}

	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return sourceKeyManager.getServerAliases(keyType, issuers);
	}

}
