package finalproject.javaee.model.util.exceptions.usersRegistrationExcepions;

public class InvalidEmailException extends RegistrationException {

    public InvalidEmailException() {
        super("Invalid email input!");
    }
}
