package groupe1.views.visitor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterViewController {
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
    private PasswordField passwordField;
    @FXML
    private Button registerButton;

    private RegisterViewListener listener;

    public void setListener(RegisterViewListener registerViewListener) {
        this.listener = registerViewListener;
    }

    /**
     * Bouton pour sauver les données une fois qu'on s'est inscrits
     * @throws Exception exception lié à l'ajout dans la database.
     */
    public void onRegisterButton() throws Exception {
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String firstname = firstnameTextField.getText();
        String lastname = lastnameTextField.getText();
        String email = emailTextField.getText();
        listener.onRegisterButton(firstname, lastname, username, email, password);
    }

    /**
     * Permet de rendre le bouton "Register" accessible quand on a coché la checkBox
     */
    public void onConditionsCheckBox() {
        registerButton.setDisable(!registerButton.disableProperty().getValue());
    }

    public void onBackButton() {
        listener.onBackButton();
    }

    public void onConditionsButton() {
        listener.onConditionsButton();
    }

    /**
     * Affiche les messages d'erreur sur la page de Register
     * @param errorMessage string message d'erreur
     */
    public void setErrorMessage(String errorMessage){
        errorMessageLabel.setText(errorMessage);
        errorMessageLabel.setVisible(true);
    }

    public interface RegisterViewListener {
        void onRegisterButton(String firstname, String lastname, String username, String email, String password) throws Exception;
        void onBackButton();
        void onConditionsButton();
    }
}

