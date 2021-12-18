package serverapp.models.databases.exceptions;

public class DatabaseSaveException extends Throwable {
    public DatabaseSaveException(Exception e) {
        super(e);
    }
}
