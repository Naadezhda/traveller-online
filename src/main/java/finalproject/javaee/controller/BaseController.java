package finalproject.javaee.controller;

import finalproject.javaee.model.pojo.ErrorMessage;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.ExistException;
import finalproject.javaee.model.util.exceptions.usersExceptions.NotLoggedException;
import finalproject.javaee.model.util.exceptions.usersRegistrationExcepions.RegistrationException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@RequestMapping(produces = "application/json")
public abstract class BaseController {

    static Logger logger = Logger.getLogger(UserController.class.getName());

    @ExceptionHandler({NotLoggedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleNotLogged(NotLoggedException n){
        logger.error(n.getMessage(),n);
        ErrorMessage message = new ErrorMessage(n.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({RegistrationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleRegistrationErrors(RegistrationException r){
        logger.error(r.getMessage(),r);
        ErrorMessage message = new ErrorMessage(r.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({ExistException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleRegistrationErrors(ExistException x){
        logger.error(x.getMessage(),x);
        ErrorMessage message = new ErrorMessage(x.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({BaseException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMyErrors(BaseException b){
        logger.error(b.getMessage(),b);
        ErrorMessage message = new ErrorMessage(b.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleOtherErrors(Exception e){
        logger.error(e.getMessage(),e);
        ErrorMessage message = new ErrorMessage(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        return message;
    }

    protected void validateisLoggedIn(HttpSession session) throws NotLoggedException{
        if (session.isNew() && session.getAttribute("Username") == null){
            throw new NotLoggedException();
        }
    }
}
