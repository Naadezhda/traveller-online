package finalproject.javaee.model.util.exceprions.usersExceptions;

import finalproject.javaee.model.util.exceprions.BaseException;

public class UserLoggedOutException extends BaseException {

    public UserLoggedOutException() {
        super("User already logged out.");
    }
}
