package serverapp.models;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private static final long serialVersionUID = -3233133803033315L;
    private final String firstname;
    private final String lastname;
    private String username;
    private final String emailAddress;
    private String password;
    private boolean isConnected = false;

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
    }

    public User(User user){
        this.firstname = user.firstname;
        this.lastname = user.lastname;
        this.emailAddress = user.emailAddress;
        this.username = user.username;
        this.password = user.password;
        this.isConnected = user.isConnected;
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

    /**
     * affiche le résultat entré dans les différents champs
     * @return string
     */
    public String toString(){
        return firstname + " " + lastname + " " + username + " " + emailAddress + " " + password + " " + isConnected;
    }
}
