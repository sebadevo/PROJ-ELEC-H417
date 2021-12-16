package clientgroupe1.controllers;

import clientgroupe1.controllers.user.ProfileModificationController;
import clientgroupe1.controllers.user.UserPageController;;
import clientgroupe1.models.User;
import javafx.stage.Stage;

import java.io.IOException;

import static clientgroupe1.MainClient.showError;

public class UserController implements ProfileModificationController.ProfileModificationListener, UserPageController.UserPageListener {

    private final UserListener listener;
    private final Stage stage;
    private User user;
    private ProfileModificationController profileModificationController;
    private UserPageController userPageController;


    public UserController(UserListener listener, Stage stage, User user) {
        this.listener = listener;
        this.stage = stage;
        this.user = user;
    }

    /**
     * Demande à la classe UserPageController d'afficher le menu principal.
     */
    public void show() {
        userPageController = new UserPageController(this, stage, user);
        try {
            userPageController.show();
        } catch (Exception e) {
            showError(UserPageController.LOAD_PRINCIPAL_USER_PAGE_ERROR);
        }
    }

    /**
     * Demande au groupe1.Main d'afficher la page de connexion.
     */
    @Override
    public void onLogOutAsked() {
        listener.logOut();
    }


    /**
     * Demande à la classe ProfileModificationController d'afficher la page de modification du profil.
     */
    @Override
    public void onModifyUserAsked() {
        profileModificationController = new ProfileModificationController(this, stage, user);
        try{
            profileModificationController.show();
        } catch (IOException e) {
            showError(ProfileModificationController.LOAD_PROFILE_MODIFICATION_PAGE_ERROR);

        }
    }

    /**
     * Demande à la classe UserPageController d'afficher le menu principal.
     * @param user utilisateur après avoir modifié ses informations
     */
    @Override
    public void onConfirmModificationsAsked(User user) {
        this.user = user;
        try {
            userPageController.show();
        } catch (IOException e) {
            showError(UserPageController.LOAD_PRINCIPAL_USER_PAGE_ERROR);
        }
    }

    /**
     * Demande à la classe UserPageController d'afficher le menu principal.
     */
    @Override
    public void onBackToUserAsked() {
        try {
            userPageController.show();
        } catch (IOException e) {
            showError(UserPageController.LOAD_PRINCIPAL_USER_PAGE_ERROR);
        }
    }

    /**
     * Demande au groupe1.Main de fermer l'application.
     * @throws Exception exception lié à la fermeture d'une scène
     */
    @Override
    public void onClose() throws Exception {
        listener.onClose();
    }

    public interface UserListener {
        void logOut();
        void onClose() throws Exception;
    }
}
