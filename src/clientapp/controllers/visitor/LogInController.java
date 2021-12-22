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
import java.security.NoSuchAlgorithmException;

import static clientapp.MainClient.DELIMITER;
import static clientapp.models.Crypto.hashing;

public class LogInController implements LogInViewController.LogInViewListener {

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
     * Allow the display of the connexion interface.
     * @throws IOException display error
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
     * Allow the closure of the connexion interface.
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Send to the server the Information the client inserted in the fields to log in. This method will expect an
     * Answer from the server telling him if the information provided are valid.
     * If they are valid will proceed to log the client in and will ask it's listener to change pages to the main page.
     * If the information are not valid, an error message will be displayed on the page.
     * @param username string
     * @param password string
     */
    @Override
    public void onLogInButton(String username, String password) throws NoSuchAlgorithmException {
        if (!username.isEmpty() && !password.isEmpty()) {
            printWriter.println("0" + DELIMITER + hashing(username) + DELIMITER + hashing(password));
            try {
                String answer = bufferedReader.readLine();
                if (answer.equals("true")) {
                    String Id = bufferedReader.readLine();
                    if (!Id.isEmpty()) {
                        String[] userInfo = Id.split(DELIMITER);
                        String firstname = userInfo[0];
                        String lastname = userInfo[1];
                        String email = userInfo[2];
                        User user = new User(firstname, lastname, username, email, password);
                        listener.onLogInAsked(user);
                    } else {
                        logInViewController.setErrorMessage("The connection with the server took too long, connection" +
                                "restarting...");
                    }
                } else {
                    logInViewController.setErrorMessage(answer);
                }
            } catch (IOException ignore) {}
        }else{
            logInViewController.setErrorMessage("One or more fields are empty");
        }
    }

    /**
     * Ask to the VisitorController class to display the register interface.
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
