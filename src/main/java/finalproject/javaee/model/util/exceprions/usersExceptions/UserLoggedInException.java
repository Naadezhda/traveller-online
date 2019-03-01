package finalproject.javaee.model.util.exceprions.usersExceptions;

import finalproject.javaee.model.util.exceprions.BaseException;

public class UserLoggedInException extends BaseException {

    public UserLoggedInException() {
        super("User already logged in.");
    }
}
