package serverapp.controllers.wireless;


import serverapp.models.User;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ConversationController {
    private static final String DELIMITER = "-";
    private final ConversationListener listener;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private User user;
    private Socket socket;

    public ConversationController(ConversationListener listener, User user, Socket socket) {
        this.user = user;
        this.listener = listener;
        this.socket = socket;

    }

    public interface ConversationListener {

    }
}
