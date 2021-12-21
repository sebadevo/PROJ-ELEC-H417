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

    private volatile boolean running = true;

    public ConnectionController(ConnectionListener listener, Socket socket) {
        this.listener = listener;
        this.socket = socket;
        running =true;
        try {
            printWriter = new PrintWriter(this.socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (Exception ignore){}
        this.start();
    }

    private void createNewUser(String[] message){
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
                    listener.addClientConversation(user, socket);  // TODO DEBUG
                    // TODO DEBUG
                    setRunning(false);
                }
            } else if (UserDatabase.getInstance().checkExistingEmail(user.getEmailAddress())){
                printWriter.println("Email already taken");
                setRunning(true);
            } else {
                printWriter.println("Username already taken");
                setRunning(true);
            }
        } else {
            printWriter.println("Wrong email address or empty fields");
            setRunning(true);
        }
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


    private void checkValidConnect(String[] message) {
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
                listener.addClientConversation(user, socket);
                setRunning(false);
            } else {
                sendErrorMessage("Wrong username or password");
                setRunning(true);
            }
        }else{
            sendErrorMessage("User already connected");
            setRunning(true);
        }
    }

    private void sendErrorMessage(String message){
        printWriter.println(message);
    }

    @Override
    public void run() {
        try {
            while(running){
                String completeMessage=bufferedReader.readLine();
                if(completeMessage != null ) { // TODO see if this is useful :  && !completeMessage.equals("exit")
                    String[] message = completeMessage.split(DELIMITER);  // séparer le message des destinataires
                    switch (message[0]) {
                        case "1":
                            createNewUser(message);
                            break;
                        case "0":
                            checkValidConnect(message);
                            break;
                        case "exit()":
                            disconnectUser(message[1]);
                            break;
                        default:
                            System.out.println("the user has sent something unexpected, it has thus been ignored");
                            break;
                    }
                }else{
                    running =false;
                    return;
                }
            }
        } catch (Exception ignored) {}
    }


    public void setRunning(boolean bool){
        running = bool;
    }

    public void forceShutDown(){
        System.out.println("SHUTDOWN");
        running = false;
        try {
            socket.close();
        } catch (IOException ignored) {}
    }


    public interface ConnectionListener {
        void addClientConversation(User user, Socket socket);
    }
}
