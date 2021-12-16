package clientgroupe1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import clientgroupe1.controllers.UserController;
import clientgroupe1.controllers.VisitorController;
import clientgroupe1.models.User;
import clientgroupe1.models.databases.Database;
import clientgroupe1.models.databases.UserDatabase;
import clientgroupe1.models.databases.exceptions.DatabaseLoadException;
import clientgroupe1.models.databases.exceptions.DatabaseSaveException;


public class MainClient extends Application implements UserController.UserListener, VisitorController.VisitorListener{

    private VisitorController visitorController;
    private UserController userController;
    private Stage stage;
    private User user;

    /**
     * Démarrage de l'application.
     * @param args argument passer en ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Charge les databases et demande à la classe VisitorController d'afficher la page de connexion.
     * @param stage le stage de l'application
     */
    @Override
    public void start(Stage stage) {
        try {
            UserDatabase.getInstance().load();
        } catch (DatabaseLoadException e){
            showError(Database.LOAD_ERROR);
        }
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
     * @param user le user connecté
     */
    @Override
    public void logIn(User user) {
        this.user = user;
        userController = new UserController(this, stage, user);
        userController.show();
    }

    /**
     * Demande à la classe VisitorController d'afficher la page de connexion.
     */
    @Override
    public void logOut() {
        visitorController.show();
    }

    /**
     * Permet de sauvegarder les databases et de déconnecter le user connecté quand on ferme l'application.
     */
    @Override
    public void onClose() {
        if (user.isConnected()){
            user.setConnected(false);
            UserDatabase.getInstance().logOut(user);
        }
        try {
            UserDatabase.getInstance().save();
        } catch (DatabaseSaveException e) {
            showError(Database.SAVE_ERROR);
        }
    }

}
