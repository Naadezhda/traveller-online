package finalproject.javaee.model.util.exceprions.postsExceptions;

import finalproject.javaee.model.util.exceprions.BaseException;

public class PostExistException extends BaseException {

    public PostExistException() {
        super("Post does not exist.");
    }
}
