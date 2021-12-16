package Client.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * La prmière implémentation de ce serveur n'était pas un véritable serveur car incapble de connecter pusieurs clients,
 * donc incapacité d'établir une conversation.
 * Le serveur permettant d'avoir un dialogue/conversation est la class ServeurMT.
 */
public class Server {
    public static void main(String[] args){
        try{
            ServerSocket ss = new ServerSocket(1234);
            System.out.println("J'attends la connexion d'un client");
            Socket s = ss.accept(); // Le Server attends que le client se connecte.
            // Quand un client se connecte on a directement la génération de l'objet socket.
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            System.out.println("J'attends un nombre");
            int nb = is.read();
            int rep = nb*8;
            System.out.println("J'envoie la réponse");
            os.write(rep);
            // Por déconnecter client et server:
            s.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
