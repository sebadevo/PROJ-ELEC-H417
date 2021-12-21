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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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
     * We hash the messages send for more security .
     * @param message is the text send from a user to another.
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String hashing(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        //Passing data to the created MessageDigest Object
        md.update(message.getBytes());

        //Compute the message digest
        byte[] digest = md.digest();

        //Converting the byte array in to HexString format
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) hexString.append(Integer.toHexString(0xFF & b));
        return hexString.toString();
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
