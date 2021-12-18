package serverapp;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import serverapp.controllers.ServerController;
import serverapp.models.databases.Database;
import serverapp.models.databases.UserDatabase;
import serverapp.models.databases.exceptions.DatabaseLoadException;
import serverapp.models.databases.exceptions.DatabaseSaveException;
import javafx.application.Application;


public class MainServer extends Application implements ServerController.ServerListener{
    private ServerController serverController;


    public static void main(String[] args){
        launch(args);
    }

    public static void showError(String type) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occured when " + type + ".");
        alert.setContentText("It's not your fault but the developper's one. The program will now terminate.");
        alert.showAndWait();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) {
        try {
            UserDatabase.getInstance().load();
        } catch (DatabaseLoadException e){
            showError(Database.LOAD_ERROR);
        }
        serverController = new ServerController(this, stage);

        serverController.startServer();
    }

    @Override
    public void onClose() {
        try {
            UserDatabase.getInstance().save();
        } catch (DatabaseSaveException e) {
            showError(Database.SAVE_ERROR);
        }
    }
}
