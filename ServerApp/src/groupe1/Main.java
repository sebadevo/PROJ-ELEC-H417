package groupe1;

import groupe1.controllers.ServerController;
import javafx.stage.Stage;

import javafx.application.Application;

public class Main extends Application implements ServerController.ServerListener{
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
