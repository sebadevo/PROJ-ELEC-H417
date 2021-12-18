package serverapp.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import serverapp.MainServer;
import serverapp.controllers.connection.ConnectionController;
import serverapp.models.Conversation;
import serverapp.views.ServerViewController;


import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;



public class ServerController extends Thread implements ConnectionController.ConnectionListener, ServerViewController.ServerViewListener, Runnable {
    private ServerViewController ServerViewController;
    private ConnectionController connectionController;
    private final ServerListener listener;
    private final Stage stage;
    int nbClients;
    private final List<Conversation> connectedClients = new ArrayList<>(); // Liste de sockets.
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
                MainServer.showError("la fenêtre a dû être fermée");
            }
        });
    }

    public void addConnection(){
        while(running){ //todo faire que quand on ecrit exit dans le terminal, alors le serveur s'arrete.
            try {
                System.out.println("je suis le client : 1");
                Socket socket = serverSocket.accept();
                if (socket.isConnected()) {
                    connectionController = new ConnectionController(this, socket);
                }
            } catch (IOException ignored){}
        }
    }


    public void interuptConversation(){
        for (Conversation c:connectedClients) {
            c.shutdownThread();
            System.out.println(c.isAlive());
        }
    }


    @Override
    public void clientConversation(Socket socket){
        ++ nbClients;
        System.out.println("je suis dans client conversation");

        // On compte le nb de clients.
        // On ne va pas mettre le code de la conversation ici sinon on empêcherait le Serveur d'accepter de
        // nouvelles connections. Le seul moyen est de démarrer un nouveau thread qui va s'occuper des conversations.
        Conversation c = new Conversation(socket, nbClients, this);

        // TODO libérer thread à la fin de la convo
        c.start(); // Si Conversation hérite de thread à chaque fois qu'on start on lance un thread qui s'occupe de la conversation.
        connectedClients.add(c);
    }

    public void broadCast(String message, int[] numeroClients){
        /**
         * Boucle sur toutes les conversations pour trouver les clients spécifiés pour leur envoyer le message
         */
        // TODO envoyer erreur si le client n'existe pas
        try {
            for (Conversation c:connectedClients) { // Boucle sur les conversations -> TODO Optimiser

                for (int i=0; i<numeroClients.length; i++){ // Boucle sur les destinataires
                    if (c.getNumeroClient()==numeroClients[i]) {
                        PrintWriter pw = new PrintWriter(c.getSocket().getOutputStream(), true);
                        pw.println(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnectButton() {
       exterminateAll();
    }

    public void exterminateAll(){
        try {
            listener.onClose();
            running = false;
            if (connectionController != null) {
                connectionController.shutdownTread();
            }
            serverSocket.close();
            interuptConversation();
            stage.hide();
            System.out.println("i've killed everything that exists");
        }catch (Exception ignore){
            // do nothing
        }
    }


    public interface ServerListener {
        void onClose();
    }
}
