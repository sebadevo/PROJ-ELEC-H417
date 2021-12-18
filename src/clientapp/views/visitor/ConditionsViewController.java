package clientapp.views.visitor;

public class ConditionsViewController {

    private ConditionsViewListener listener;

    public void setListener(ConditionsViewListener listener) {
        this.listener = listener;
    }

    public void onBackButton() {
        listener.onBackButton();
    }

    public interface ConditionsViewListener {
        void onBackButton();
    }
}




