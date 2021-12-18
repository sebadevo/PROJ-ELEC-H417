package serverapp.controllers;


public class ServerController {
    private final ServerListener listener;

    public ServerController(ServerListener listener) {
        this.listener = listener;

    }



    public interface ServerListener {
    }
}
