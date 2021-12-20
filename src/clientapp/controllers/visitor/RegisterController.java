package clientapp.controllers.visitor;

import clientapp.models.User;
import clientapp.views.visitor.RegisterViewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RegisterController implements RegisterViewController.RegisterViewListener {

    private static final String DELIMITER = "-";
    private final RegisterListener listener;
    private final Stage stage;
    private RegisterViewController registerViewController;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public static final String LOAD_REGISTER_PAGE_ERROR = "the register window had to be displayed";

    public RegisterController(RegisterListener listener, Stage stage, Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.stage = stage;
        this.listener = listener;
        this.socket = socket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
    }

    /**
     * Permet d'afficher la page register
     * @throws IOException erreur d'affichage
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(RegisterViewController.class.getResource("RegisterView.fxml"));
        loader.load();
        registerViewController = loader.getController();
        registerViewController.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Permet de fermer la page de register
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Crée un User si les champs entrés sont valides et de demander au VisitorController d'afficher la page de
     * connexion.
     * @param firstname string
     * @param lastname string
     * @param username string
     * @param email string
     * @param password string
     */
    @Override
    public void onRegisterButton(String firstname, String lastname, String username, String email, String password) {
        if (!firstname.isEmpty() && !lastname.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            printWriter.println("1" + DELIMITER + firstname + DELIMITER + lastname + DELIMITER + username + DELIMITER + email + DELIMITER + password);
            try {
                String answer = bufferedReader.readLine();
                System.out.println("voici ce qui est en string " + answer);
                if (answer.equals("true")) {
                    User user = new User(firstname, lastname, username, email, password);
                    listener.onRegisterAsked(user);
                } else {
                    registerViewController.setErrorMessage(answer);
                }
            } catch (IOException ignore) {}
        }else{
            registerViewController.setErrorMessage("One or more fields are empty");
        }
    }

    /**
     * Demande au VisitorController d'afficher la page de connexion.
     */
    @Override
    public void onBackButton() {
        listener.onBackToLogInAsked();
    }

    /**
     * Demande au VisitorController d'afficher la page de conditions d'utilisation.
     */
    @Override
    public void onConditionsButton() {
        listener.onConditionsAsked();
    }

    public interface RegisterListener {
        void onRegisterAsked(User user);
        void onBackToLogInAsked();
        void onConditionsAsked();
    }
}
