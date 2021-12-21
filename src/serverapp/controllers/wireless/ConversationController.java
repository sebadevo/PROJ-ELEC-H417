package serverapp.controllers.wireless;


import serverapp.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static serverapp.MainServer.DELIMITER;

public class ConversationController extends Thread {


    private final ConversationListener listener;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private User user;
    private Socket socket;
    private boolean running = true;

    public ConversationController(ConversationListener listener, User user, Socket socket) {
        this.user = user;
        this.listener = listener;
        this.socket = socket;
        try {
            printWriter = new PrintWriter(this.socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (Exception ignore){}
    }

    @Override
    public void run(){
        String encrypted;
        while(running){
            try {
                System.out.println("je suis dans la conversation");
                if((encrypted=bufferedReader.readLine()) != null){
                    String[] splitmessage = encrypted.split(DELIMITER);
                    if (splitmessage[0].equals("exit")){
                        System.out.println("received message of disconnection");
                        disconnect();
                        return;
                    }
                    else if (splitmessage[0].equals("key")){
                        String destinataire = splitmessage[1];
                        String ga = splitmessage[2];
                        listener.keyExchange(ga, user, destinataire);
                    }
                    String destinataire = splitmessage[0];
                    String message = splitmessage[1];
                    listener.sendMessage(message, user, destinataire);
                }
            } catch (IOException ignore) {}
        }
        try {
            socket.close();
        } catch (IOException ignore) {}
    }

    public void forceShutDown(){
        running = false;
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    private void disconnect(){
        running = false;
        printWriter.println("disconnected");
        listener.disconnectUser(user,socket);
    }

    public PrintWriter getPrintWrinter(){
        return printWriter;
    }

    public User getUser(){
        return user;
    }




    public interface ConversationListener {
        void keyExchange(String ga, User user, String destination);
        void disconnectUser(User user, Socket socket);
        void sendMessage(String Message, User source, String destination);

    }
}
