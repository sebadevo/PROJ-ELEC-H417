package groupe1.models;

import java.io.*;
import java.net.Socket;

public class Conversation extends Thread {
    protected Socket socket;
    protected int numeroClient;
    protected ServeurMT server;
    /**
     * Tout le code ci-dessous s'éxécute de manière indépandante du reste du code.
     */
    public Conversation (Socket socket, int num, ServeurMT server){
        // super(); Utility ???
        this.socket = socket; // Permet de communiquer avec le client.
        this.numeroClient = num;
        this.server = server;
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
                String req;
                while((req= br.readLine()) != null){
                    String[] t = req.split("-");  // séparer le message des destinataires
                    String message = t[0];
                    String[] t2 = t[1].replace(" ", "").split(",");  // séparer les destinataires
                    //System.out.println("J'imprime la longueur de la liste: " + t2.length);
                    int[] numeroClients = new int[t2.length];
                    for(int i=0; i<t2.length; i++){
                        //System.out.println("J'imprime les éléments de la liste à l'index: " + i + ": t2[i] " + t2[i]);
                        numeroClients[i] = Integer.parseInt(t2[i]); // " 1"
                    }
                    server.broadCast(message, numeroClients); // envoie le message au destinataire spécifiés
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
