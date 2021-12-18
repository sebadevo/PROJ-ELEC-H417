package clientapp.controllers.user;

import clientapp.MainClient;
import clientapp.models.User;
import clientapp.models.databases.UserDatabase;
import clientapp.models.databases.exceptions.DatabaseSaveException;
import clientapp.views.user.UserPageViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserPageController implements UserPageViewController.UserPageViewListener {

    private final UserPageListener listener;
    private final Stage stage;
    private final User user;
    private UserPageViewController userPageViewController;

    public static final String LOAD_PRINCIPAL_USER_PAGE_ERROR = "the principal user window had to be displayed";

    public UserPageController(UserPageListener listener, Stage stage, User user) {
        this.stage = stage;
        this.listener = listener;
        this.user = user;
    }

    /**
     * Permet l'affichage du menu principal.
     * @throws IOException erreur d'affichage
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(UserPageViewController.class.getResource("UserPageView.fxml"));
        loader.load();

        userPageViewController = loader.getController();
        userPageViewController.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
        onCloseRequest();
    }

    /**
     * permet de détecter si on ferme la fenêtre et d'en notifier la classe UserController.
     */
    private void onCloseRequest() {
        stage.setOnCloseRequest(e -> {
            try {
                listener.onClose();
            } catch (Exception exception) {
                MainClient.showError("la fenêtre a dû être fermée");
            }
        });
    }

    /**
     * Permet de fermer la page du menu principal.
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Permet de déconnecter un User et de demander à la classe UserController de changer de fenêtre.
     */
    @Override
    public void onLogOutButtonPressed() {
        UserDatabase.getInstance().logOut(user);
        try {
            UserDatabase.getInstance().save();
        } catch (DatabaseSaveException e) {
            userPageViewController.setErrorMessage("Error saving user to database");
        }
        listener.onLogOutAsked();
    }

    /**
     * Demande à la classe UserController d'aller à la page de modification du profil.
     */
    @Override
    public void onModifyUserButtonPressed() {
        listener.onModifyUserAsked();
    }


    public interface UserPageListener {
        void onLogOutAsked();
        void onModifyUserAsked();
        void onClose() throws Exception;
    }
}

