package finalproject.javaee.controller;

import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.dto.userDTO.UserInformationDTO;
import finalproject.javaee.dto.userDTO.UserLoginDTO;
import finalproject.javaee.dto.userDTO.*;
import finalproject.javaee.dto.userDTO.editUserProfileDTO.*;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.util.exceptions.usersExceptions.NotLoggedException;
import finalproject.javaee.util.exceptions.usersExceptions.*;
import finalproject.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@Controller
public class UserController extends BaseController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    @PostMapping(value = "/register")
    public RegisterDTO userRegistration(@RequestBody User user, HttpSession session) throws Exception {
        RegisterDTO registerDTO = userService.register(user);
        session.setAttribute("User", user);
        session.setAttribute("Username", user.getUsername());
        return registerDTO;
    }

    @RequestMapping(value = "/register/{userId}/{secureCode}")
    private MessageDTO completeRegister(@PathVariable("secureCode") String secureCode,
                                                @PathVariable("userId") long userId) throws Exception {
        userService.validateIfUserExist(userId);
        User user = userRepository.findById(userId);
        return userService.complete(user, secureCode);
    }

    @PostMapping(value = "/login")
    public UserLoginDTO userLogin(@RequestBody LoginDTO loginDTO, HttpSession session) throws BaseException {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (!isLoggedIn(session)) {
            userService.validateUsernameAndPassword(loginDTO.getUsername().trim(), loginDTO.getPassword().trim());
            if(user.isCompleted()) {
                session.setAttribute("User", user);
                session.setAttribute("Username", user.getUsername());
            }else {
                throw new InvalidInputException("Account is not activated.");
            }
        } else {
            throw new UserActivityException("User already logged in.");
        }
        return new UserLoginDTO(user.getId(), user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhoto(), user.getGender());
    }

    @PostMapping(value = "/logout")
    public MessageDTO userLogout(HttpSession session) throws BaseException {
        if (!isLoggedIn(session)) {
            throw new UserActivityException("User already logged out.");
        }
        session.invalidate();
        return new MessageDTO("Logout successful.");
    }

    /* ************* Follow and Unfollow ************* */

    @GetMapping(value = "/follow/{id}")
    public MessageDTO userFollow(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        return userService.followUser(user, id);
    }

    @DeleteMapping(value = "/unfollow/{id}")
    public MessageDTO userUnfollow(@PathVariable("id") long id, HttpSession session) throws BaseException {
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
    public MessageDTO editPassword(@RequestBody EditPasswordDTO editPasswordDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        MessageDTO messageDTO = userService.editPassword(user, editPasswordDTO);
        session.invalidate();
        return messageDTO;
    }

    @PutMapping(value = "/profile/edit/email")
    public MessageDTO editEmail(@RequestBody EditEmailDTO editEmailDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        return userService.editEmail(user, editEmailDTO);
    }

    @PutMapping(value = "profile/edit/firstName")
    public MessageDTO editFirstName(@RequestBody EditFirstNameDTO editFirstNameDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        return userService.editFirstName(user, editFirstNameDTO);
    }

    @PutMapping(value = "/profile/edit/lastName")
    public MessageDTO editLastName(@RequestBody EditLastNameDTO editLastNameDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        return userService.editLastName(user, editLastNameDTO);
    }

    @Transactional
    @DeleteMapping(value = "/profile/edit/delete")
    public UserInformationDTO deleteProfile(@RequestBody DeleteUserProfileDTO deleteUserProfileDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        userLogout(session);
        return userService.deleteUser(user, deleteUserProfileDTO);
    }

    public static boolean isLoggedIn(HttpSession session){
        return (!session.isNew() && session.getAttribute("Username") != null);
    }

    protected long getLoggedUserByIdSession(HttpSession session) throws BaseException {
        User user = ((User) (session.getAttribute("User")));
        if (isLoggedIn(session) || user != null) {
            if (user.isCompleted()) {
                return ((User) (session.getAttribute("User"))).getId();
            } else {
                throw new InvalidInputException("Account is not activated.");
            }
        } else {
            throw new NotLoggedException();
        }
    }
}
