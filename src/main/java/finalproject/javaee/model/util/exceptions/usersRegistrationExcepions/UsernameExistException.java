package finalproject.javaee.model.util.exceptions.usersRegistrationExcepions;

public class UsernameExistException extends RegistrationException {

    public UsernameExistException() {
        super("Username already exists.");
    }
}
