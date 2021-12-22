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
     * Détects when we press on the send button and checks that the destination field and writing fields are not empty.
     * If they are not empty, then we notify the controller so that he can send them.
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
     * Displays the error messages on the Main user page.
     * @param errorMessage string message d'erreur
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setText(errorMessage);
        errorMessageLabel.setVisible(true);
    }

    /**
     * Appends a message to the message Area
     * @param message message to append
     */
    public void addReadingArea(String message){
        readingArea.appendText(message + "\n");
    }

    public interface UserPageViewListener {
        void onLogOutButtonPressed() throws Exception;
        void onSendButtonPressed(String destination, String message) throws NoSuchAlgorithmException, InterruptedException;
    }
}
