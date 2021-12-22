package clientapp;

import clientapp.controllers.UserController;
import clientapp.models.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import clientapp.controllers.VisitorController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class MainClient extends Application implements UserController.UserPageListener, VisitorController.VisitorListener{

    private VisitorController visitorController;
    private UserController userController;
    private Stage stage;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    public static final String DELIMITER = "-%%-%%-%%-%%-";

    /**
     * Launches the applications
     * @param args arguments send by command line.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Asks the VisitorController class to display the connection page.
     * @param stage the stage of the application
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        try {
            socket = new Socket("localhost", 4321);
            printWriter = new PrintWriter(this.socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            visitorController = new VisitorController(this, stage, socket, printWriter, bufferedReader);
            visitorController.show();
        } catch (IOException ignore) {}
    }

    /**
     * Creates an error window if an error exists.
     * Source : MVCFX-Demineur project of Frédéric Pluquet : https://gitlab.com/devexos/mvcfx-demineur
     * @param type name of the page that couldn't be loaded.
     */
    public static void showError(String type) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occured when " + type + ".");
        alert.setContentText("It's not your fault but the developper's one. The program will now terminate.");
        alert.showAndWait();
        Platform.exit();
    }

    /**
     * When logging, creates a UserController object that will display the main menu of the communication application.
     */
    @Override
    public void logIn(User user) {
        try {
            userController = new UserController(this, stage, user, socket, printWriter, bufferedReader);
            userController.show();
            userController.start();
        } catch (Exception e) { showError(UserController.LOAD_PRINCIPAL_USER_PAGE_ERROR); }
    }

    /**
     * Logs out and calls the visitorController to load the visitor page.
     */
    @Override
    public void logOut() {
        try {
            visitorController.show();
        } catch (Exception e) { System.out.println(e); }
    }

    /**
     * Closes the socket once the application is shutdown.
     */
    @Override
    public void onClose(){
        try{
           socket.close();
        } catch (Exception e) { System.out.println(e); }
    }
}