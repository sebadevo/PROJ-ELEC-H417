package Server.views;

public class ServerViewController {

    private ServerViewListener listener;

    public void setListener(ServerViewListener listener) {
        this.listener = listener;
    }

    public interface ServerViewListener {

    }
}