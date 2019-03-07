package finalproject.javaee.util.exceptions.usersExceptions;

import finalproject.javaee.util.exceptions.BaseException;

public class NotLoggedException extends BaseException {

    public NotLoggedException() {
        super("You are not logged!");
    }
}
