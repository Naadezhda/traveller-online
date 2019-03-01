package finalproject.javaee.model.util.exceprions.usersExceptions;

import finalproject.javaee.model.util.exceprions.BaseException;

public class NotLoggedException extends BaseException {

    public NotLoggedException() {
        super("You are not logged!");
    }
}
