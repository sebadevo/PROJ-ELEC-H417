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

    /**
     * Constuctor receives it's lister and the socket of the client
     * @param listener listener
     * @param socket client socket
     */
    public ConnectionController(ConnectionListener listener, Socket socket) {
        this.listener = listener;
        this.socket = socket;
        try {
            printWriter = new PrintWriter(this.socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (Exception ignore){}
        this.start();
    }

    /**
     * Is called when a user tries to register (so when creating a new account). It will verify if the information sent
     * are correct, such that no user with the same usernam can exist.
     * If all the information are valid, the server will then notify the client that he can login and will Call the
     * AndClientConversation and set the running thread to false.
     * If some information the user sent are not valid, the server will notify the error to the client.
     * @param message the information sent by the client.
     */
    private void createNewUser(String[] message){
        String firstname = message[1];
        String lastname = message[2];
        String username = message[3];
        String email = message[4];
        String password = message[5];
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
                    listener.addClientConversation(user, socket);
                    setRunning(false);
                }
            } else if (UserDatabase.getInstance().checkExistingEmail(user.getEmailAddress())){
                sendErrorMessage("Email already taken");
                setRunning(true);
            } else {
                sendErrorMessage("Username already taken");
                setRunning(true);
            }
        } else {
            sendErrorMessage("Wrong email address or empty fields");
            setRunning(true);
        }
    }

    /**
     * Is called when a user tries to login. It will verify if the information sent are correct, the password
     * corresponds to the username.
     * If all the information are valid, the server will then notify the client that he can login and will Call the
     * AndClientConversation and set the running thread to false.
     * If some information the user sent are not valid, the server will notify the error to the client.
     * @param message the information sent by the client.
     */
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

    /**
     * Send the error message to the user
     * @param message error message
     */
    private void sendErrorMessage(String message){
        printWriter.println(message);
    }

    /**
     * Will cycle through the loop until the client has provided valid information or will stop if the user disconnects.
     */
    @Override
    public void run() {
        try {
            while(running){
                String completeMessage=bufferedReader.readLine();
                if(completeMessage != null ) {
                    String[] message = completeMessage.split(DELIMITER);
                    switch (message[0]) {
                        case "1":
                            createNewUser(message);
                            break;
                        case "0":
                            checkValidConnect(message);
                            break;
                        default:
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

    /**
     * Forces the socket to close.
     */
    public void forceShutDown(){

        running = false;
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    public interface ConnectionListener {
        void addClientConversation(User user, Socket socket);
    }
}
