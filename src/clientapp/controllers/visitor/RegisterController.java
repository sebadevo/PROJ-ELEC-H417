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

import java.security.NoSuchAlgorithmException;

import static clientapp.MainClient.DELIMITER;
import static clientapp.models.Crypto.hashing;


public class RegisterController implements RegisterViewController.RegisterViewListener {

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
     * Allow the display of the register interface.
     * @throws IOException display error.
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
     * Allow the closure of the register interface.
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Create a user if the entered fields are correct and ask to VisitorController to display the connection interface.
     * displays error message in the case where the information are not valid (ex: username already taken).
     * @param firstname string
     * @param lastname string
     * @param username string
     * @param email string
     * @param password string
     */
    @Override
    public void onRegisterButton(String firstname, String lastname, String username, String email, String password) throws NoSuchAlgorithmException {
        if (!firstname.isEmpty() && !lastname.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            System.out.println(hashing("test"));
            printWriter.println("1" + DELIMITER + hashing(firstname) + DELIMITER + hashing(lastname) + DELIMITER + hashing(username) + DELIMITER + hashing(email) + DELIMITER + hashing(password));
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
     * Ask to VisitorController to display the connection interface.
     */
    @Override
    public void onBackButton() {
        listener.onBackToLogInAsked();
    }

    /**
     * Ask to the VisitorController to display the term of agreements.
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
