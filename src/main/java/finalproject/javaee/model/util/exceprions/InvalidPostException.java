package finalproject.javaee.model.util.exceprions;

public class InvalidPostException extends BaseException {

    public InvalidPostException(){
        super("There is no post with such an id!");
    }

}
