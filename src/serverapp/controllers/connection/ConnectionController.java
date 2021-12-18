package serverapp.controllers.connection;

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
        this.start();
        try {
            printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (Exception ignore){}
    }

    // source : moi Sébastien le Grand.
    public void createNewUser(String[] message){
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
                try {
                    UserDatabase.getInstance().save();
                } catch (DatabaseSaveException e) {
                    System.out.println("Error saving user to database");
                }
                printWriter.println(true);
                listener.clientConversation(socket);
                shutdownTread();

            } else if (UserDatabase.getInstance().checkExistingEmail(user.getEmailAddress())){
                System.out.println("Email already taken"); // TODO envoye message au client comme quoi l'adresse email est déjà prise.

            } else {
                System.out.println("Username already taken"); // TODO envoye message au client comme quoi le username est déjà pris.
            }
        } else {
            System.out.println("Wrong email address or empty fields"); // TODO erreur de syntax a envoyer au client
        }
    }

    private void checkValidConnect(String[] message) {
        String username = message[1];
        String password = message[2];
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
    }

    public void sendErrorMessage(String message){
        printWriter.println(message);
    }

    @Override
    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(running){

                String completeMessage=bufferedReader.readLine();
                if(completeMessage != null && !completeMessage.equals("exit")){
                    String[] message = completeMessage.split(DELIMITER);  // séparer le message des destinataires
                    if (message[0].equals("1")){
                        createNewUser(message);
                    }else if (message[0].equals("0")){
                        checkValidConnect(message);
                    }else {
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
    }
}
