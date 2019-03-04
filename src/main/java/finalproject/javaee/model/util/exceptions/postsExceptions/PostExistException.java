package finalproject.javaee.model.util.exceptions.postsExceptions;

import finalproject.javaee.model.util.exceptions.ExistException;

public class PostExistException extends ExistException {

    public PostExistException() {
        super("Post does not exist.");
    }
}
