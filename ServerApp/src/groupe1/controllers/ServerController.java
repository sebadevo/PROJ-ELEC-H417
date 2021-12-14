package groupe1.controllers;

import groupe1.views.ServerViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerController implements ServerViewController.ServerViewListener {
    private final ServerListener listener;
    private final Stage stage;
    private ServerViewController serverViewController;

    public ServerController(ServerListener listener, Stage stage) {
        this.listener = listener;
        this.stage = stage;
    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(ServerController.class.getResource("ServerView.fxml"));
        loader.load();
        serverViewController = loader.getController();
        serverViewController.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.setTitle("Meta Server");
        stage.show();
    }

    public void hide(){
        stage.hide();
    }

    public interface ServerListener {
    }
}
