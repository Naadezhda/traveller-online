package finalproject.javaee.controller;

import finalproject.javaee.model.pojo.ErrorMessage;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceprions.BaseException;
import finalproject.javaee.model.util.exceprions.NotLoggedException;
import finalproject.javaee.model.util.exceprions.WrongDataExseption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
public abstract class BaseController {

    @Autowired
    private UserRepository userRepository;

    @ExceptionHandler({NotLoggedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleNotLogged(Exception e){
        ErrorMessage message = new ErrorMessage(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({WrongDataExseption.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleDataErrors(Exception e){
        ErrorMessage message = new ErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({BaseException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMyErrors(Exception e){
        ErrorMessage message = new ErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleOtherErrors(Exception e){
        ErrorMessage message = new ErrorMessage(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        return message;
    }

    protected void validateLogin(HttpSession session) throws NotLoggedException{
        if(session.getAttribute("loggedUser")== null){
            throw new NotLoggedException();
        }
    }


}
