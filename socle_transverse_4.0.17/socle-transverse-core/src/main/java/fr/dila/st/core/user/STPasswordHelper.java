package fr.dila.st.core.user;

import com.google.common.base.Strings;
import fr.dila.st.core.util.SHA512Util;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

public class STPasswordHelper {
    private static final String HSSHA = "{SSHA}";

    private static final int SALT_LEN = 16;

    private static final String pepper = "kldfsnsdlfezsr$ŝevfmv:opzezpaék)ài,dsk";

    private static final Random random = new SecureRandom();

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LEN];
        synchronized (random) {
            random.nextBytes(salt);
        }

        salt = Base64.encodeBase64(salt);

        return salt;
    }

    /**
     * Stocke le password encrypté en SHA512 avec salage et poivrage
     * @param password the password to store
     * @param salt the salt
     * @return return encoded password
     */
    public static String hashPassword(String password, byte[] salt) {
        String hash = SHA512Util.getSHA512Hash(
            password.getBytes(Charsets.UTF_8),
            Base64.decodeBase64(salt),
            pepper.getBytes(Charsets.UTF_8)
        );
        return HSSHA + Base64.encodeBase64String(hash.getBytes());
    }

    public static boolean isHashed(String password) {
        if (Strings.isNullOrEmpty(password) || !password.startsWith(HSSHA)) {
            return false;
        }

        return true;
    }

    /**
     * Vérifie que le password saisie est valide
     * @param password password to check
     * @param salt the salt
     * @param hashedPassword encrypted password to check
     * @return true if same passwords
     */
    public static boolean verifyPassword(String password, byte[] salt, String hashedPassword) {
        // Si le mdp stocké est invalide ou vide ou si celui saisi est vide on considère qu'il y a un souci
        if (Strings.isNullOrEmpty(password)) {
            return false;
        }

        if (isHashed(hashedPassword)) {
            String passwordInput = STPasswordHelper.hashPassword(password, salt);
            return passwordInput.equals(hashedPassword);
        } else {
            return password.equals(hashedPassword);
        }
    }
}
