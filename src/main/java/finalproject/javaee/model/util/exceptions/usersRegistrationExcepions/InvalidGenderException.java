package finalproject.javaee.model.util.exceptions.usersRegistrationExcepions;

public class InvalidGenderException extends RegistrationException {

    public InvalidGenderException() {
        super("Invalid gender input!");
    }
}
