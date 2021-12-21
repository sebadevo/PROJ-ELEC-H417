package clientapp.models;

import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto {



    /**
     * We hash the messages send for more security .
     * @param message is the text send from a user to another.
     * @return String hashed
     * @throws NoSuchAlgorithmException error of hashing
     */
    public static String hashing(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        //Passing data to the created MessageDigest Object
        md.update(message.getBytes());

        //Compute the message digest
        byte[] digest = md.digest();

        //Converting the byte array in to HexString format
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) hexString.append(Integer.toHexString(0xFF & b));
        return hexString.toString();
    }

    public static String Encryption(String message){
        return null;
    }

    public static String Decryption(String encryptedMessage){
        return null;
    }

    /*public static String() {
        // create 3 BigInteger objects
        BigInteger base, mod, result;

        // create a BigInteger exponent
        // https://lcn2.github.io/mersenne-english-name/m2203/prime-c-e.html
        BigInteger exponent = new BigInteger("1475979915214180235084898622737381736312066145333169775147771216478570297878078949377407337049389289382748507531496480477281264838760259191814463365330269540496961201113430156902396093989090226259326935025281409614983499388222831448598601834318536230923772641390209490231836446899608210795482963763094236630945410832793769905399982457186322944729636418890623372171723742105636440368218459649632948538696905872650486914434637457507280441823676813517852099348660847172579408422316678097670224011990280170474894487426924742108823536808485072502240519452587542875349976558572670229633962575212637477897785501552646522609988869914013540483809865681250419497686697771007");
        base = new BigInteger("3");
        mod = new BigInteger("1024");

        // perform modPow operation on bi1 using bi2 and exp
        result = base.modPow(exponent, mod);
        String str = base + "^" + exponent+ " mod " + mod + " is " + result;

        // print bi3 value
        System.out.println( str );

    }*/

}
