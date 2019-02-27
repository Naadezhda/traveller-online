package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class InvalidUsernameException extends RegistrationException {

    public InvalidUsernameException() {
        super("Invalid username input!");
    }
}
