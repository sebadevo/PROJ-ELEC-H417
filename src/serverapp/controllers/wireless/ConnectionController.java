package serverapp.controllers.wireless;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import serverapp.models.User;
import serverapp.models.databases.UserDatabase;
import serverapp.models.databases.exceptions.DatabaseSaveException;

import static serverapp.models.User.checkSyntax;

public class ConnectionController extends Thread{

    private static final String DELIMITER = "-";
    private final ConnectionListener listener;
    private final Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private boolean running = true;

    public ConnectionController(ConnectionListener listener, Socket socket) {
        this.listener = listener;
        this.socket = socket;
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
        System.out.println(firstname +" "+ lastname +" "+ username +" "+ email +" "+ password);
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
                    listener.clientConversation(socket);
                    shutdownTread();
                }
            } else if (UserDatabase.getInstance().checkExistingEmail(user.getEmailAddress())){
                printWriter.println("Email already taken");

            } else {
                printWriter.println("Username already taken");
            }
        } else {
            printWriter.println("Wrong email address or empty fields");
        }
    }

    public void disconnectUser(User user, String username){
        //if(UserDatabase.getInstance().getUser(username) == user.getUsername()){
        //
//
        //}
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
                listener.clientConversation(socket);
                shutdownTread();
            } else {
                sendErrorMessage("Wrong username or password");
            }
        }else{
            sendErrorMessage("User already connected");
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
                if(completeMessage != null && !completeMessage.equals("exit")){
                    String[] message = completeMessage.split(DELIMITER);  // séparer le message des destinataires
                    if (message[0].equals("1")){
                        createNewUser(message);
                    }else if (message[0].equals("0")){
                        checkValidConnect(message);
                    }
                    else {
                        System.out.println("the user has sent something unexpected, it has thus been ignored");
                    }
                }else{
                    running = false;
                }
            }
        } catch (IOException ignored) {
        }
    }

    public void shutdownTread(){
        running = false;
        try {
            socket.close();
        } catch (IOException ignored) {}
    }


    public interface ConnectionListener {
        void clientConversation(Socket socket);
        void addClientConversation(User user, Socket socket);
    }
}
