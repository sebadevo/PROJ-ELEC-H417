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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import static clientapp.MainClient.DELIMITER;
import static clientapp.models.Crypto.*;

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
     * Allow the display of the UserPageView interface.
     * @throws IOException display error.
     */
    public void show() throws IOException {
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
     * Detect if we close the window and notify the UserController class.
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
     * Allow the closure of the main menu interface.
     */
    public void hide() {
        stage.hide();
    }

    /**
     * Deconnect a user and ask to the UserController class to change window.
     */
    @Override
    public void onLogOutButtonPressed() {
        printWriter.println("exit");
        running =false;
        listener.logOut();
    }

    /**
     * Sends the public key in a specific format that the server can understand.
     * @param destinataire the user with want to exchange key with.
     */
    private void sendPublicKey(String destinataire) {
        printWriter.println("key"+DELIMITER+destinataire+DELIMITER+user.getGa());
    }

    /**
     * Will create a new friend or update one if this one had already been created. It will store the encryption key in
     * of the friend and the hashed username in the object FriendsKey, which is stored in a list of Firendskey.
     * If was not originaly created, it means it's is the first time we are communicating together and thus we have
     * to send him our public key. (this is part of the KeyExchange protocol)
     * @param gb g^b
     * @param hashUsername hashed username of the sender.
     */
    private void addNewFriend(String gb, String hashUsername) {
        BigInteger gB = new BigInteger(gb) ;
        for (FriendsKey friend : user.getFriends()){
            if (friend.getFriendName().equals(hashUsername)){
                friend.setKey(user.getA(), gB);
                return;
            }
        }
        FriendsKey friend = new FriendsKey(hashUsername);
        friend.setKey(user.getA(), gB);
        user.addFriends(friend);
        sendPublicKey(hashUsername);
    }


    /**
     * Allow the sending of a message to a designated recipient. If the message is sent for the first time, a key
     * exchange protocol will take place where the user sending the message will first send it's public key, then will
     * wait for the other client to send it's key. if the key is not received in the next 10 seconds the key exchange
     * protocol will end and show a fail message on the gui. In the case where the key exchange protocol did work,
     * the Client encryption key will be stored in a "friends list" so that it can decrypt and encrypt all the following
     * messages from and to the given client.
     * @param username username of the client we are sending the message to
     * @param message not encrypted message
     * @throws NoSuchAlgorithmException
     */
    @Override
    public void onSendButtonPressed(String username, String message) throws NoSuchAlgorithmException {
        String destinataire = hashing(username);
        if (!user.checkFriends(destinataire)){
            FriendsKey friend = new FriendsKey(destinataire);
            user.addFriends(friend);
            sendPublicKey(destinataire);
            int temp = 0;
            while(null == friend.getKey()){
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
        if (!user.getUsername().equals(username)) {
            userPageViewController.addReadingArea(user.getUsername() + " : " + message); // Feedback message
        }
        String encryptedMessage = null;
        try {
            encryptedMessage = encrypt(user.getUsername() + " : " + message,user.getFriendsKey(destinataire) );
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message = destinataire + DELIMITER + encryptedMessage;
        printWriter.println(message);
        userPageViewController.setErrorMessage("");
    }


    /**
     * Receive the message and manage it depending on the header of the message. if no header is specified, then it is
     * supposed to be a direct message from another client that has been encrypted.
     * @param message Message received from the server
     */
    public void receiveText(String message) {
        String[] header = message.split(DELIMITER);
        switch (header[0]) {
            case "key":
                String ga = header[2];
                String hashusername = header[1];
                addNewFriend(ga, hashusername);
                break;
            case "forceExit":
                running = false;
                System.exit(-1);
                return;
            default:
                String decrypted = decryptMessage(header[0], header[1]);
                userPageViewController.addReadingArea(decrypted);
        }
    }

    /**
     * Try to decrypt the encryptedMessage using the hashed username "source" to know wich key is needed to decrypt the
     * the message, if it is not successful an error is launched.
     * @param source hashed username of the sender.
     * @param encryptedMessage encrypted message
     * @return the decrypted message or nothing if it failed.
     */
    private String decryptMessage(String source, String encryptedMessage) {
        try {
            return decrypt(encryptedMessage, user.getFriendsKey(source));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Thread that receives all the message from the server and sends them to the receiveText() method.
     */
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

