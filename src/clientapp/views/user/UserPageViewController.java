package clientapp.views.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import java.security.NoSuchAlgorithmException;

public class UserPageViewController {
    @FXML
    public TextField destinationTextField;
    @FXML
    public Label errorMessageLabel;
    @FXML
    public Label identificationLabel;
    @FXML
    private TextField writtingField;
    @FXML
    private TextArea readingArea;

    private UserPageViewListener listener;

    public void setListener(UserPageViewListener listener) {
        this.listener = listener;
    }

    /**
     * Détecte si on a appuyé sur le bouton "Log Out" et notifie le controller.
     * @throws Exception exception lié à la déconnection
     */
    public void onLogOutButton() throws Exception {
        listener.onLogOutButtonPressed();
    }

    public void setIdentificationLabel(String name){
        identificationLabel.setText(name);
    }

    /**
     * Détecte quand on appuie sur le bouton "Send" et vérifie si les fields destination et writing ne sont pas vide.
     * S'ils ne sont pas vide, alors il notifiera le controller pour qu'ils puisse l'envoyé.
     */
    public void onSendButton() throws NoSuchAlgorithmException, InterruptedException {
        if (!destinationTextField.getText().equals("") && !writtingField.getText().equals("")){
            String destination = destinationTextField.getText();
            String message = writtingField.getText();
            writtingField.setText("");
            listener.onSendButtonPressed(destination, message);
        }else{
            setErrorMessage("no Senders precised...");
        }
    }

    /**
     * Affiche les messages d'erreur sur la page de Log In
     * @param errorMessage string message d'erreur
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setText(errorMessage);
        errorMessageLabel.setVisible(true);
    }

    public void addReadingArea(String message){
        readingArea.appendText(message + "\n");
    }

    public interface UserPageViewListener {
        void onLogOutButtonPressed() throws Exception;
        void onSendButtonPressed(String destination, String message) throws NoSuchAlgorithmException, InterruptedException;
    }
}
