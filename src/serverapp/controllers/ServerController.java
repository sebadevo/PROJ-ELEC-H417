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


import java.io.PrintWriter;
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
    private boolean running = true;



    public ServerController(ServerListener listener, Stage stage) {
        this.listener = listener;
        this.stage = stage;
    }

    public void startServer(){
        try {
            show();
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(4321);
            try{
                addConnection();
            }catch (Exception ignored){
                System.out.println("sheh2");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private void onCloseRequest() {
        stage.setOnCloseRequest(e -> {
            try {
                exterminateAll();

            } catch (Exception exception) {
                showError("la fenêtre a dû être fermée");
            }
        });
    }

    public void addConnection(){
        while(running){
            try {
                Socket socket = serverSocket.accept();
                if (socket.isConnected()) {
                    System.out.println("NEW CONNECTION CONTROLLER");
                    connectionController = new ConnectionController(this, socket);
                    // todo conversationController = new ConversationController(this, socket);
                    // todo ConversationController possède une fonction pour démarrer et le connectionController l'appel pour passer  relais;
                }
            } catch (IOException ignored){}
        }
    }


    public void interuptAllConversation(){
        for (ConversationController conversation:connectedControllers) {
            PrintWriter printWriter = conversation.getPrintWrinter();
            printWriter.println("forceExit");
            conversation.forceShutDown();
        }
    }

    @Override
    public void disconnectUser(User user, Socket socket){
        if (user.isConnected()){
            user.setConnected(false);
            UserDatabase.getInstance().logOut(user);
        }
        connectionController = new ConnectionController(this, socket);

    }

    @Override
    public void sendMessage(String message, User user, String destination){
        for (ConversationController conversation:connectedControllers){
            if (conversation.getUser().getUsername().equals(destination)){
                PrintWriter printWriter = conversation.getPrintWrinter();
                printWriter.println(user.getUsername()+DELIMITER+message);
                System.out.printf("Do I arrive here when I send a message ? ");
            }
        }
    }

    @Override
    public void addClientConversation(User user, Socket socket){
        ConversationController conversationController = new ConversationController(this, user, socket);
        connectedControllers.add(conversationController);
        System.out.println("client :" + connectedControllers.size() + " is connected");
        conversationController.start();
    }


    @Override
    public void onDisconnectButton() {
       exterminateAll();
    }

    public void exterminateAll(){
        try {
            disconnectAllUser();
            listener.onClose();
            running = false;
            if (connectionController != null) {
                connectionController.forceShutDown();
            }
            interuptAllConversation();
            serverSocket.close();
            stage.hide();
            System.out.println("i've killed everything that exists");
        }catch (Exception ignore){
            // do nothing
        }
    }

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
