package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class InvalidGenderException extends RegistrationException {

    public InvalidGenderException() {
        super("Invalid gender input!");
    }
}
