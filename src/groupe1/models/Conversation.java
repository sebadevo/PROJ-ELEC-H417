package groupe1.models;

import java.io.*;
import java.net.Socket;

public class Conversation extends Thread {
    private Socket socket;
    private int numeroClient;
    /**
     * Tout le code ci-dessous s'éxécute de manière indépandante du reste du code.
     */
    public Conversation (Socket socket, int num){
        // super(); Utility ???
        this.socket = socket; // Permet de communiquer avec le client.
        this.numeroClient = num;

    }
    @Override
    public void run() {
    // Code de la conversation.
        try {
            InputStream is = socket.getInputStream(); // Octets
            InputStreamReader isr = new InputStreamReader(is); // Octets qui forment un charactères
            BufferedReader br = new BufferedReader(isr); // caractères qui forment une phrase, pas de limite.

            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true); // true permet d'envoyer la donnée.

            // Quand est ce que cette opération se fait :
            // On récupère l'adresse IP du client.
            String IP = socket.getRemoteSocketAddress().toString();
            // Les adresses seront récupérées dans un log, on pourra savoir quelles machines se sont connectées.

            System.out.println("Connection du client numéro " + numeroClient + " IP= " +IP);
            pw.println("Bienvenue, vous êtes le client N° " + numeroClient); // Le serveur envoit au Client.

            // Conversation (Requête-Réponse), toute la conversation se fait avec la même socket pour cahque client.

            while(true){
                String req=br.readLine();// Envoit requête
                System.out.println(IP + "a envoyé  " + req);
                if(req != null) {
                    String rep = "Size= " + req.length();
                    pw.println(rep); // Envoit réponse
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
