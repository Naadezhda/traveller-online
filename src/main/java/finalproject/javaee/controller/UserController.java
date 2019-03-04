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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController extends BaseController {

    static Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostController postController;

    @PostMapping(value = "/register")
    public void userRegistration(@RequestBody User user, HttpSession session) throws Exception {
        validateUsername(user.getUsername());
        validatePassword(user.getPassword(),user.getVerifyPassword());

//        validatePassword(CryptWithMD5.crypt(user.getPassword()),CryptWithMD5.crypt(user.getVerifyPassword()));
//        user.setPassword(CryptWithMD5.crypt(user.getPassword()));
//        user.setVerifyPassword(CryptWithMD5.crypt(user.getVerifyPassword()));

        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
        validateEmail(user.getEmail());
        validateGender(user.getGender());

        user.setSecureCode(key());
        String key = user.getSecureCode();

        new Thread(()-> {
            try {

                MailUtil.sendMail("nadejdab29@gmail.bg", user.getEmail(), "Confirm registration by email.",
                        "To complete your registration, enter the following code  " + key + " ");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }).start();
        userRepository.save(user);
        session.setAttribute("User", user);
        session.setAttribute("Username", user.getUsername());
    }

    private String key() throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.toString();
    }


    @PostMapping(value = "/register/complete")
    private UserRegisterDTO completeRegisterWithMeil(@RequestBody CompleteRegisterDTO completeRegister,HttpSession session)throws Exception {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        validateKey(user.getSecureCode(), completeRegister.getSecureCode());

        return new UserRegisterDTO(user.getId(),user.getUsername(),user.getFirstName(),
                user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
    }

    private void validateKey(String key, String verifyKey) throws BaseException{
        if((key == null || verifyKey ==null)||(key.isEmpty() || verifyKey.isEmpty())){
            throw new BaseException("Invalid input code.");
        }
        if(!key.equals(verifyKey)){
            throw new BaseException("You entered the wrong code. Try again later.");
        }
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
    public void userLogout(HttpSession session) throws BaseException {
        if (!isLoggedIn(session)) {
            throw new UserLoggedOutException();
        }
        session.invalidate();
    }

    /* ************* Follow and Unfollow ************* */

    @GetMapping(value = "/follow/{id}")
    public void userFollow(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        User followingUser = userRepository.findById(id);
        validateisLoggedIn(session);
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
    }

    @DeleteMapping(value = "/unfollow/{id}")
    public void userUnfollow(@PathVariable("id") long id, HttpSession session) throws BaseException {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        User unfollowingUser = userRepository.findById(id);
        validateisLoggedIn(session);
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
    }

    protected List<ViewUserRelationsDTO> getAllUserFollowers(User user) {
        List<User> follower = userRepository.findAllByFollowingId(user.getId());
        List<ViewUserRelationsDTO> userFollowerDTO = new ArrayList<>();
        for(User u : follower){
            userFollowerDTO.add(new ViewUserRelationsDTO(u.getId(),u.getUsername(),u.getFirstName(),u.getLastName(),u.getPhoto()));
        }
        return userFollowerDTO;
    }
    protected List<ViewUserRelationsDTO> getAllUserFollowing(User user){
        List<User> following = userRepository.findAllByFollowerId(user.getId());
        List<ViewUserRelationsDTO> userFollowingDTO = new ArrayList<>();
        for (User user1 : following){
            userFollowingDTO.add(new ViewUserRelationsDTO(user1.getId(), user1.getUsername(),user1.getFirstName(),user1.getLastName(), user1.getPhoto()));
        }
        return userFollowingDTO;
    }

    /* ************* Edit profile ************* */

    @GetMapping(value = "/profile")
    public ViewUserProfileDTO viewProfile(HttpSession session) throws Exception {
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        validateisLoggedIn(session);
        return new ViewUserProfileDTO(user.getUsername(),user.getPhoto(),
                postController.getAllUserPosts(getLoggedUserByIdSession(session)).size(),postController.getAllUserPosts(getLoggedUserByIdSession(session)),
                getAllUserFollowing(user).size(),getAllUserFollowing(user),
                getAllUserFollowers(user).size(), getAllUserFollowers(user));
    }

    @PutMapping(value = "/profile/edit/password")
    public void editPassword(@RequestBody EditPasswordDTO editPasswordDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        validateisLoggedIn(session);
        if(editPasswordDTO.getOldPassword().equals(user.getPassword())) {
//        if(editPasswordDTO.getOldPassword().equals(CryptWithMD5.crypt(user.getPassword()))) {
            validatePassword(editPasswordDTO.getNewPassword(), editPasswordDTO.getVerifyNewPassword());
            user.setPassword(editPasswordDTO.getNewPassword());
            userRepository.save(user);
            session.invalidate();
        }
        else {
            throw new WrongPasswordInputException();
        }
    }

    @PutMapping(value = "/profile/edit/email")
    public void editEmail(@RequestBody EditEmailDTO editEmailDTO, HttpSession session)throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        validateisLoggedIn(session);
//            if(editEmailDTO.getPassword().equals(CryptWithMD5.crypt(user.getPassword()))){
        if(editEmailDTO.getPassword().equals(user.getPassword())){
            validateEmail(editEmailDTO.getNewEmail());
            user.setEmail(editEmailDTO.getNewEmail());
            userRepository.save(user);
        }
        else{
            throw new WrongPasswordInputException();
        }
    }

    @PutMapping(value = "profile/edit/firstName")
    public void editFirstName(@RequestBody EditFirstNameDTO editFirstNameDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        validateisLoggedIn(session);
        validateFirstName(editFirstNameDTO.getNewFirstName());
        user.setFirstName(editFirstNameDTO.getNewFirstName());
        userRepository.save(user);
    }

    @PutMapping(value = "/profile/edit/lastName")
    public void editLastName(@RequestBody EditLastNameDTO editLastNameDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        validateisLoggedIn(session);
        validateLastName(editLastNameDTO.getNewLastName());
        user.setLastName(editLastNameDTO.getNewLastName());
        userRepository.save(user);
    }

    @DeleteMapping(value = "/profile/edit/delete")
    public void deleteProfile(@RequestBody DeleteUserProfileDTO deleteUserProfileDTO, HttpSession session) throws BaseException{
        User user = userRepository.findById(getLoggedUserByIdSession(session));
        validateisLoggedIn(session);
//            if(deleteUserProfileDTO.getConfirmPassword().equals(CryptWithMD5.crypt(user.getPassword()))){
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
