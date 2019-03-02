package finalproject.javaee.model.util.exceprions.postsExceptions;

import finalproject.javaee.model.util.exceprions.BaseException;

public class InvalidPostException extends BaseException {

    public InvalidPostException(){
        super("There is no post with such an id!");
    }

}
