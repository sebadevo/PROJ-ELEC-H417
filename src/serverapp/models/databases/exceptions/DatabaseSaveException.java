package serverapp.models.databases.exceptions;
import java.io.IOException;
public class DatabaseSaveException extends Throwable {
    public DatabaseSaveException(IOException e) {
        super(e);
    }
}
