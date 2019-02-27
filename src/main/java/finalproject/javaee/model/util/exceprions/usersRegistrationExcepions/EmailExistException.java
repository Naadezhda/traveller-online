package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class EmailExistException extends RegistrationException {

    public EmailExistException() {
        super("Email already exists.");
    }
}
