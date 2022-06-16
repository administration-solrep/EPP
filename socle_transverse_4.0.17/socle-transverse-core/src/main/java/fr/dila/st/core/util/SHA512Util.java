package fr.dila.st.core.util;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

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
     * @param message Message à hacher
     * @return Hash
     */
    public static String getSHA512Hash(byte[] message) {
        return getSHA512Hash(message, 0, message.length);
    }
    
    public static String getSHA512Hash(byte[] message, int offset, int length) {
        return getSHA512Hash(message, offset, length, null, null);
    }

    public static String getSHA512Hash(byte[] message, byte[] salt, byte[] pepper) {
        return getSHA512Hash(message, 0, message.length, salt, pepper);
    }
    
    public static String getSHA512Hash(byte[] message, int offset, int length, byte[] salt, byte[] pepper) {
        if (message == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(message, offset, length);
            if (salt != null && salt.length > 0) {
                md.update(salt);

                if (pepper != null && pepper.length > 0) {
                    md.update(pepper);
                }
            }
            byte[] sha512hash = md.digest();
            StringBuilder builder = new StringBuilder();
            for (byte b : sha512hash) {
                builder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new NuxeoException(e);
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
     * @param message Message à hacher
     * @return Hash
     */
    public static String getSHA512Hash(String message) {
        if (StringUtils.isBlank(message)) {
            return null;
        }
        return getSHA512Hash(message.getBytes());
    }
}
