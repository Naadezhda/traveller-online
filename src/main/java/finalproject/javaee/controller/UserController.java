package finalproject.javaee.controller;

import finalproject.javaee.dto.userDTO.*;
import finalproject.javaee.dto.userDTO.editUserProfileDTO.*;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.CryptWithMD5;
import finalproject.javaee.model.util.MailUtil;
import finalproject.javaee.model.util.exceptions.*;
import finalproject.javaee.model.util.exceptions.usersExceptions.*;
import finalproject.javaee.model.util.exceptions.usersExceptions.InvalidLoginException;
import finalproject.javaee.model.util.exceptions.usersExceptions.UserLoggedInException;
import finalproject.javaee.model.util.exceptions.usersRegistrationExcepions.*;
import finalproject.javaee.service.PostService;
import finalproject.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController extends BaseController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    @PostMapping(value = "/register")
    public UserRegisterDTO userRegistration(@RequestBody User user, HttpSession session) throws BaseException, MessagingException {
        session.setAttribute("User", user);
        session.setAttribute("Username", user.getUsername());
        return userService.register(user);
    }

//    @PostMapping(value = "/register/complete")
//    private UserRegisterDTO completeRegisterWithMeil(User user,HttpSession session)throws BaseException {
//        if(user.getSecureCode() == ){
//            userRepository.save(user);
//            session.setAttribute("User", user);
//            session.setAttribute("Username", user.getUsername());
//            return new UserRegisterDTO(user.getId(),user.getUsername(),user.getFirstName(),
//                    user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
//
//        }
//        throw new BaseException("wywedohte greshen kod. Opitajte pak");
//    }

    @PostMapping(value = "/login")
    public UserLoginDTO userLogin(@RequestBody LoginDTO loginDTO, HttpSession session) throws BaseException {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (!isLoggedIn(session)) {
            userService.validateUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
            session.setAttribute("User", user);
            session.setAttribute("Username", user.getUsername());
        } else {
            throw new UserLoggedInException();
        }
        return new UserLoginDTO(user.getId(),user.getUsername(),user.getFirstName(),
                user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
    }

    @PostMapping(value = "/logout")
    public void userLogout(HttpSession session) throws BaseException {
        if (!isLoggedIn(session)) {
            throw new UserLoggedOutException();
        }
        session.invalidate();
    }

    /* ************* Follow and Unfollow ************* */

    @GetMapping(value = "/follow/{id}") //TODO return a message saying "You've followed successful"
    public void userFollow(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        userService.followUser(user, id);
    }

    @DeleteMapping(value = "/unfollow/{id}")
    public UserDTO userUnfollow(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        return userService.unfollowUser(user, id);
    }

    /* ************* Edit profile ************* */

    @GetMapping(value = "/profile")
    public ViewUserProfileDTO viewProfile(HttpSession session) throws Exception {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        return userService.viewProfile(user);
    }

    @PutMapping(value = "/profile/edit/password")
    public void editPassword(@RequestBody EditPasswordDTO editPasswordDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        userService.editPassword(user, editPasswordDTO);
    }

    @PutMapping(value = "/profile/edit/email")
    public void editEmail(@RequestBody EditEmailDTO editEmailDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        userService.editEmail(user, editEmailDTO);
    }

    @PutMapping(value = "profile/edit/firstName")
    public void editFirstName(@RequestBody EditFirstNameDTO editFirstNameDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        userService.editFirstName(user, editFirstNameDTO);
    }

    @PutMapping(value = "/profile/edit/lastName")
    public void editLastName(@RequestBody EditLastNameDTO editLastNameDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        userService.editLastName(user, editLastNameDTO);
    }

    @DeleteMapping(value = "/profile/edit/delete")
    public DeleteUserProfileDTO deleteProfile(@RequestBody DeleteUserProfileDTO deleteUserProfileDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        userLogout(session);
        return userService.deleteUser(user, deleteUserProfileDTO);
    }

    protected long getLoggedUserByIdSession(HttpSession session)throws NotLoggedException {
        User user = ((User)(session.getAttribute("User")));
        if(isLoggedIn(session) || user != null){
            return  ((User)(session.getAttribute("User"))).getId();
        }
        throw new NotLoggedException();
    }


    public static boolean isLoggedIn(HttpSession session){
        return (!session.isNew() && session.getAttribute("Username") != null);
    }




}
