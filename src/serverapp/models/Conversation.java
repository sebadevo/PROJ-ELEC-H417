package serverapp.models;

import serverapp.controllers.ServerController;

import java.io.*;
import java.net.Socket;

public class Conversation extends Thread {
    private Socket socket;
    private int numeroClient;
    private ServerController server;
    /**
     * Tout le code ci-dessous s'éxécute de manière indépandante du reste du code.
     */
    public Conversation (Socket socket, int num, ServerController server){
        // super(); Utility ???
        this.socket = socket; // Permet de communiquer avec le client.
        this.numeroClient = num;
        this.server = server;
    }
    @Override
    public void run() {
        // Code de la conversation.
        try {
            //InputStream is = socket.getInputStream(); // Octets
            //InputStreamReader isr = new InputStreamReader(is); // Octets qui forment un charactères
            //BufferedReader br = new BufferedReader(isr); // caractères qui forment une phrase, pas de limite.
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //OutputStream os = socket.getOutputStream();
            //PrintWriter pw = new PrintWriter(os, true); // true permet d'envoyer la donnée.
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            // Quand est ce que cette opération se fait :
            // On récupère l'adresse IP du client.
            String IP = socket.getRemoteSocketAddress().toString();
            // Les adresses seront récupérées dans un log, on pourra savoir quelles machines se sont connectées.

            System.out.println("Connection du client numero " + numeroClient + " IP= " +IP);
            pw.println("Bienvenue, vous etes le client : " + numeroClient); // Le serveur envoit au Client.

            // Conversation (Requête-Réponse), toute la conversation se fait avec la même socket pour cahque client.
            while(true){
                String req;
                while((req= br.readLine()) != null){
                    String[] t = req.split("-");  // séparer le message des destinataires
                    if (t.length == 2) {
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
                    else if ("exit".equalsIgnoreCase(br.readLine())){
                        socket.close();
                    }
                    else {
                        int[] source = new int[] {numeroClient};
                        server.broadCast("ERROR", source);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
    public int getNumeroClient() {
        return numeroClient;
    }

    public ServerController getServer() {
        return server;
    }

}
