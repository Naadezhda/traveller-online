package finalproject.javaee.model.util.exceptions.usersRegistrationExcepions;

public class InvalidFirstNameException extends RegistrationException {

    public InvalidFirstNameException() {
        super("Invalid first name input!");
    }
}
