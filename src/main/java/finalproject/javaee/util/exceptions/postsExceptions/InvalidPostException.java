package finalproject.javaee.util.exceptions.postsExceptions;

import finalproject.javaee.util.exceptions.usersExceptions.ExistException;

public class InvalidPostException extends ExistException {

    public InvalidPostException(String message){
        super(message);
    }

}
