package finalproject.javaee.model.util.exceptions.usersExceptions;

import finalproject.javaee.model.util.exceptions.BaseException;

public class WrongPasswordInputException extends BaseException {

    public WrongPasswordInputException() {
        super("Invalid password input exception.");
    }
}
