package clientgroupe1.controllers.user;

import clientgroupe1.models.User;
import clientgroupe1.models.databases.UserDatabase;
import clientgroupe1.views.user.ProfileModificationViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static clientgroupe1.models.User.checkSyntax;

public class ProfileModificationController implements ProfileModificationViewController.ProfileModificationViewListener {

    private final ProfileModificationListener listener;
    private final Stage stage;
    private User user;
    private ProfileModificationViewController profileModificationViewController;

    public static final String LOAD_PROFILE_MODIFICATION_PAGE_ERROR = "the profile modification window had to be displayed";

    public ProfileModificationController(ProfileModificationListener listener, Stage stage, User user) {
        this.listener = listener;
        this.stage = stage;
        this.user = user;
    }

    /**
     * Permet l'affichage de la vue modification de projet
     * @throws IOException exception lié à l'affichage d'une nouvelle scène
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(ProfileModificationViewController.class.getResource("ProfileModificationView.fxml"));
        loader.load();
        profileModificationViewController = loader.getController();
        profileModificationViewController.setFields(user.getFirstname(), user.getLastname(), user.getUsername(), user.getEmailAddress());
        profileModificationViewController.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Permet de fermer la fenêtre de la modification de projet
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Permet d'appeler la classe UserController qui affichera la page d'acceuil.
     */
    @Override
    public void onBackButtonPressed() {
        listener.onBackToUserAsked();
    }

    /**
     * permet la modification d'un user, requiert le mot de passe initial du user pour fonctionner
     * @param firstname string
     * @param lastname string
     * @param username string
     * @param email string
     * @param password string
     * @param oldPassword string
     */
    @Override
    public void onConfirmProfileModificationButtonPressed(String firstname, String lastname, String username, String email,
                                                          String password, String oldPassword) {
        if (checkSyntax(firstname, lastname, username, email, password) && oldPassword.equals(user.getPassword())){
            User temporaryUser = new User(firstname, lastname, username, email, password);
            temporaryUser.setConnected(true);
            if (UserDatabase.getInstance().checkModification(user, temporaryUser)) {
                UserDatabase.getInstance().replace(user, temporaryUser);
                user = new User(temporaryUser);
                listener.onConfirmModificationsAsked(user);
            } else {
                profileModificationViewController.setErrorMessage("Email already taken");
            }
        } else if (!oldPassword.equals(user.getPassword())) {
            profileModificationViewController.setErrorMessage("Old password is not correct");
        } else {
            profileModificationViewController.setErrorMessage("Empty fields");
        }
    }

    public interface ProfileModificationListener {
        void onConfirmModificationsAsked(User user);
        void onBackToUserAsked();
    }
}
