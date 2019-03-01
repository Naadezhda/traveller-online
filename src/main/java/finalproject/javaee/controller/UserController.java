package finalproject.javaee.controller;

import finalproject.javaee.dto.ViewUserProfileDTO;
import finalproject.javaee.dto.userDTO.UserLoginDTO;
import finalproject.javaee.model.dao.UserDao;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceprions.*;
import finalproject.javaee.model.util.exceprions.usersRegistrationExcepions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class UserController extends BaseController {


    @Autowired
    private UserRepository userRepository;
    private UserDao userDao;

    @PostMapping(value = "/register")
    public User userRegistration(@RequestBody User user) throws RegistrationException, IOException {
        //TODO DTO
        validateUsername(user.getUsername());
        validatePassword(user.getPassword().trim(), user.getVerifyPassword().trim());
        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
        validateEmail(user.getEmail());
        validateGender(user.getGender());
        userRepository.save(user);
        return user;
    }

    @PostMapping(value = "/login")
    public UserLoginDTO userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpSession session) throws BaseException {
        User user = userRepository.findByUsername(userLoginDTO.getUsername());
        if (!isLoggedIn(session)) {
            validateUsernameAndPassword(userLoginDTO.getUsername(), userLoginDTO.getPassword().trim());
            session.setAttribute("User", user);
            session.setAttribute("Username", user.getUsername());
            return new UserLoginDTO(user.getUsername(), user.getPassword());
        } else {
            throw new UserLoggedInException();
        }
    }

    @PostMapping(value = "/logout")
    public void userLogout(HttpSession session) throws UserLoggedOutException {
        //TODO DTO
        if (!isLoggedIn(session)) {
            throw new UserLoggedOutException();
        }
        session.invalidate();
    }

    /* ************* Follow and Unfollow ************* */

    @GetMapping(value = "/follow/{id}")
    public void userFollow(@PathVariable("id") long id, HttpSession session) throws NotLoggedException, BaseException {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        User followingUser = userRepository.findById(id);
        if (isLoggedIn(session)) {
            if (userRepository.existsById(id)) {
                if (!user.getFollowing().contains(followingUser)) {
                    followingUser.getFollower().add(user);
                    user.getFollowing().add(followingUser);
                    userRepository.save(user);
                } else {
                    //TODO Exception for already followed
                    throw new BaseException("ALREADY FOLLOWED");
                }
            } else {
                //TODO Exception for does not exist
                throw new BaseException("User does not exist");
            }
        } else {
            throw new NotLoggedException();
        }
    }

    @DeleteMapping(value = "/unfollow/{id}")
    public void userUnfollow(@PathVariable("id") long id, HttpSession session) throws NotLoggedException,BaseException {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        User unfollowingUser = userRepository.findById(id);
        if (isLoggedIn(session)) {
            if (userRepository.existsById(id)) {
                if (user.getFollowing().contains(unfollowingUser)) {
                    unfollowingUser.getFollower().remove(user);
                    user.getFollowing().remove(unfollowingUser);
                    userRepository.save(user);
                } else {
                    //TODO Exception for not followed
                    throw new BaseException("You are not followed");
                }
            } else {
                //TODO Exception for does not exist
                throw new BaseException("User does not exist");
            }
        } else {
            throw new NotLoggedException();
        }
    }
    //TODO make view list of followers and following

    /* ************* Edit profile ************* */

    @GetMapping(value = "/profile")
    public ViewUserProfileDTO viewProfile(HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        if(isLoggedIn(session)) {
            return new ViewUserProfileDTO(user.getUsername(), user.getPhoto());
        }
        throw new NotLoggedException();
    }

    @GetMapping(value = "/profile/edit")
    public void editProfile(){

    }
    @PutMapping(value = "/profile/edit/password")
    public void editPassword(){

    }
    @PutMapping(value = "/profile/edit/email")
    public void editEmail(){

    }
    @PutMapping(value = "profile/edit/firstName")
    public void editFirstName(){

    }
    @PutMapping(value = "/profile/edit/lastName")
    public void editLastName(){

    }

    @DeleteMapping(value = "/profile/edit/delete")
    public void deleteProfile(){
        
    }

    /* ************* Validations ************* */

    public static boolean isLoggedIn(HttpSession session){
        return (!session.isNew() && session.getAttribute("Username") != null);
    }

    private void validateUsername(String username)throws RegistrationException {
        if(username == null || username.isEmpty()){
            throw new InvalidUsernameException();
        }
        if(userRepository.findByUsername(username) != null){
            throw new UsernameExistException();
        }
    }

    private void validatePassword(String password, String verifyPassword) throws RegistrationException {
        if((password == null || verifyPassword ==null)||(password.isEmpty() || verifyPassword.isEmpty()) ||
                (password.length()<6 || verifyPassword.length() <6) ){
            throw new InvalidPasswordException();
        }
        if(!password.equals(verifyPassword)){
            throw new MismatchPasswordException();
        }
    }

    private void validateFirstName(String firstName) throws RegistrationException {
        if(firstName == null || firstName.isEmpty()){
            throw new InvalidFirstNameException();
        }
    }
    private void validateLastName(String lastName) throws RegistrationException {
        if(lastName == null || lastName.isEmpty()){
            throw new InvalidLastNameException();
        }
    }

    private void validateEmail(String email) throws RegistrationException {
        try {
            if(email != null) {
                InternetAddress internetAddress = new InternetAddress(email);
                internetAddress.validate();
            }
            else {
                throw new InvalidEmailException();
            }
        } catch (AddressException e) {
            throw new InvalidEmailException();
        }
        if(userRepository.findByEmail(email) != null){
            throw new EmailExistException();
        }
    }

    private void validateGender(String gender) throws RegistrationException{
        if(gender == null ||!(gender.equalsIgnoreCase(("M")) || gender.equalsIgnoreCase(("F")))){
            throw new InvalidGenderException();
        }
    }

    private void validateUsernameAndPassword(String username, String password) throws InvalidLoginException {
        if((username == null || password == null)||(username.isEmpty() || password.isEmpty())){
            throw new InvalidLoginException();
        }
        else{
            User user = userRepository.findByUsername(username);
            if(user == null || !user.getPassword().equals(password)){
                throw new InvalidLoginException();
            }
        }
    }

    public long getLoggedUserByIdSession(HttpSession session)throws NotLoggedException {
        User user = ((User)(session.getAttribute("User")));
        if(isLoggedIn(session) || user != null){
            return  ((User)(session.getAttribute("User"))).getId();
        }
        throw new NotLoggedException();
    }


    public User getUserById(long id){
        return getUserById(id);
    }

}
