package Server;

import Server.models.Conversation;
import Server.controllers.ServerController;
import javafx.stage.Stage;

import javafx.application.Application;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainServer extends Application implements ServerController.ServerListener{
    int nbClients;
    private List<Conversation> connectedClients = new ArrayList<>(); // Liste de sockets.
    private ServerController serverController;
    private Stage stage;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        serverController = new ServerController(this, stage);
        try {
            serverController.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ServerSocket ss = new ServerSocket(1234);
            // Boucle infinie pour pouvoir connecter des clients à n'importe quel moment.
            while(true){
                Socket s = ss.accept();
                ++ nbClients; // On compte le nb de clients.
                // On ne va pas mettre le code de la conversation ici sinon on empêcherait le Serveur d'accepter de
                // nouvelles connections. Le seul moyen est de démarrer un nouveau thread qui va s'occuper des conversations.
                Conversation c = new Conversation(s, nbClients, this);

                // TODO libérer thread à la fin de la convo
                c.start(); // Si Conversation hérite de thread à chaque fois qu'on start on lance un thread qui s'occupe de la conversation.
                connectedClients.add(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    //public static void main(String[] args) {
    //    // On appel une instance de la classe ServeurMT
    //    // On crée automatiquement un thread qui va juste s'occuper de la connection des clients.
    //    new Main.start();
    //}

}
