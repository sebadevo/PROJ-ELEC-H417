package clientapp.views;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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
         * Bouton pour rentrer dans l'application
         * @throws Exception execption lié à la connection
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
         * Affiche les messages d'erreur sur la page de Log In
         * @param errorMessage string message d'erreur
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


