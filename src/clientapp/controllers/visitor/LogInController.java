package clientapp.controllers.visitor;

import clientapp.views.visitor.LogInViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController implements LogInViewController.LogInViewListener {

    private final LogInListener listener;
    private final Stage stage;
    private LogInViewController logInViewController;

    public static final String LOAD_LOGIN_PAGE_ERROR = "the login window had to be displayed";

    public LogInController(LogInListener listener, Stage stage){
        this.stage = stage;
        this.listener = listener;
    }

    /**
     * Permet l'affichage de la page de connexion.
     * @throws IOException erreur d'affichage
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(LogInViewController.class.getResource("LogInView.fxml"));
        loader.load();
        logInViewController = loader.getController();
        logInViewController.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.setTitle("Meta");
        stage.show();
    }

    /**
     * Permet de fermer la page de connexion.
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Permet de connecter un user à la l'application et demande de changer de page au VisitorController
     * @param username string
     * @param password string
     */
    @Override
    public void onLogInButton(String username, String password) {
        listener.onLogInAsked(username, password);
        // TODO
        /*
            try {
                Connection
            } catch () {
                logInViewController.setErrorMessage("Wrong username or password");
            }
         */
    }

    /**
     * Demande à la classe VisitorCotroller d'afficher la page de register.
     */
    @Override
    public void onRegisterLink() {
        listener.onRegisterLinkAsked();
    }

    public interface LogInListener {
        void onLogInAsked(String username, String password);
        void onRegisterLinkAsked();
    }
}
