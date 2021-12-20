package serverapp.controllers.wireless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import serverapp.models.User;
import serverapp.models.databases.UserDatabase;
import serverapp.models.databases.exceptions.DatabaseSaveException;


import static serverapp.MainServer.DELIMITER;

import static serverapp.models.User.checkSyntax;

public class ConnectionController extends Thread{

    private final ConnectionListener listener;
    private final Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private volatile boolean running = false;

    public ConnectionController(ConnectionListener listener, Socket socket) {
        this.listener = listener;
        this.socket = socket;
        try {
            printWriter = new PrintWriter(this.socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (Exception ignore){}
        this.start();
    }

    private boolean createNewUser(String[] message){
        String firstname = message[1];
        String lastname = message[2];
        String username = message[3];
        String email = message[4];
        String password = message[5];
        System.out.println("Voici les infos du user:" + firstname +" "+ lastname +" "+ username +" "+ email +" "+ password);
        if (checkSyntax(firstname, lastname, username, email, password)) {
            User user = new User(firstname, lastname, username, email, password);
            if (!UserDatabase.getInstance().checkExistingUser(user)) {
                UserDatabase.getInstance().add(user);
                if (UserDatabase.getInstance().logIn(username, password)) {
                    try {
                        UserDatabase.getInstance().save();
                    } catch (DatabaseSaveException e) {
                        System.out.println("Error saving user to database");
                    }
                    printWriter.println(true);

                    setRunning(false);
                    listener.addClientConversation(user, socket);
                    // TODO DEBUG
                    return true;
                }
            } else if (UserDatabase.getInstance().checkExistingEmail(user.getEmailAddress())){
                printWriter.println("Email already taken");
            } else {
                printWriter.println("Username already taken");
            }
        } else {
            printWriter.println("Wrong email address or empty fields");
        }
        return false;
    }

    public void disconnectUser(String username){
        User user = UserDatabase.getInstance().getUser(username);
        if(user.getUsername().equals(username)){
            printWriter.println("dce");
        }
        else{
            System.out.println("Error can't log out"); //TODO, deal with the error.
        }
    }


    private boolean checkValidConnect(String[] message) {
        String username = message[1];
        String password = message[2];
        if (!UserDatabase.getInstance().alreadyConnected(username)){
            if (UserDatabase.getInstance().logIn(username, password)) {
                try {
                    UserDatabase.getInstance().save();
                } catch (DatabaseSaveException e) {
                    System.out.println("Error saving user to database");
                }
                User user = UserDatabase.getInstance().getUser(username);
                printWriter.println(true);
                printWriter.println(user.getFirstname() + DELIMITER + user.getLastname() + DELIMITER + user.getEmailAddress());
                setRunning(false);
                listener.addClientConversation(user, socket);
                return true;
            } else {
                sendErrorMessage("Wrong username or password");
                return false;
            }
        }else{
            sendErrorMessage("User already connected");
            return false;
        }
    }

    private void sendErrorMessage(String message){
        printWriter.println(message);
    }

    @Override
    public void run() {
        running = true;
        try {
            int i = 0;
            while(running){
                System.out.println("Running :" + running);
                System.out.println("Je compte :" + i);
                i++;
                String completeMessage=bufferedReader.readLine();
                System.out.println("Voici ce que le client envoit :" + completeMessage); // DEBUG
                if(completeMessage != null ) { // TODO see if this is useful :  && !completeMessage.equals("exit")
                    String[] message = completeMessage.split(DELIMITER);  // séparer le message des destinataires
                    if (message[0].equals("1")) {
                        running = !createNewUser(message);
                        running = false;
                    } else if (message[0].equals("0")) {
                        running = !checkValidConnect(message);
                        running = false;
                    } else if (message[0].equals("exit()")) {
                        System.out.println("DECONNECTION");
                        disconnectUser(message[1]);
                        running = false;
                    } else {
                        System.out.println("the user has sent something unexpected, it has thus been ignored");
                    }
                }
                else {
                    running = false;
                }
                System.out.println("Running end :" + running);

            }
        } catch (IOException ignored) { // todo exception
        }
    }

    public void setRunning(boolean bool){
        System.out.println("Set Running to " + bool);
        //running = bool;
    }

    public void forceShutDown(){
        System.out.println("SHUTDOWN");
        //running = false;
        try {
            socket.close();
        } catch (IOException ignored) {}
    }


    public interface ConnectionListener {

        void addClientConversation(User user, Socket socket);
    }
}
