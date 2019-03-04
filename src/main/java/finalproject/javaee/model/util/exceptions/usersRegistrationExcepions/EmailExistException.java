package finalproject.javaee.model.util.exceptions.usersRegistrationExcepions;

public class EmailExistException extends RegistrationException {

    public EmailExistException() {
        super("Email already exists.");
    }
}
