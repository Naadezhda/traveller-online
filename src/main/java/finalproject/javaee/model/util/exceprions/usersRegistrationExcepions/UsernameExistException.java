package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class UsernameExistException extends RegistrationException {

    public UsernameExistException() {
        super("Username already exists.");
    }
}
