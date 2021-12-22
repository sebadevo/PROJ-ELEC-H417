package clientapp.models;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.*;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * For security and efficiency purpose, we do not reinvent the wheel for the security layer of the project.
 * Most of the code in this file comes from internet, with some small adaptations.
 * You can find the sources bellow :
 *
 * ENCRYPTION (AES) : https://stackoverflow.com/questions/1132567/encrypt-password-in-configuration-files
 * HASHING (SHA) : https://www.tutorialspoint.com/java_cryptography/java_cryptography_message_digest.htm
 * MODULAR EXPONENTIATION : https://www.tutorialspoint.com/Modular-Exponentiation-Power-in-Modular-Arithmetic-in-java
 *
 * BIG PRIME NUMBERS : http://www.isthe.com/chongo/tech/math/prime/mersenne.html#M2203
 */


public class Crypto {

    // https://lcn2.github.io/mersenne-english-name/m2203/prime-c-e.html -> For big prime number (BIG_PRIME is a 664-digits)
    static final String BIG_PRIME = "1475979915214180235084898622737381736312066145333169775147771216478570297878078949377407337049389289382748507531496480477281264838760259191814463365330269540496961201113430156902396093989090226259326935025281409614983499388222831448598601834318536230923772641390209490231836446899608210795482963763094236630945410832793769905399982457186322944729636418890623372171723742105636440368218459649632948538696905872650486914434637457507280441823676813517852099348660847172579408422316678097670224011990280170474894487426924742108823536808485072502240519452587542875349976558572670229633962575212637477897785501552646522609988869914013540483809865681250419497686697771007";

    static final String BASE = "3";

    /**
     * Define a pseudo-random (deterministic) g^a (in Diffie-Hellman Key Exchange) using the user's username and password
     * @param a exponent of the user A
     * @return the g^a value of the Diffie-Hellman key Exchange
     */
    public static BigInteger defineGa(BigInteger a){
        return powMod(a);
    }

    /**
     * Change characters to the ASCII value to obtain a string only made of digits
     * @param word is the word to convert in ASCII value, char by char
     * @return the translation of the input in ASCII (number)
     */
    public static String letterTodigit(String word) {
        String result="";
        char[] s = word.toCharArray();
        for (char c : s){
            result+=Integer.toString((int) c);
        }
        if (!(result.length()<BIG_PRIME.length())){
            result = result.substring(0, BIG_PRIME.length() - 3);
        }
        return result;
    }

    /**
     * Hashing the message send for more security/anonymity purpose.
     * @param message is the text to hash (to digest)
     * @return the hashed value of the message
     * @throws NoSuchAlgorithmException error of hashing
     */
    public static String hashing(String message) throws NoSuchAlgorithmException {
        String hashed;

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        //Passing data to the created MessageDigest Object
        md.update(message.getBytes());

        //Compute the message digest
        byte[] digest = md.digest();

        //Converting the byte array in to HexString format
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) hexString.append(Integer.toHexString(0xFF & b));
        hashed = hexString.toString();

        return hashed;
    }

    /**
     * Use MODULAR EXPONENTIATION to quickly compute exposant with a modulo -> Use in Diffie-Hellman
     * With a define base (BASE) and a define modulo (BIG_PRIME)
     * @param exp is the exponant use
     * @return (BASE ^ exp) mod BIG_PRIME
     */
    public static BigInteger powMod(BigInteger exp) { // exposant < BIG_PRIME - 1
        // create a BigInteg
        BigInteger base = new BigInteger(BASE);
        BigInteger mod = new BigInteger(BIG_PRIME);
        return base.modPow(exp, mod);
    }

    /**
     * Use MODULAR EXPONENTIATION to quickly compute exposant with a modulo -> Use in Diffie-Hellman
     * With a define modulo (BIG_PRIME)
     * @param a is used as the exponent
     * @param gb is the base use
     * @return (gb ^ a) mod BIG_PRIME
     */
    public static BigInteger getPrivateKey(BigInteger a, BigInteger gb) { // exposant < BIG_PRIME - 1
        BigInteger mod = new BigInteger(BIG_PRIME);
        return gb.modPow(a, mod);
    }


    /**
     * Use the key obtain with Diffie-Hellman Key Exchange to generate a SecretKeySpec
     * @param diffieHellman secret key string which will generate the secret key
     * @return a Secret key use for encryption/decryption
     * @throws NoSuchAlgorithmException Exception
     * @throws InvalidKeySpecException Exception
     * @return the secret key.
     */
    public static SecretKeySpec generateKey(String diffieHellman) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = diffieHellman;
        if (password == null) {
            throw new IllegalArgumentException("Run with -Dpassword=<password>");
        }
        byte[] salt = new String("TSAD").getBytes(); // TODO for Assistants : guess why. ^^
        // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
        int iterationCount = 40000;
        // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
        int keyLength = 128;
        SecretKeySpec key = createSecretKey(password.toCharArray(), salt, iterationCount, keyLength);
        return key;
    }

    /**
     * Generate a SecretKeySpec from a password
     * @param password use the define the key
     * @param salt use to define the key
     * @param iterationCount number of iteration (increase the security)
     * @param keyLength define the length of the key
     * @return a SecretKeySpec object
     * @throws NoSuchAlgorithmException Exception
     * @throws InvalidKeySpecException Exception
     */
    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    /**
     * Encrypt the message with a key (AES Encryption)
     * @param property message to encrypt
     * @param key key use to encrypt
     * @return ciphertext
     * @throws GeneralSecurityException Exception
     * @throws UnsupportedEncodingException Exception
     */
    public static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // CTR a la place CBC
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decrypt a message with a key
     * @param string cipher to decrypt
     * @param key key use to decrypt the ciphertext
     * @return plaintext
     * @throws GeneralSecurityException Exception
     * @throws IOException Exception
     */
    public static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }

}
