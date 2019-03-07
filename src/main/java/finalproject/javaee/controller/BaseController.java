package finalproject.javaee.controller;

import finalproject.javaee.model.pojo.ErrorMessage;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.util.exceptions.usersExceptions.ExistException;
import finalproject.javaee.util.exceptions.usersExceptions.NotLoggedException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@RequestMapping(produces = "application/json")
public abstract class BaseController {

    static Logger logger = Logger.getLogger(BaseController.class.getName());

    @ExceptionHandler({NotLoggedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleNotLogged(NotLoggedException n){
        logger.error(n.getMessage());
        ErrorMessage message = new ErrorMessage(n.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({ExistException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleRegistrationErrors(ExistException x){
        logger.error(x.getMessage());
        ErrorMessage message = new ErrorMessage(x.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({BaseException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMyErrors(BaseException b){
        logger.error(b.getMessage());
        ErrorMessage message = new ErrorMessage(b.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return message;
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleOtherErrors(Exception e){
        logger.error(e.getMessage());
        ErrorMessage message = new ErrorMessage(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        return message;
    }

    protected void validateisLoggedIn(HttpSession session) throws NotLoggedException{
        if (session.isNew() && session.getAttribute("Username") == null){
            throw new NotLoggedException();
        }
    }

    public static String key() throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        String secureCode = secretKey.toString();
        return secureCode.substring(secureCode.length()-8,secureCode.length()-1);
    }
}
