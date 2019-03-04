package finalproject.javaee.model.util.exceptions.usersRegistrationExcepions;

public class InvalidUsernameException extends RegistrationException {

    public InvalidUsernameException() {
        super("Invalid username input!");
    }
}
