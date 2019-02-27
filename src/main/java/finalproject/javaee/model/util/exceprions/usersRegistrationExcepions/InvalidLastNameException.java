package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class InvalidLastNameException extends RegistrationException {

    public InvalidLastNameException() {
        super("Invalid last name input!");
    }
}
