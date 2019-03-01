package finalproject.javaee.model.util.exceprions.usersExceptions;

import finalproject.javaee.model.util.exceprions.BaseException;

public class UserExistException extends BaseException {


    public UserExistException() {
        super("User does not exist!");
    }
}
