package serverapp.views;

public class ServerViewController {
    private ServerViewListener listener;

    public void setListener(ServerViewListener listener){this.listener = listener;}

    public void onDisconnectButton() {
        listener.onDisconnectButton();
    }

    public interface ServerViewListener{
        void onDisconnectButton();
    }
}
