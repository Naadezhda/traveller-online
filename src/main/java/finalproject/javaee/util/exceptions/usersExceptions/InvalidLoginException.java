package finalproject.javaee.util.exceptions.usersExceptions;

import finalproject.javaee.util.exceptions.BaseException;

public class InvalidLoginException extends BaseException {

    public InvalidLoginException() {
        super("Wrong username or password!");
    }
}
