package finalproject.javaee.model.util.exceprions;

public class UserLoggedOutException extends BaseException {

    public UserLoggedOutException() {
        super("User already logged out.");
    }
}
