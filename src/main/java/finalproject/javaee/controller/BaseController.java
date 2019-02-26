package finalproject.javaee.controller;

import finalproject.javaee.model.pojo.ErrorMessage;
import finalproject.javaee.model.util.exceprions.NotLoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.ErrorManager;
import java.util.logging.Logger;

@RestController
public abstract class BaseController {

    @ExceptionHandler({NotLoggedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleNotLogged(Exception e){
        ErrorMessage message = new ErrorMessage();


    }
}
