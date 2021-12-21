package clientapp.controllers;

import clientapp.MainClient;
import clientapp.models.User;
import clientapp.views.user.UserPageViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserController extends Thread implements UserPageViewController.UserPageViewListener {

    private static final String DELIMITER = "-";
    private final UserPageListener listener;
    private final Stage stage;
    private UserPageViewController userPageViewController;
    private User user;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private boolean running = true;

    public static final String LOAD_PRINCIPAL_USER_PAGE_ERROR = "the principal user window had to be displayed";

    public UserController(UserPageListener listener, Stage stage, User user, Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.stage = stage;
        this.listener = listener;
        this.user = user;
        this.socket = socket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
    }

    /**
     * Permet l'affichage de UserPageView.
     * @throws IOException erreur d'affichage
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(UserPageViewController.class.getResource("UserPageView.fxml"));
        loader.load();
        userPageViewController = loader.getController();
        userPageViewController.setListener(this);
        Parent page = loader.getRoot();
        stage.setScene(new Scene(page));
        stage.show();
        onCloseRequest();
    }

    /**
     * Permet de détecter si on ferme la fenêtre et d'en notifier la classe UserController.
     */
    private void onCloseRequest() {
        stage.setOnCloseRequest(e -> {
            try {
                printWriter.println("exit");
                running =false;
                listener.onClose();
            } catch (Exception exception) {
                MainClient.showError("la fenêtre a dû être fermée");
            }
        });
    }

    /**
     * Permet de fermer la page du menu principal.
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Permet de déconnecter un User et de demander à la classe UserController de changer de fenêtre.
     */
    @Override
    public void onLogOutButtonPressed() {
        printWriter.println("exit");
        running =false;
        listener.logOut();
    }

    @Override
    public void onSendButtonPressed(String message) throws NoSuchAlgorithmException {
        //printWriter.println(message);
        message = hashing(user.getUsername())+DELIMITER+message + " (from " + user.getUsername() + ")";
        printWriter.println(message);
        receiveText(message);
    }

    public void receiveText(String message) {
        System.out.println(message);
        System.out.println("does forceExit = forceExit? : " + message.equals("forceExit"));
        if ("forceExit".equals(message)) {
            running = false;
            listener.logOut();
        }
        userPageViewController.addReadingArea(message);
    }

    /**
     * We hash the messages send for more security .
     * @param message is the text send from a user to another.
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String hashing(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        //Passing data to the created MessageDigest Object
        md.update(message.getBytes());

        //Compute the message digest
        byte[] digest = md.digest();

        //Converting the byte array in to HexString format
        StringBuilder hexString = new StringBuilder();

        for (byte b : digest) hexString.append(Integer.toHexString(0xFF & b));
        return hexString.toString();
    }

    @Override
    public void run(){
        while(running){
            try{
                receiveText(bufferedReader.readLine());
            } catch (Exception ignore){}
        }
    }

    public interface UserPageListener {
        void logOut();
        void onClose();
    }
}

