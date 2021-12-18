package clientapp.views.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserPageViewController {

    @FXML
    private Label errorMessageLabel;

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
     * Détecte si on a appuyé sur le bouton "Modify profile" et notifie le controller.
     */
    public void onUserModificationButton() {
        listener.onModifyUserButtonPressed();
    }


    /**
     * Affiche le message d'erreur.
     * @param errorMessage string
     */
    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setText(errorMessage);
        errorMessageLabel.setVisible(true);
    }

    public interface UserPageViewListener {
        void onLogOutButtonPressed() throws Exception;
        void onModifyUserButtonPressed();
    }
}
