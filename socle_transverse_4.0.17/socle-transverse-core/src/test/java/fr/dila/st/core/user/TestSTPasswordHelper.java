package fr.dila.st.core.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.nuxeo.ecm.directory.PasswordHelper;

public class TestSTPasswordHelper {
    private static final String PASSWORD = "Password123";
    private static final String HSSHA = "{SSHA}";

    @Test
    public void testSaltGeneratedAreDifferent() {
        Set<byte[]> setSalt = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            byte[] salt = STPasswordHelper.generateSalt();

            assertNotNull(salt);
            assertTrue(salt.length > 0);
            setSalt.add(salt);
        }

        assertEquals(100, setSalt.size());
    }

    @Test
    public void testPasswordGeneration() {
        byte[] salt = STPasswordHelper.generateSalt();

        String hashedPassword = STPasswordHelper.hashPassword(PASSWORD, salt);
        assertTrue(STPasswordHelper.isHashed(hashedPassword));
        String decodedValue = new String(Base64.decodeBase64(hashedPassword.substring(HSSHA.length())));
        assertEquals(128, decodedValue.length());
    }

    @Test
    public void testPasswordVerificationMechanism() {
        byte[] salt = STPasswordHelper.generateSalt();

        String hashedPassword = STPasswordHelper.hashPassword(PASSWORD, salt);

        assertTrue(STPasswordHelper.verifyPassword(PASSWORD, salt, hashedPassword));
        assertFalse(STPasswordHelper.verifyPassword(PASSWORD.toLowerCase(), salt, hashedPassword));
    }

    @Test
    public void testSamePasswordAreHashedDifferently() {
        byte[] salt1 = STPasswordHelper.generateSalt();
        byte[] salt2 = STPasswordHelper.generateSalt();
        String hashedPWD1 = STPasswordHelper.hashPassword(PASSWORD, salt1);
        String hashedPWD2 = STPasswordHelper.hashPassword(PASSWORD, salt2);

        assertFalse(hashedPWD1.equals(hashedPWD2));
    }

    @Test
    public void testHashedPasswordDetected() {
        byte[] salt = STPasswordHelper.generateSalt();

        String hashedPassword = STPasswordHelper.hashPassword(PASSWORD, salt);

        assertFalse(STPasswordHelper.isHashed(null));
        assertFalse(STPasswordHelper.isHashed(""));
        assertFalse(STPasswordHelper.isHashed(PASSWORD));
        assertTrue(STPasswordHelper.isHashed(hashedPassword));
        assertTrue(PasswordHelper.isHashed(hashedPassword));
    }
}
