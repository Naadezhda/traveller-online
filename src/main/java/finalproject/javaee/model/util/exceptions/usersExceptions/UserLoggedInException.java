package finalproject.javaee.model.util.exceptions.usersExceptions;

import finalproject.javaee.model.util.exceptions.BaseException;

public class UserLoggedInException extends BaseException {

    public UserLoggedInException() {
        super("User already logged in.");
    }
}
