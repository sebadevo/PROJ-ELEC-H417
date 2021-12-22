package clientapp.views.visitor;

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
     * Button to send the data to register to the server
     * @throws Exception exception tied to sending the message to the server.
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
     * enables the button register to be available when the checkbox is ticked.
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
     * Displays the error message on the register page
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

