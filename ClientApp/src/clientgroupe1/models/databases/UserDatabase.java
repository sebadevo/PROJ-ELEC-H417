package clientgroupe1.models.databases;

import clientgroupe1.models.User;

import java.util.ArrayList;

public class UserDatabase extends Database<User> {
    private static UserDatabase instance;

    public static UserDatabase getInstance(){
        if (instance == null){
            instance = new UserDatabase();
        }
        return instance;
    }

    private UserDatabase(){
        path = "database/UserDB.serial";
        data = new ArrayList<>();
    }

    /**
     * Modifie les données d'un utilisateur de la liste 'userData'
     * @param user user actuel
     * @param temporaryUser user avec les valeurs modifiées
     */
    public void replace(User user, User temporaryUser) {
        for (User databaseUser : data) {
            if (databaseUser.getUsername().equals(user.getUsername())) {
                data.set(data.indexOf(databaseUser), temporaryUser);
                break;
            }
        }
    }

    /**
     * Vérifie si les modifications sont éligibles à une modification définitive. Renvoie true si éligible à une
     * modification définitive.
     * @param user user actuel
     * @param temporaryUser user avec les valeurs modifiées
     * @return boolean
     */
    public boolean checkModification(User user, User temporaryUser){
        return ((checkExistingEmail(temporaryUser.getEmailAddress()) &&
                temporaryUser.getEmailAddress().equals(user.getEmailAddress())
                || !checkExistingEmail(temporaryUser.getEmailAddress())));
    }

    /**
     * Vérifie si un utilisateur est présent dans la liste 'userData'
     * @param user un user temporaire pour verifier si il n'en existe pas déjà un avec les mêmes adresse email ou username
     * @return boolean
     */
    public boolean checkExistingUser(User user){
        for (User databaseUser : data){
            if (databaseUser.getUsername().equals(user.getUsername()) ||
                    databaseUser.getEmailAddress().equals(user.getEmailAddress())){
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si un utilisateur de la liste 'dataUser' possède déjà l'adresse e-mail passée en argument.
     * @param email string e-mail
     * @return boolean
     */
    public boolean checkExistingEmail(String email){
        for (User databaseUser : data){
            if (databaseUser.getEmailAddress().equals(email)){
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie les infos entrées pour se connecter
     * @param username string username écrit par l'utilisateur lorsqu'il veut se connecter
     * @param password string password écrit par l'utilisateur lorsqu'il veut se connecter
     * @return boolean
     */
    public boolean logIn(String username, String password){
        for (User databaseUser : data){
            if (databaseUser.getUsername().equals(username) && databaseUser.getPassword().equals(password)
                    && !databaseUser.isConnected()){
                databaseUser.setConnected(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Notifie un utilisateur comme étant déconnecté
     * @param user utilisateur actuellement connecté
     */
    public void logOut(User user){
        for (User databaseUser : data){
            if (databaseUser.getUsername().equals(user.getUsername())){
                databaseUser.setConnected(false);
            }
        }
    }

    /**
     * renvoie le user qui a pour username le string passé en argument
     * @param username nom de l'utilisateur
     * @return null si il n'existe pas, le user sinon
     */
    public User getUser(String username){
        for (User user : data){
            if (user.getUsername().equals(username)){
                return user;
            }
        }
        return null;
    }
}
