package finalproject.javaee.model.util.exceptions.postsExceptions;

import finalproject.javaee.model.util.exceptions.ExistException;

public class InvalidPostException extends ExistException {

    public InvalidPostException(){
        super("There is no post with such an id!");
    }

}
