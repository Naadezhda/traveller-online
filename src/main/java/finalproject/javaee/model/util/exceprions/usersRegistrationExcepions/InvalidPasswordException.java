package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class InvalidPasswordException extends RegistrationException {

    public InvalidPasswordException() {
        super("Password must be at least six symbols!");
    }
}
