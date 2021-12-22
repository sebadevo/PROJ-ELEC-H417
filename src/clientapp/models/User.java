package clientapp.models;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

import static clientapp.models.Crypto.hashing;
import static clientapp.models.Crypto.letterTodigit;

public class User implements Serializable {
    private static final long serialVersionUID = -3233133803033315L;
    private final String firstname;
    private final String lastname;
    private String username;
    private final String emailAddress;
    private String password;
    private boolean isConnected = false;
    private ArrayList<FriendsKey> friends;
    private BigInteger ga;
    private BigInteger a;

    /**
     * Crée un utilisateur
     * @param firstname String prénom
     * @param lastname String nom
     * @param username String nom d'utilisateur
     * @param emailAddress string adresse mail
     * @param password string mot de passe
     */
    public User(String firstname, String lastname, String username, String emailAddress, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.emailAddress = emailAddress;
        this.username = username;
        this.password = password;
        friends = new ArrayList<>();
        try {
            setA();
            setGa();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void setA() throws NoSuchAlgorithmException {
        a = new BigInteger(letterTodigit(hashing(username+password)));
    }

    public User(User user){
        this.firstname = user.firstname;
        this.lastname = user.lastname;
        this.emailAddress = user.emailAddress;
        this.username = user.username;
        this.password = user.password;
        this.isConnected = user.isConnected;
        this.friends = user.friends;
        this.ga = user.ga;
        this.a = user.a;
    }

    /**
     * Vérifie que les champs ne soient pas vides et la typographie d'un mail
     * @param firstname String prénom
     * @param lastname String nom
     * @param username String nom d'utilisateur
     * @param emailAddress string adresse mail
     * @param password string mot de passe
     * @return boolean
     */
    public static boolean checkSyntax(String firstname, String lastname, String username, String emailAddress, String password){
        if ((firstname == null || lastname == null || username == null || password == null)){
            return false;
        } else if (Objects.equals(firstname, "") || Objects.equals(lastname, "") || Objects.equals(username, "")
                || Objects.equals(password, "")){
            return false;
        } else {
            return emailAddress.contains("@");
        }
    }

    /**
     * Check if a username is already in our list of friends.
     * @param username the username we want to check.
     * @return boolean
     */
    public boolean checkFriends(String username){
        for (FriendsKey friend: friends){
            if (friend.getFriendName().equals(username)){
                return true;
            }
        }
        return false;
    }

    /**
     * Will get the Encryption and Decryption key of a given username in argument
     * @param friendName the name of the user we want ot get the key.
     * @return the key used to encrypt/decrypt the messages from the user
     */
    public SecretKeySpec getFriendsKey(String friendName){
        for (FriendsKey friend: friends){
            if (friend.getFriendName().equals(friendName)){
                return friend.getKey();
            }
        }
        return null;
    }

    /**
     * adds a friend to the list of friends
     * @param friend new friend
     */
    public void addFriends(FriendsKey friend){
        friends.add(friend);
    }

    public ArrayList<FriendsKey> getFriends(){
        return friends;
    }

    // Getters and setters

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public void setConnected(boolean bool){
        this.isConnected = bool;
    }

    public BigInteger getGa(){
        return ga;
    }

    public BigInteger getA(){
        return a;
    }

    private void setGa() throws NoSuchAlgorithmException {
        ga = Crypto.defineGa(a);
    }

    /**
     * methode to display the different fields of a user.
     * @return string
     */
    public String toString(){
        return firstname + " " + lastname + " " + username + " " + emailAddress + " " + password + " " + isConnected;
    }

    /**
     * removes a friend of the list of friends.
     * @param username friend to remove
     */
    public void removeFriend(String username) {
        for (FriendsKey friend: friends){
            if (friend.getFriendName().equals(username)){
                friends.remove(friend);
                return;
            }
        }

    }
}
