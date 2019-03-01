package finalproject.javaee.model.util.exceprions.usersExceptions;

import finalproject.javaee.model.util.exceprions.BaseException;

public class WrongPasswordInputException extends BaseException {

    public WrongPasswordInputException() {
        super("Invalid password input exception.");
    }
}
