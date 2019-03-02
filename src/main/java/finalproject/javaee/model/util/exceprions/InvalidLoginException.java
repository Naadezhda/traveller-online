package finalproject.javaee.model.util.exceprions;

public class InvalidLoginException extends BaseException {

    public InvalidLoginException() {
        super("Wrong username or password!");
    }
}
