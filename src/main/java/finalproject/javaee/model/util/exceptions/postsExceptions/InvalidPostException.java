package finalproject.javaee.model.util.exceptions.postsExceptions;

import finalproject.javaee.model.util.exceptions.usersExceptions.ExistException;

public class InvalidPostException extends ExistException {

    public InvalidPostException(String message){
        super(message);
    }

}
