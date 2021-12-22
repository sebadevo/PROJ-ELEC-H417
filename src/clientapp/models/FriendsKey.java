package clientapp.models;

import javax.crypto.spec.SecretKeySpec;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static clientapp.models.Crypto.generateKey;
import static clientapp.models.Crypto.getPrivateKey;


public class FriendsKey {
    private SecretKeySpec key = null; // SecretKeySpec
    private final String friendName;

    public FriendsKey(String username){
        this.friendName = username;
    }

    /**
     * Sets the Encryption and Decryption key associated to the friendName.
     * @param a exponent a
     * @param gb base g (in our case 3) exponent b
     */
    public void setKey(BigInteger a, BigInteger gb){
        try {
            key = generateKey(getPrivateKey(a, gb).toString());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public SecretKeySpec getKey() {
        return key;
    }

    public String getFriendName() {
        return friendName;
    }

}
