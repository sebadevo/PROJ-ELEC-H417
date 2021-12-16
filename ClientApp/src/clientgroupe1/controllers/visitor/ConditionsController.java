package clientgroupe1.controllers.visitor;

import clientgroupe1.views.visitor.ConditionsViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ConditionsController implements ConditionsViewController.ConditionsViewListener {

    private final ConditionsListener listener;
    private final Stage stage;

    public static final String LOAD_CONDITIONS_PAGE_ERROR = "the condition window had to be displayed";

    public ConditionsController(ConditionsListener listener, Stage stage){
        this.stage = stage;
        this.listener = listener;
    }

    /**
     * Permet d'afficher la page des conditions d'utilisation.
     * @throws IOException erreur d'affichage
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(ConditionsViewController.class.getResource("ConditionsView.fxml"));
        loader.load();
        ConditionsViewController controller = loader.getController();
        controller.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Permet de fermer la page de conditions d'utilisation
     */
    public void hide() {
        stage.hide();
    }

    /**
     * demande au VisitorController d'afficher la page de register.
     */
    @Override
    public void onBackButton() {
        listener.onBackToRegisterAsked();
    }

    public interface ConditionsListener {
        void onBackToRegisterAsked();
    }
}
