package finalproject.javaee.model.util.exceptions.usersExceptions;

import finalproject.javaee.model.util.exceptions.ExistException;

public class UserExistException extends ExistException {

    public UserExistException() {
        super("User does not exist!");
    }

    public UserExistException(String message) {
        super(message);
    }
}
