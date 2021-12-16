package Client.views.user;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileModificationViewController {

    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField oldPasswordField;

    private ProfileModificationViewListener listener;

    public void setListener(ProfileModificationViewListener listener) {
        this.listener = listener;
    }

    /**
     * Détecte si on a appuyé sur le bouton "Back" et le notifie au controller.
     */
    public void onBackButton() {
        listener.onBackButtonPressed();
    }

    /**
     * Récupère les données rentrées dans les fields et envoie celles-ci au controller pour modifier le user connecté
     */
    public void onConfirmModificationsButton() {
        String username = usernameTextField.getText();
        String password = newPasswordField.getText();
        String oldPassword = oldPasswordField.getText();
        String firstname = firstnameTextField.getText();
        String lastname = lastnameTextField.getText();
        String email = emailTextField.getText();
        listener.onConfirmProfileModificationButtonPressed(firstname, lastname, username, email, password, oldPassword);
    }

    /**
     * Remplit les fields de la page avec les données passées en argument.
     * @param firstname string
     * @param lastname string
     * @param username string
     * @param email string
     */
    public void setFields(String firstname, String lastname, String username, String email) {
        firstnameTextField.setText(firstname);
        lastnameTextField.setText(lastname);
        usernameTextField.setText(username);
        emailTextField.setText(email);
    }

    /**
     * Affiche le message d'erreur passé en argument.
     * @param errorMessage string
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setText(errorMessage);
        errorMessageLabel.setVisible(true);
    }

    public interface ProfileModificationViewListener {
        void onBackButtonPressed();
        void onConfirmProfileModificationButtonPressed(String firstname, String lastname, String username, String email, String password, String oldPassword);
    }
}
