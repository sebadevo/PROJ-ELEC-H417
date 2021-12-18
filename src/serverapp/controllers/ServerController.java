package serverapp.controllers;

import serverapp.models.Conversation;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;



public class ServerController {
    private final ServerListener listener;
    int nbClients;
    private List<Conversation> connectedClients = new ArrayList<>(); // Liste de sockets.
    private ServerSocket serverSocket;


    public ServerController(ServerListener listener) {
        this.listener = listener;
    }

    public void startServer(){
        try {
            serverSocket = new ServerSocket(4321);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addConnection();
    }

    public void addConnection(){
        while(true){
            try {
                Socket socket = serverSocket.accept();
                clientConversation(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }






    public void clientConversation(Socket socket){
        ++ nbClients;

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



    public interface ServerListener {
    }
}
