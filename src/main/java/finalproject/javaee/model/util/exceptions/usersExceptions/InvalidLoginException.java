package finalproject.javaee.model.util.exceptions.usersExceptions;

import finalproject.javaee.model.util.exceptions.BaseException;

public class InvalidLoginException extends BaseException {

    public InvalidLoginException() {
        super("Wrong username or password!");
    }
}
