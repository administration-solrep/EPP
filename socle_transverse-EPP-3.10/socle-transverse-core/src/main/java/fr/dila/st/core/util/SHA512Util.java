package fr.dila.st.core.util;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Utilitaires pour hacher des chaines en SHA512.
 * 
 * @author acleuet
 */
public final class SHA512Util {

	/**
	 * utility class
	 */
	private SHA512Util() {
		// do nothing
	}

	/**
	 * Hache un message en SHA512.
	 * 
	 * @param message
	 *            Message à hacher
	 * @return Hash
	 */
	public static String getSHA512Hash(byte[] message) {
		if (message == null) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(message);
			byte[] sha512hash = md.digest();
			StringBuilder builder = new StringBuilder();
			for (byte b : sha512hash) {
				builder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Hache le contenu d'un fichier en SHA512.
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static String getSHA512Hash(File file) throws IOException {
		return DigestUtils.sha512Hex(FileUtils.readFileToByteArray(file));
	}

	/**
	 * Hache un message en SHA512.
	 * 
	 * @param message
	 *            Message à hacher
	 * @return Hash
	 */
	public static String getSHA512Hash(String message) {
		if (StringUtils.isBlank(message)) {
			return null;
		}
		return getSHA512Hash(message.getBytes());
	}
}
