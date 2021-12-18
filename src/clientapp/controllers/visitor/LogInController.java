package clientapp.controllers.visitor;

import clientapp.models.User;
import clientapp.views.visitor.LogInViewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LogInController implements LogInViewController.LogInViewListener {

    private static final String DELIMITER = "-";
    private final LogInListener listener;
    private final Stage stage;
    private LogInViewController logInViewController;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public static final String LOAD_LOGIN_PAGE_ERROR = "the login window had to be displayed";

    public LogInController(LogInListener listener, Stage stage, Socket socket, PrintWriter printWriter, BufferedReader bufferedReader){
        this.stage = stage;
        this.listener = listener;
        this.socket = socket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
    }

    /**
     * Permet l'affichage de la page de connexion.
     * @throws IOException erreur d'affichage
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(LogInViewController.class.getResource("LogInView.fxml"));

        System.out.println(LogInViewController.class.getResource(""));
        System.out.println(LogInViewController.class.getResource("../../views/visitor/LogInView.fxml"));
        System.out.println(LogInViewController.class.getResource("visitor/LogInView.fxml"));
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
        printWriter.println("0"+DELIMITER+username+DELIMITER+password);
        try {
            String answer = bufferedReader.readLine();
            System.out.println("voici ce qui est en string " + answer);
            if (answer.equals("true")){
                String Id = bufferedReader.readLine();
                if (!Id.isEmpty()) {
                    String[] userInfo = Id.split(DELIMITER);
                    String firstname =userInfo[0];
                    String lastname = userInfo[1];
                    String email = userInfo[2];
                    User user = new User(firstname,lastname, username, email, password);
                    listener.onLogInAsked(user);
                }
                else{
                    //TODO gerer le faite que le packet à pris trop de temps pour être envoyé.
                }
            }
            else {
                logInViewController.setErrorMessage(answer);
            }
        } catch (IOException ignore) {}
    }

    /**
     * Demande à la classe VisitorCotroller d'afficher la page de register.
     */
    @Override
    public void onRegisterLink() {
        listener.onRegisterLinkAsked();
    }

    public interface LogInListener {
        void onLogInAsked(User user);
        void onRegisterLinkAsked();
    }
}
