package fr.dila.st.core.util;

import static java.util.Objects.requireNonNull;
import static org.nuxeo.runtime.api.Framework.getProperty;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.nuxeo.ecm.core.api.NuxeoException;

public final class CryptoUtils {
    private static final STLogger LOGGER = STLogFactory.getLog(CryptoUtils.class);

    private static final Integer ITERATION_COUNT = 65536;
    private static final Integer KEY_LENGTH = 256;

    private static final int TAG_LENGTH_BIT = 128; // must be one of {128, 120, 112, 104, 96}
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";

    private CryptoUtils() {}

    public static String decodeValue(String value) {
        if (value != null) {
            try {
                byte[] decode = Base64.decodeBase64(value.getBytes(UTF_8));

                ByteBuffer bb = ByteBuffer.wrap(decode);

                byte[] iv = new byte[IV_LENGTH_BYTE];
                bb.get(iv);

                byte[] salt = new byte[SALT_LENGTH_BYTE];
                bb.get(salt);

                byte[] cipherText = new byte[bb.remaining()];
                bb.get(cipherText);

                SecretKey aesKeyFromPassword = getAESKeyFromPassword(salt);

                Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
                cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

                byte[] original = cipher.doFinal(cipherText);

                return new String(original, UTF_8);
            } catch (Exception ex) {
                LOGGER.error(STLogEnumImpl.FAIL_GET_META_DONNEE_TEC, ex);
            }
        }
        return null;
    }

    private static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static String encodeValue(String value) {
        Cipher cipher;
        byte[] cipherText;
        try {
            byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);

            // 16 bytes salt
            byte[] salt = CryptoUtils.getRandomNonce(SALT_LENGTH_BYTE);

            SecretKey aesKeyFromPassword = CryptoUtils.getAESKeyFromPassword(salt);

            cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            cipherText = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[] cipherTextWithIvSalt = ByteBuffer
                .allocate(iv.length + salt.length + cipherText.length)
                .put(iv)
                .put(salt)
                .put(cipherText)
                .array();

            return Base64.encodeBase64String(cipherTextWithIvSalt);
        } catch (Exception e) {
            LOGGER.error(STLogEnumImpl.FAIL_SAVE_PWD_TEC, e);
            throw new NuxeoException("Erreur lors du cryptage du mot de passe");
        }
    }

    // AES 256 bits secret key derived from a password
    private static SecretKey getAESKeyFromPassword(byte[] salt) {
        try {
            String passphrase = requireNonNull(getProperty("solon.ws.passphrase"));

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (Exception e) {
            LOGGER.error(STLogEnumImpl.FAIL_GENERATE_KEY_TEC, e);
        }

        return null;
    }
}
