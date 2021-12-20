package serverapp.models;

import serverapp.controllers.ServerController;

import java.io.*;
import java.net.Socket;

public class Conversation extends Thread {

    private static final String DELIMITER = "-";
    private Socket socket;
    private int numeroClient;
    private ServerController server;
    private boolean running = true;
    /**
     * Tout le code ci-dessous s'éxécute de manière indépandante du reste du code.
     */
    public Conversation (Socket socket, int num, ServerController server){
        this.socket = socket; // Permet de communiquer avec le client.
        this.numeroClient = num;
        this.server = server;
    }
    @Override
    public void run() {
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            String IP = socket.getRemoteSocketAddress().toString();

            System.out.println("Connection du client numero " + numeroClient + " IP= " +IP);
            pw.println("Bienvenue, vous etes le client : " + numeroClient); // Le serveur envoit au Client.


            while(running){
                String req;
                if((req= br.readLine()) != null){
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
                        //server.broadCast(message, numeroClients); // envoie le message au destinataire spécifiés
                    }
                    else if (req.equals("exit")){
                        socket.close();
                        System.out.println("the socket has been terminated");
                        running = false;
                    }
                    else {
                        int[] source = new int[] {numeroClient};
                        //server.broadCast("ERROR", source);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public void shutdownThread(){
        running = false;
        try {
            socket.close();
        }catch(Exception ignore){
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
