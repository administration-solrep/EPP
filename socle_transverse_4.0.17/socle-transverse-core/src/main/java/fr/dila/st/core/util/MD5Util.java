package fr.dila.st.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilitaires pour hacher des chaines en MD5.
 *
 * @author jtremeaux
 */
public final class MD5Util {

    /**
     * utility class
     */
    private MD5Util() {
        // do nothing
    }

    /**
     * Hache un message en MD5.
     *
     * @param message
     *            Message à hacher
     * @return Hash
     */
    public static String getMD5Hash(byte[] message) {
        if (message == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message);
            byte[] md5hash = md.digest();
            StringBuilder builder = new StringBuilder();
            for (byte b : md5hash) {
                builder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hache un message en MD5.
     *
     * @param message
     *            Message à hacher
     * @return Hash
     */
    public static String getMD5Hash(String message) {
        if (StringUtils.isBlank(message)) {
            return null;
        }
        return getMD5Hash(message.getBytes());
    }
}
