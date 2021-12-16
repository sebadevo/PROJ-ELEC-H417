package Client.models.databases.exceptions;

public class DatabaseLoadException extends Throwable {
    public DatabaseLoadException(Exception e) {
        super(e);
    }
}
