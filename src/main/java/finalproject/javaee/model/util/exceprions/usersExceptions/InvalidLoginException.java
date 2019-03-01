package finalproject.javaee.model.util.exceprions.usersExceptions;

import finalproject.javaee.model.util.exceprions.BaseException;

public class InvalidLoginException extends BaseException {

    public InvalidLoginException() {
        super("Wrong username or password!");
    }
}
