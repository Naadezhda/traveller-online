package finalproject.javaee.model.util.exceptions.usersRegistrationExcepions;

public class MismatchPasswordException extends RegistrationException {

    public MismatchPasswordException() {
        super("Passwords do not match.");
    }
}
