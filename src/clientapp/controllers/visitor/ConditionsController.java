package clientapp.controllers.visitor;

import clientapp.views.visitor.ConditionsViewController;
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
     * Display the interface of the user conditions.
     * @throws IOException display error.
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
     * Allow the closure of the condition user interface.
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Ask to VisitorController to display the register interface.
     */
    @Override
    public void onBackButton() {
        listener.onBackToRegisterAsked();
    }

    public interface ConditionsListener {
        void onBackToRegisterAsked();
    }
}
