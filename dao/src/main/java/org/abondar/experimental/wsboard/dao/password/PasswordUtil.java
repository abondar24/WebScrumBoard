package org.abondar.experimental.wsboard.dao.password;

import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Utility for password hash creation and updating
 *
 * @author a.bondar
 */
public class PasswordUtil {

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    // These constants may be changed without breaking existing hashes.
    private static final int SALT_BYTE_SIZE = 24;
    private static final int HASH_BYTE_SIZE = 18;
    private static final int PBKDF2_ITERATIONS = 64000;

    // These constants define the encoding and may not be changed.
    private static final int HASH_SECTIONS = 5;
    private static final int HASH_ALGORITHM_INDEX = 0;
    private static final int ITERATION_INDEX = 1;
    private static final int HASH_SIZE_INDEX = 2;
    private static final int SALT_INDEX = 3;
    private static final int PBKDF2_INDEX = 4;

    private PasswordUtil() {
    }

    /**
     * Create password hash
     *
     * @param password - raw password
     * @return password hash
     */
    public static String createHash(String password)
            throws CannotPerformOperationException {
        return createHash(password.toCharArray());
    }

    /**
     * Create password hash
     *
     * @param password - raw password as char array
     * @return password hash
     */
    private static String createHash(char[] password)
            throws CannotPerformOperationException {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
        int hashSize = hash.length;

        // format: algorithm:iterations:hashSize:salt:hash
        return "sha1:" +
                PBKDF2_ITERATIONS +
                ":" + hashSize +
                ":" +
                toBase64(salt) +
                ":" +
                toBase64(hash);
    }

    /**
     * Verify password
     *
     * @param password    - raw password
     * @param correctHash - password hash from db
     * @return true - password matches, false - no match
     * @throws CannotPerformOperationException - hash not supported
     * @throws InvalidHashException            - hash has missing fields
     */
    public static boolean verifyPassword(String password, String correctHash)
            throws CannotPerformOperationException, InvalidHashException {
        return verifyPassword(password.toCharArray(), correctHash);
    }

    /**
     * Verify password
     *
     * @param password    - raw password as char array
     * @param correctHash - password hash from db
     * @return true - password matches, false - no match
     * @throws CannotPerformOperationException - hash not supported
     * @throws InvalidHashException            - hash has missing fields
     */
    private static boolean verifyPassword(char[] password, String correctHash)
            throws CannotPerformOperationException, InvalidHashException {
        // Decode the hash into its parameters
        String[] params = correctHash.split(":");
        if (params.length != HASH_SECTIONS) {
            throw new InvalidHashException(
                    "Fields are missing from the password hash."
            );
        }

        // Currently, Java only supports SHA1.
        if (!params[HASH_ALGORITHM_INDEX].equals("sha1")) {
            throw new CannotPerformOperationException(
                    "Unsupported hash type."
            );
        }

        int iterations;
        try {
            iterations = Integer.parseInt(params[ITERATION_INDEX]);
        } catch (NumberFormatException ex) {
            throw new InvalidHashException(
                    "Could not parse the iteration count as an integer.",
                    ex
            );
        }

        if (iterations < 1) {
            throw new InvalidHashException(
                    "Invalid number of iterations. Must be >= 1."
            );
        }


        byte[] salt;
        try {
            salt = fromBase64(params[SALT_INDEX]);
        } catch (IllegalArgumentException ex) {
            throw new InvalidHashException(
                    "Base64 decoding of salt failed.",
                    ex
            );
        }

        byte[] hash;
        try {
            hash = fromBase64(params[PBKDF2_INDEX]);
        } catch (IllegalArgumentException ex) {
            throw new InvalidHashException(
                    "Base64 decoding of pbkdf2 output failed.",
                    ex
            );
        }


        int storedHashSize;
        try {
            storedHashSize = Integer.parseInt(params[HASH_SIZE_INDEX]);
        } catch (NumberFormatException ex) {
            throw new InvalidHashException(
                    "Could not parse the hash size as an integer.",
                    ex
            );
        }

        if (storedHashSize != hash.length) {
            throw new InvalidHashException(
                    "Hash length doesn't match stored hash length."
            );
        }


        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        return slowEquals(hash, testHash);
    }


    /**
     * Compare the hashes in constant time.
     * The password is correct if both hashes match.
     *
     * @param a - old hash
     * @param b - new hash
     * @return true - hash matches, false - no match
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    /**
     * Compute the hash of the provided password, using the same salt,
     * iteration count, and hash length
     *
     * @param password   - password
     * @param salt       - hash salt
     * @param iterations - number of iterations
     * @param bytes      - hash length
     * @return password hash
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws CannotPerformOperationException {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            throw new CannotPerformOperationException(
                    "Hash algorithm not supported.",
                    ex
            );
        } catch (InvalidKeySpecException ex) {
            throw new CannotPerformOperationException(
                    "Invalid key spec.",
                    ex
            );
        }
    }

    /**
     * Decode from base64
     *
     * @param hex - hex num
     * @return decoded number
     * @throws IllegalArgumentException - num is not hex
     */
    private static byte[] fromBase64(String hex) {
        return DatatypeConverter.parseBase64Binary(hex);
    }

    /**
     * Encode base64
     *
     * @param array - array to encode
     * @return encoded number
     */
    private static String toBase64(byte[] array) {
        return DatatypeConverter.printBase64Binary(array);
    }
}
