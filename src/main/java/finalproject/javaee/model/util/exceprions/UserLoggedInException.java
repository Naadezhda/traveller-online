package finalproject.javaee.model.util.exceprions;

public class UserLoggedInException extends BaseException {

    public UserLoggedInException() {
        super("User already logged in.");
    }
}
