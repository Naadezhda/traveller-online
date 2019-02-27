package finalproject.javaee.model.util.exceprions.usersRegistrationExcepions;

public class InvalidFirstNameException extends RegistrationException {

    public InvalidFirstNameException() {
        super("Invalid first name input!");
    }
}
