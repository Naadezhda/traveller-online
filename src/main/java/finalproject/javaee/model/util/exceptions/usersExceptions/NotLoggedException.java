package finalproject.javaee.model.util.exceptions.usersExceptions;

import finalproject.javaee.model.util.exceptions.BaseException;

public class NotLoggedException extends BaseException {

    public NotLoggedException() {
        super("You are not logged!");
    }
}
