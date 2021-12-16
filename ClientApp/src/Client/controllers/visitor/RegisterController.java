package Client.controllers.visitor;

import Client.models.User;
import Client.models.databases.UserDatabase;
import Client.models.databases.exceptions.DatabaseSaveException;
import Client.views.visitor.RegisterViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static Client.models.User.checkSyntax;

public class RegisterController implements RegisterViewController.RegisterViewListener {

    private final RegisterListener listener;
    private final Stage stage;
    private RegisterViewController registerViewController;

    public static final String LOAD_REGISTER_PAGE_ERROR = "the register window had to be displayed";

    public RegisterController(RegisterListener listener, Stage stage) {
        this.stage = stage;
        this.listener = listener;
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
        if (checkSyntax(firstname, lastname, username, email, password)) {
            User user = new User(firstname, lastname, username, email, password);
            if (!UserDatabase.getInstance().checkExistingUser(user)) {
                UserDatabase.getInstance().add(user);
                try {
                    UserDatabase.getInstance().save();
                } catch (DatabaseSaveException e) {
                    registerViewController.setErrorMessage("Error saving user to database");
                }
                listener.onRegisterAsked();
            } else if (UserDatabase.getInstance().checkExistingEmail(user.getEmailAddress())){
                registerViewController.setErrorMessage("Email already taken");
            } else {
                registerViewController.setErrorMessage("Username already taken");
            }
        } else {
            registerViewController.setErrorMessage("Wrong email address or empty fields");
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
        void onRegisterAsked();
        void onBackToLogInAsked();
        void onConditionsAsked();
    }
}
