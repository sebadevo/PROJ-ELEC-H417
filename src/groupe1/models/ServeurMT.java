package groupe1.models;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMT extends Thread {

    int nbClients;


    @Override
    public void run(){
        try {
            ServerSocket ss = new ServerSocket(234);
            // Boucllle infinie pour pouvoir connecter des clients à n'importe quel moment.
            while(true){
                Socket s = ss.accept();
                ++ nbClients; // On compte le nb de clients.
                // On ne va pas mettre le code de la conversation ici sinon on empêcherait le Serveur d'accepter de
                // nouvelles connections. Le seul moyen est de démarrer un nouveau thread qui va s'occuper des conversations.
                new Conversation(s, nbClients).start(); // Si Conversation herite de thread à chaque fois qu'on start on lance un thread qui s'occupe de la conversation.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // On appel une instance de la classe ServeurMT
        // On crée automatiquement un thread qui va juste s'occuper de la connection des clients.
        new ServeurMT().start();

    }
}
