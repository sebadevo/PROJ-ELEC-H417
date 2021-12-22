package clientapp.views.visitor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogInViewController {

    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    private LogInViewListener listener;

    public void setListener(LogInViewListener listener) {
        this.listener = listener;
    }

    /**
     * Button to log in
     * @throws Exception exception tied with the login
     */
    public void onLogInButton() throws Exception {
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        listener.onLogInButton(username, password);
    }

    public void onRegisterLink() {
        listener.onRegisterLink();
    }

    /**
     * Displays the error messages on the login page
     * @param errorMessage error message string
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setText(errorMessage);
        errorMessageLabel.setVisible(true);
    }

    public interface LogInViewListener {
        void onLogInButton(String username, String password) throws Exception;
        void onRegisterLink();
    }
}
