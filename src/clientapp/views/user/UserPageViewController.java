package clientapp.views.user;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import java.security.NoSuchAlgorithmException;

public class UserPageViewController {
    @FXML
    public TextField destinationTextField;
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

    /**
     * Détecte quand on appuie sur le bouton "Send" et vérifie si les fields destination et writing ne sont pas vide.
     * S'ils ne sont pas vide, alors il notifiera le controller pour qu'ils puisse l'envoyé.
     * @throws NoSuchAlgorithmException
     */
    public void onSendButton() throws NoSuchAlgorithmException {
        if (!destinationTextField.getText().equals("") && !writtingField.getText().equals("")){
            String destination = destinationTextField.getText();
            String message = writtingField.getText();
            writtingField.setText("");
            listener.onSendButtonPressed(destination, message);
        }else{
            System.out.println("no Senders precised...");
        }
    }

    public void addReadingArea(String message){
        readingArea.appendText(message + "\n");
    }

    public interface UserPageViewListener {
        void onLogOutButtonPressed() throws Exception;
        void onSendButtonPressed(String destination, String message) throws NoSuchAlgorithmException;
    }
}
