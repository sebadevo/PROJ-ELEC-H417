package clientapp;

import clientapp.controllers.UserController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import clientapp.controllers.VisitorController;



public class MainClient extends Application implements UserController.UserPageListener, VisitorController.VisitorListener{

    private VisitorController visitorController;
    private UserController userController;
    private Stage stage;

    /**
     * Démarrage de l'application.
     * @param args argument passer en ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Demande à la classe VisitorController d'afficher la page de connexion.
     * @param stage le stage de l'application
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        visitorController = new VisitorController(this, stage);
        visitorController.show();
    }

    /**
     * Crée une fenêtre d'erreur s'il existe un problème
     * issu du projet MVCFX-Demineur de Frédéric Pluquet : https://gitlab.com/devexos/mvcfx-demineur
     * @param type nom de la page qui n'a pas pu être chargée
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
     * Permet d'assigner à l'attribut user du main le user qui est connecté et demande à la classe UserCotnroller
     * d'afficher la page du menu principal.
     */
    @Override
    public void logIn(String username, String password) {
        userController = new UserController(this, stage);
        try {
            userController.show();
        } catch (Exception e) {
            showError(UserController.LOAD_PRINCIPAL_USER_PAGE_ERROR);
        }
    }

    /**
     * Demande à la classe VisitorController d'afficher la page de connexion.
     */
    @Override
    public void logOut() {
        System.out.println("LOG OUT"); // DEBUG
        // TODO déconnection au server
        try {
            visitorController.show();
        } catch (Exception e) { System.out.println(e); }
    }

    @Override
    public void sendMessage(String message) {
        receiveMessage(message); // DEBUG
        // TODO send message to the server
    }

    public void receiveMessage(String message) {
        userController.receiveText(message);
    }
}
