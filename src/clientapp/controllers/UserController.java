package clientapp.controllers;

import clientapp.MainClient;
import clientapp.views.user.UserPageViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserController implements UserPageViewController.UserPageViewListener {

    private final UserPageListener listener;
    private final Stage stage;
    private UserPageViewController userPageViewController;

    public static final String LOAD_PRINCIPAL_USER_PAGE_ERROR = "the principal user window had to be displayed";

    public UserController(UserPageListener listener, Stage stage) {
        this.stage = stage;
        this.listener = listener;
    }

    /**
     * Permet l'affichage de UserPageView.
     * @throws IOException erreur d'affichage
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(UserPageViewController.class.getResource("UserPageView.fxml"));
        loader.load();

        userPageViewController = loader.getController();
        userPageViewController.setListener(this);
        Parent page = loader.getRoot();
        stage.setScene(new Scene(page));
        stage.show();
        onCloseRequest();
    }

    /**
     * Permet de détecter si on ferme la fenêtre et d'en notifier la classe UserController.
     */
    private void onCloseRequest() {
        stage.setOnCloseRequest(e -> {
            try {
                listener.logOut(); // avant : listener.onClose();
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
        // TODO send deconnection to server -> server must handle the thing
        listener.logOut();
    }

    @Override
    public void onSendButtonPressed(String message){
        listener.sendMessage(message);
    }

    public void receiveText(String message) {
        userPageViewController.addReadingArea(message);
    }

    public interface UserPageListener {
        void sendMessage(String message);
        void logOut();
    }
}

