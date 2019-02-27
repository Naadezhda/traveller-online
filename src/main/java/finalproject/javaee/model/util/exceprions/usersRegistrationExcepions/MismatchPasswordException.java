package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class MismatchPasswordException extends RegistrationException {

    public MismatchPasswordException() {
        super("Passwords do not match.");
    }
}
