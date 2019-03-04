package finalproject.javaee.model.util.exceptions.usersExceptions;

import finalproject.javaee.model.util.exceptions.BaseException;

public class UserLoggedOutException extends BaseException {

    public UserLoggedOutException() {
        super("User already logged out.");
    }
}
