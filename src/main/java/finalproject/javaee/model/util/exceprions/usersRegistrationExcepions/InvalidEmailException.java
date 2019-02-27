package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class InvalidEmailException extends RegistrationException {

    public InvalidEmailException() {
        super("Invalid email input!");
    }
}
