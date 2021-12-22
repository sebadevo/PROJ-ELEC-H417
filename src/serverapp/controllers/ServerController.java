package serverapp.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import serverapp.controllers.wireless.ConnectionController;
import serverapp.controllers.wireless.ConversationController;
import serverapp.models.User;
import serverapp.views.ServerViewController;
import serverapp.models.databases.UserDatabase;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import static serverapp.MainServer.DELIMITER;
import static serverapp.MainServer.showError;


public class ServerController extends Thread implements ConnectionController.ConnectionListener,
        ServerViewController.ServerViewListener, ConversationController.ConversationListener{
    private ServerViewController ServerViewController;
    private ConnectionController connectionController;
    private final ServerListener listener;
    private final Stage stage;
    private final List<ConversationController> connectedControllers = new ArrayList<>(); // Liste de sockets.
    private ServerSocket serverSocket;
    private volatile boolean running = true;


    /**
     * Constructor receives the stage and his listener.
     * @param listener the main class
     * @param stage the stage where the gui will be
     */
    public ServerController(ServerListener listener, Stage stage) {
        this.listener = listener;
        this.stage = stage;
    }


    /**
     * Starts the main thread of the server and launches the gui.
     */
    public void startServer(){
        try {
            show();
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main Thread of the server. Creates the server socket and enables the addition of client connections.
     */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(4321);
            try{
                addConnection();
            }catch (Exception ignored){
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Shows the gui. Holds a RIP button that shuts down everything
     * @throws IOException exception in stage display
     */
    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(ServerViewController.class.getResource("ServerView.fxml"));
        loader.load();
        ServerViewController = loader.getController();
        ServerViewController.setListener(this);
        Parent root = loader.getRoot();
        stage.setScene(new Scene(root));
        stage.setTitle("Meta Server");
        stage.show();
        onCloseRequest();
    }

    /**
     * When pressing on the cross button of the window it closes the application and shutdown everything, it will also
     * disconnect all users curruntly connected and terminate their session.
     */
    private void onCloseRequest() {
        stage.setOnCloseRequest(e -> {
            try {
                exterminateAll();

            } catch (Exception exception) {
                showError("la fenêtre a dû être fermée");
            }
        });
    }

    /**
     * Will accept all client launching the app and will send them to the connection controller that will verify their
     * information when trying to log in or will register a new user.
     */
    public void addConnection(){
        while(running){
            try {
                Socket socket = serverSocket.accept();
                if (socket.isConnected()) {
                    connectionController = new ConnectionController(this, socket);
                }
            } catch (IOException ignored){}
        }
    }


    /**
     * interrupts the conversation of each client and sends them the message to shutdown on their side too.
     */
    public void interuptAllConversation(){
        for (ConversationController conversation:connectedControllers) {
            conversation.getPrintWrinter().println("forceExit");
            conversation.forceShutDown();
        }
    }
    /**
     * Sets the user as disconnected in the database and send the client to the connection controller if he wants to
     * login with a different account.
     */
    @Override
    public void disconnectUser(User user, Socket socket){
        if (user.isConnected()){
            user.setConnected(false);
            UserDatabase.getInstance().logOut(user);
        }
        connectionController = new ConnectionController(this, socket);

    }

    /**
     * Send a message from the user to the user called "destination"
     * @param message encrypted message
     * @param user source
     * @param destination destination
     */
    @Override
    public void sendMessage(String message, User user, String destination){
        for (ConversationController conversation:connectedControllers){
            if (conversation.getUser().getUsername().equals(destination)){
            conversation.getPrintWrinter().println(user.getUsername()+DELIMITER+message);
            }
        }
    }


    /**
     * first messages between 2 clients, they will exchange key using the Diffie-Hellman method.
     * ga being g^a from the user "user" and send it the the user "destination"
     * @param ga g^a
     * @param user source
     * @param destination destination
     */
    @Override
    public void keyExchange(String ga, User user, String destination){
        for (ConversationController conversation:connectedControllers){
            if (conversation.getUser().getUsername().equals(destination)){
                conversation.getPrintWrinter().println("key"+DELIMITER+user.getUsername()+DELIMITER+ga);
            }
        }
    }

    /**
     * Once a client has loged in or registered, he is sent to the conversation controller which will take care of
     * the conversation this client will have.
     * @param user client connected
     * @param socket socket
     */
    @Override
    public void addClientConversation(User user, Socket socket){
        ConversationController conversationController = new ConversationController(this, user, socket);
        connectedControllers.add(conversationController);
        conversationController.start();
    }

    /**
     * When pressing the RIP button, call the exterminateALL() function.
     */
    @Override
    public void onDisconnectButton() {
       exterminateAll();
    }

    /**
     * Will kill the server app by correctly disconnecting each clients, will update the database and shut down
     */
    public void exterminateAll(){
        try {
            running = false;
            interuptAllConversation();
            disconnectAllUser();
            listener.onClose();
            if (connectionController != null) {
                connectionController.forceShutDown();
            }
            serverSocket.close();
            stage.hide();
            System.out.println("killed everything");
        }catch (Exception ignore){
            // do nothing
        }
    }

    /**
     * Sets all currently connected users as disconected in the database.
     */
    private void disconnectAllUser() {
        for (ConversationController conversation:connectedControllers){
            User user =conversation.getUser();
            if (user.isConnected()){
                user.setConnected(false);
                UserDatabase.getInstance().logOut(user);
            }
        }
    }

    public interface ServerListener {
        void onClose();
    }
}
