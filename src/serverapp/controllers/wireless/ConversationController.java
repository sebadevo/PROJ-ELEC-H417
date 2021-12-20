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
        } catch (Exception ignore){
            System.out.println("CONVEVERSATION CONTROLLER " + ignore);
        }
    }

    @Override
    public void run(){
        String encrypted;
        while(running){
            try {
                if((encrypted=bufferedReader.readLine()) != null){
                    String[] splitmessage = encrypted.split(DELIMITER);
                    if (splitmessage.length == 2) {
                        String destinataire = splitmessage[0];
                        String message = splitmessage[1];
                        listener.sendMessage(message,user, destinataire);
                    }
                    else if (encrypted.equals("exit")){
                        disconnect();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        listener.disconnectUser(user);
        System.out.println("the socket has been terminated");
    }

    public PrintWriter getPrintWrinter(){
        return printWriter;
    }

    public User getUser(){
        return user;
    }




    public interface ConversationListener {
        void disconnectUser(User user);
        void sendMessage(String Message, User source, String destination);

    }
}
