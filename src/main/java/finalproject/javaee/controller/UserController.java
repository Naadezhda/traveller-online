package finalproject.javaee.controller;

import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.dto.userDTO.LoginDTO;
import finalproject.javaee.dto.userDTO.UserInformationDTO;
import finalproject.javaee.dto.userDTO.UserLoginDTO;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.usersExceptions.NotLoggedException;
import finalproject.javaee.model.util.exceptions.usersExceptions.UserLoggedInException;
import finalproject.javaee.model.util.exceptions.usersExceptions.UserLoggedOutException;
import finalproject.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class UserController extends BaseController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @PostMapping(value = "/register")
    public MessageDTO userRegistration(@RequestBody User user, HttpSession session) throws Exception {
        session.setAttribute("User", user);
        session.setAttribute("Username", user.getUsername());
        return userService.register(user);
    }

    @RequestMapping(value = "/register/{userId}/{secureCode}")
    private UserInformationDTO completeRegister(@PathVariable("secureCode") String secureCode,
                                                @PathVariable("userId") long userId,
                                                HttpSession session) throws Exception {
        User user = userRepository.findById(userId);
        session.setAttribute("User", user);
        session.setAttribute("Username", user.getUsername());
        return userService.complete(user, secureCode, userId);
    }

    @PostMapping(value = "/login")
    public UserLoginDTO userLogin(@RequestBody LoginDTO loginDTO, HttpSession session) throws BaseException {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (!isLoggedIn(session)) {
            if (user.isCompleted()) {
                userService.validateUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
                session.setAttribute("User", user);
                session.setAttribute("Username", user.getUsername());
            } else throw new BaseException("Verify email address.");
        } else {
            throw new UserLoggedInException();
        }
        return new UserLoginDTO(user.getId(), user.getUsername(), user.getFirstName(),
                user.getLastName(), user.getEmail(), user.getPhoto(), user.getGender());
    }

    @PostMapping(value = "/logout")
    public MessageDTO userLogout(HttpSession session) throws BaseException {
        if (!isLoggedIn(session)) {
            throw new UserLoggedOutException();
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

    public static boolean isLoggedIn(HttpSession session){
        return (!session.isNew() && session.getAttribute("Username") != null);
    }

    protected long getLoggedUserByIdSession(HttpSession session) throws BaseException {
        User user = ((User) (session.getAttribute("User")));
        if (isLoggedIn(session) || user != null) {
            if (user.isCompleted()) {
                return ((User) (session.getAttribute("User"))).getId();
            } else {
                throw new BaseException("Account is not activated.");
            }
        }else throw new NotLoggedException();
    }
}
