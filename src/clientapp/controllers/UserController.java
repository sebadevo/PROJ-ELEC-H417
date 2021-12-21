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

import static clientapp.MainClient.DELIMITER;
import static clientapp.models.Crypto.hashing;

public class UserController extends Thread implements UserPageViewController.UserPageViewListener {
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
    public void onSendButtonPressed(String destinataire, String message) throws NoSuchAlgorithmException {
        /*
        if hashing(destinataire) is dans liste keys:
            envoie message
        sinon:
            key = diffie-hellman()
            store key
            send message
         */
        // A -> B
        // hash(B)
        // encryption du message
        if (!user.getUsername().equals(destinataire)) { // permet d'afficher qu'une seule fois les message envoyé à sois même
            receiveText(user.getUsername() + " : " + message); // Feedback message
        }
        message = hashing(destinataire) + DELIMITER + user.getUsername() + " : " + message;
        printWriter.println(message);
    }

    public void receiveText(String message) {
        System.out.println(message);
        System.out.println("does forceExit = forceExit? : " + message.equals("forceExit"));
        // decrypter le message

        if ("forceExit".equals(message)) {
            running = false;
            listener.logOut();
            return;
        }

        userPageViewController.addReadingArea(message);
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

