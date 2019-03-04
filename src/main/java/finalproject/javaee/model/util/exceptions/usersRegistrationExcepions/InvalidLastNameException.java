package finalproject.javaee.model.util.exceptions.usersRegistrationExcepions;

public class InvalidLastNameException extends RegistrationException {

    public InvalidLastNameException() {
        super("Invalid last name input!");
    }
}
