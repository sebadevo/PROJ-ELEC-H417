package Server;

import Server.controllers.ServerController;
import javafx.stage.Stage;

import javafx.application.Application;

public class MainServer extends Application implements ServerController.ServerListener{
    private ServerController serverController;
    private Stage stage;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage){
        this.stage = stage;
        serverController = new ServerController(this,stage);
        try {
            serverController.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
