package clientapp.controllers;

import clientapp.MainClient;
import clientapp.models.FriendsKey;
import clientapp.models.User;
import clientapp.views.user.UserPageViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import static clientapp.MainClient.DELIMITER;
import static clientapp.models.Crypto.decrypt;
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
        System.out.println("JE SUIS "+user.getUsername());
        FXMLLoader loader = new FXMLLoader(UserPageViewController.class.getResource("UserPageView.fxml"));
        loader.load();
        userPageViewController = loader.getController();
        userPageViewController.setListener(this);
        Parent page = loader.getRoot();
        stage.setScene(new Scene(page));
        stage.show();
        userPageViewController.setIdentificationLabel("Connected as : \n"+user.getUsername());
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

    private void sendPublicKey(String destinataire) {
        printWriter.println("key"+DELIMITER+destinataire+DELIMITER+user.getGa());
    }

    private void addNewFriend(String gb, String hashUsername) {
        BigInteger gB = new BigInteger(gb) ;
        for (FriendsKey friend : user.getFriends()){
            if (friend.getFriendName().equals(hashUsername)){
                friend.setKey(user.getGa(), gB);
                return;
            }
        }
        FriendsKey friend = new FriendsKey(hashUsername);
        friend.setKey(user.getGa(), gB);
        user.addFriends(friend);
        sendPublicKey(hashUsername);
    }


    @Override
    public void onSendButtonPressed(String username, String message) throws NoSuchAlgorithmException {
        // TODO check si dans contact key
        String destinataire = hashing(username);
        if (!user.checkFriends(destinataire) && !user.getUsername().equals(username)){
            //SecretKeySpec key = Crypto.generateKey(temp)
            FriendsKey friend = new FriendsKey(destinataire);
            user.addFriends(friend);
            sendPublicKey(destinataire);
            int temp = 0;
            while(null == friend.getKey()){
                System.out.println("est ce que je bug "+temp);
                try{
                    sleep(1000);
                } catch (Exception ignore){}
                temp++;
                if (temp>10){ // wait 5 sec
                    userPageViewController.setErrorMessage("Message not sent, user : "+ username +" did not answer.");
                    user.removeFriend(destinataire);
                    return;
                }
            }
        }
        if (!user.getUsername().equals(username)) { // permet d'afficher qu'une seule fois les message envoyé à sois même
            receiveText(user.getUsername() + " : " + message); // Feedback message
        }
        message = destinataire + DELIMITER + user.getUsername() + " : " + message;
        printWriter.println(message);
        userPageViewController.setErrorMessage("");
    }



    public void receiveText(String message) {
        System.out.println(message);
        // decrypter le message
        String[] header = message.split(DELIMITER);
        switch (header[0]) {
            case "key":
                String ga = header[1];
                String hashusername = header[2];
                addNewFriend(ga, hashusername);
                break;
            case "forceExit":
                running = false;
                listener.logOut();
                return;
            default:

                //String decrypted = decrypt(message);
                userPageViewController.addReadingArea(message);
        }
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

