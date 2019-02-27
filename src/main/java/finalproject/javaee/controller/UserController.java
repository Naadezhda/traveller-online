package finalproject.javaee.controller;

import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceprions.WrongDataExseption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/registration")
    public User userRegistration(@RequestBody User user, HttpServletResponse response) throws Exception {
        validateUsername(user.getUsername());
        validatePassword(user.getPassword(), user.getVerifyPassword());
        validateGender(user.getGender());
        if (!this.checkIfUserOrEmailExist(user) && user.getVerifyPassword().equals(user.getPassword()) && validateEmail(user.getEmail())) {
            if (validateFirstName(user.getFirstName())) {
                user.setFirstName(null);
            }
            if (validateLastName(user.getLastName())) {
                user.setLastName(null);
            }
        userRepository.save(user);
        response.sendRedirect("/login.html");
    }else{
        response.setStatus(HttpStatus.BAD_REQUEST.value());//400
            response.getWriter().append("Input matching password");
            return null;
    }
        return user;
    }

    @PostMapping(value = "/login")
    public void login(){

    }

     //TODO exceptions in validate methods
    private void validateUsername(String username)throws WrongDataExseption {
        if(username.isEmpty() && username != null ){
            throw new WrongDataExseption();
        }
    }

    private boolean validatePassword(String password, String verifyPassword) {
        return (!password.isEmpty() || !verifyPassword.isEmpty() || password != null || verifyPassword != null);

    }

    private boolean validateFirstName(String firstName) throws WrongDataExseption {
        if(firstName.isEmpty() && firstName != null){
            throw new WrongDataExseption();
        }
        return true;
    }
    private boolean validateLastName(String lastName) throws WrongDataExseption {
        if(lastName.isEmpty() && lastName != null){
            throw new WrongDataExseption();
        }
        return true;
    }

    private boolean validateEmail(String email) throws WrongDataExseption {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        } catch (AddressException e) {
            throw new WrongDataExseption();
        }
        return true;
    }

    private void validateGender(String gender) throws WrongDataExseption{
        if(!(gender.equalsIgnoreCase(("M")) || gender.equalsIgnoreCase(("F")))){
            throw new WrongDataExseption();
        }
    }

    private boolean checkIfUserOrEmailExist(User user) throws WrongDataExseption{
        if(userRepository.findByUsername(user.getUsername()) != null){
            throw new WrongDataExseption();
        }
        else if(userRepository.findByEmail(user.getEmail()) != null){
            throw new WrongDataExseption();
        }
        return false;
    }


    public User getUserById(long id){
        return getUserById(id);
    }

}
