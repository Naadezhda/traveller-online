package finalproject.javaee.controller;

import finalproject.javaee.dto.userDTO.*;
import finalproject.javaee.dto.userDTO.editUserProfileDTO.*;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.CryptWithMD5;
import finalproject.javaee.model.util.MailUtil;
import finalproject.javaee.model.util.exceprions.*;
import finalproject.javaee.model.util.exceprions.usersExceptions.*;
import finalproject.javaee.model.util.exceprions.usersExceptions.InvalidLoginException;
import finalproject.javaee.model.util.exceprions.usersExceptions.UserLoggedInException;
import finalproject.javaee.model.util.exceprions.usersRegistrationExcepions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController extends BaseController {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostController postController;

    @PostMapping(value = "/register")
    public UserRegisterDTO userRegistration(@RequestBody User user, HttpSession session) throws RegistrationException, IOException, MessagingException {
        validateUsername(user.getUsername());
        validatePassword(user.getPassword(),user.getVerifyPassword());
//        user.setVerifyPassword(user.getVerifyPassword());
        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
        validateEmail(user.getEmail());
        validateGender(user.getGender());
        userRepository.save(user);
        session.setAttribute("User", user);
        session.setAttribute("Username", user.getUsername());
        MailUtil mailUtil = new MailUtil();
        mailUtil.sendMail("nadejdab29@gmail.bg",user.getEmail(),"Confirm registration by email.","BODY");
        //TODO send code or file list
        return new UserRegisterDTO(user.getId(),user.getUsername(),user.getFirstName(),
                user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
    }

    @PostMapping(value = "/login")
    public UserLoginDTO userLogin(@RequestBody LoginDTO loginDTO, HttpSession session) throws BaseException {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (!isLoggedIn(session)) {
            validateUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
            session.setAttribute("User", user);
            session.setAttribute("Username", user.getUsername());
        } else {
            throw new UserLoggedInException();
        }
        return new UserLoginDTO(user.getId(),user.getUsername(),user.getFirstName(),
                user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
    }

    @PostMapping(value = "/logout")
    public void userLogout(HttpSession session) throws UserLoggedOutException {
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
                    throw new FollowException("Already followed.");
                }
            } else {
                throw new UserExistException();
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
                    throw new FollowException("User is not followed.");
                }
            } else {
                throw new UserExistException();
            }
        } else {
            throw new NotLoggedException();
        }
    }

    protected List<ViewUserRelationsDTO> getAllUserFollowers(User user) {
        List<User> follower = userRepository.findAllByFollowingId(user.getId());
        List<ViewUserRelationsDTO> userFollowerDTO = new ArrayList<>();
        for(User u : follower){
            userFollowerDTO.add(new ViewUserRelationsDTO(u.getId(),u.getFirstName(),u.getLastName()));
        }
        return userFollowerDTO;
    }
    protected List<ViewUserRelationsDTO> getAllUserFollowing(User user){
        List<User> following = userRepository.findAllByFollowerId(user.getId());
        List<ViewUserRelationsDTO> userFollowingDTO = new ArrayList<>();
        for (User user1 : following){
            userFollowingDTO.add(new ViewUserRelationsDTO(user1.getId(),user1.getFirstName(),user1.getLastName()));
        }
        return userFollowingDTO;
    }
    
    /* ************* Edit profile ************* */

    @GetMapping(value = "/profile")
    public ViewUserProfileDTO viewProfile(HttpSession session) throws NotLoggedException, IOException {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        if(isLoggedIn(session)) {
            return new ViewUserProfileDTO(user.getUsername(),user.getPhoto(),
                    postController.getAllUserPosts(getLoggedUserByIdSession(session)).size(),postController.getAllUserPosts(getLoggedUserByIdSession(session)),
                    getAllUserFollowing(user).size(),getAllUserFollowing(user),
                    getAllUserFollowers(user).size(), getAllUserFollowers(user));
        }
        throw new NotLoggedException();
    }

    @PutMapping(value = "/profile/edit/password")
    public void editPassword(@RequestBody EditPasswordDTO editPasswordDTO, HttpSession session) throws NotLoggedException,BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        System.out.println(user.getUsername());
        if(isLoggedIn(session)){
            if(editPasswordDTO.getOldPassword().equals(user.getPassword())) {
                validatePassword(editPasswordDTO.getNewPassword(), editPasswordDTO.getVerifyNewPassword());
                user.setPassword(editPasswordDTO.getNewPassword());
                userRepository.save(user);
            }
            else {
                throw new WrongPasswordInputException();
            }
        }
        else {
            throw new NotLoggedException();
        }
    }

    @PutMapping(value = "/profile/edit/email")
    public void editEmail(@RequestBody EditEmailDTO editEmailDTO, HttpSession session)throws NotLoggedException,BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        if(isLoggedIn(session)){
            if(editEmailDTO.getPassword().equals(user.getPassword())){
                validateEmail(editEmailDTO.getNewEmail());
                user.setEmail(editEmailDTO.getNewEmail());
                userRepository.save(user);
            }
            else{
                throw new WrongPasswordInputException();
            }
        }
        else {
            throw new NotLoggedException();
        }
    }

    @PutMapping(value = "profile/edit/firstName")
    public void editFirstName(@RequestBody EditFirstNameDTO editFirstNameDTO, HttpSession session) throws NotLoggedException,BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        if(isLoggedIn(session)){
            validateFirstName(editFirstNameDTO.getNewFirstName());
            user.setFirstName(editFirstNameDTO.getNewFirstName());
            userRepository.save(user);
        }else {
            throw new NotLoggedException();
        }
    }

    @PutMapping(value = "/profile/edit/lastName")
    public void editLastName(@RequestBody EditLastNameDTO editLastNameDTO, HttpSession session) throws NotLoggedException,BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        if(isLoggedIn(session)){
            validateLastName(editLastNameDTO.getNewLastName());
            user.setLastName(editLastNameDTO.getNewLastName());
            userRepository.save(user);
        }else {
            throw new NotLoggedException();
        }
    }

    @DeleteMapping(value = "/profile/edit/delete")
    public void deleteProfile(@RequestBody DeleteUserProfileDTO deleteUserProfileDTO, HttpSession session) throws NotLoggedException,BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        if(isLoggedIn(session)){
            if(deleteUserProfileDTO.getConfirmPassword().equals(user.getPassword())){
                for(ViewUserRelationsDTO user1 : getAllUserFollowing(user)) {
                    userUnfollow(user1.getId(),session);
                }
                userLogout(session);
                userRepository.delete(user);
            }
            else{
                throw new WrongPasswordInputException();
            }
        }
        else {
            throw new NotLoggedException();
        }
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

    protected long getLoggedUserByIdSession(HttpSession session)throws NotLoggedException {
        User user = ((User)(session.getAttribute("User")));
        if(isLoggedIn(session) || user != null){
            return  ((User)(session.getAttribute("User"))).getId();
        }
        throw new NotLoggedException();
    }

    protected void validateIfUserExist(long userId)throws UserExistException {
        if(!userRepository.existsById(userId)) {
            throw new UserExistException();
        }
    }


    public User getUserById(long id){
        return getUserById(id);
    }
}
