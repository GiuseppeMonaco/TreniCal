package it.trenical.server.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtils {

    private static final Logger logger = LoggerFactory.getLogger(PasswordUtils.class);

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 600_000;
    private static final int KEY_LENGTH = 256;

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Calcola l'hash PBKDF2 della password e ritorna una stringa nel formato salt:hash.
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Codifica salt e hash in Base64 e concatena con ':'
            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);
            return saltB64 + ":" + hashB64;
        } catch (Exception e) {
            logger.error("Error hashing password: {}", e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    /**
     * Verifica che la password in chiaro corrisponda all'hash salvato (salt:hash).
     */
    public static boolean verifyPassword(String password, String stored) {
        try {
            String[] parts = stored.split(":", 2);
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            if (storedHash.length != testHash.length) return false;
            int diff = 0;
            for (int i = 0; i < storedHash.length; i++) {
                diff |= storedHash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (Exception e) {
            logger.error("Error checking password: {}", e.getMessage());
            return false;
        }
    }
}
