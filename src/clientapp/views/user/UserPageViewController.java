package clientapp.views.user;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import java.security.NoSuchAlgorithmException;

public class UserPageViewController {

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

    public void onSendButton() throws NoSuchAlgorithmException {
        String message = writtingField.getText();
        writtingField.setText("");
        listener.onSendButtonPressed(message);
    }

    public void addReadingArea(String message){
        // TODO check comment ça fonctionne
        readingArea.appendText(message + "\n");
    }

    public interface UserPageViewListener {
        void onLogOutButtonPressed() throws Exception;
        void onSendButtonPressed(String message) throws NoSuchAlgorithmException;
    }
}
