package finalproject.javaee.service;

import finalproject.javaee.controller.BaseController;
import finalproject.javaee.controller.UserController;
import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.dto.userDTO.*;
import finalproject.javaee.dto.userDTO.editUserProfileDTO.*;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.MailUtil;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.usersExceptions.*;
import finalproject.javaee.model.util.exceptions.usersRegistrationExcepions.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    static Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    PostService postService;

    public MessageDTO register(User user) throws Exception{
        validateUsername(user.getUsername());
        validatePassword(user.getPassword(),user.getVerifyPassword());

//        validatePassword(CryptWithMD5.crypt(user.getPassword()),CryptWithMD5.crypt(user.getVerifyPassword()));
//        user.setPassword(CryptWithMD5.crypt(user.getPassword()));
//        user.setVerifyPassword(CryptWithMD5.crypt(user.getVerifyPassword()));

        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
        validateEmail(user.getEmail());
        validateGender(user.getGender());

        user.setSecureCode(BaseController.key());
        String secureCode = user.getSecureCode();

        userRepository.save(user);

        new Thread(()-> {
            try {

                MailUtil.sendMail("ittalentsX@gmail.com", user.getEmail(), "Confirm registration by email.",
                        "Complete your registration, enter the following code" +
                                " http://localhost:7777/register/" + user.getId() + "/" + secureCode);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }).start();

        return new MessageDTO("Registration success.We have sent you email to " + user.getEmail() + "."+
                "Please click the link in that message to activate your account!");
    }

    public UserInformationDTO compete(User user, String secureCode, long id) throws BaseException{
        if(user.isCompleted()) {
            throw new RegistrationException("Registration is already confirmed.");
        }
        if(userRepository.existsById(id)) {
            validateKey(user.getSecureCode(), secureCode);
            user.setCompleted(true);
            userRepository.save(user);
        }else {
            throw new UserExistException();
        }
        return new UserInformationDTO(user.getId(),user.getUsername(),user.getFirstName(),
                user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
    }

    public MessageDTO followUser(User user, long id) throws BaseException {
        User followingUser = userRepository.findById(id);
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
        return new MessageDTO(user.getUsername() + " follow " + followingUser.getUsername() + ".");
    }

    public MessageDTO unfollowUser(User user, long id) throws BaseException {
        User unfollowingUser = userRepository.findById(id);
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
        return new MessageDTO(user.getUsername() + " unfollow " + unfollowingUser.getUsername() + ".");
    }

    public List<ViewUserRelationsDTO> getAllUserFollowers(User user) {
        List<User> follower = userRepository.findAllByFollowingId(user.getId());
        List<ViewUserRelationsDTO> userFollowerDTO = new ArrayList<>();
        for(User u : follower){
            userFollowerDTO.add(new ViewUserRelationsDTO(u.getId(),u.getUsername(),u.getFirstName(),u.getLastName(),u.getPhoto()));
        }
        return userFollowerDTO;
    }

    public List<ViewUserRelationsDTO> getAllUserFollowing(User user){
        List<User> following = userRepository.findAllByFollowerId(user.getId());
        List<ViewUserRelationsDTO> userFollowingDTO = new ArrayList<>();
        for (User user1 : following){
            userFollowingDTO.add(new ViewUserRelationsDTO(user1.getId(), user1.getUsername(),user1.getFirstName(),user1.getLastName(), user1.getPhoto()));
        }
        return userFollowingDTO;
    }

    public ViewUserProfileDTO viewProfile(User user){
        return new ViewUserProfileDTO(user.getUsername(), user.getPhoto(),
                getAllUserFollowing(user),
                getAllUserFollowers(user),
                postService.getAllUserPosts(user.getId()));
    }

    public MessageDTO editPassword(User user, EditPasswordDTO editPasswordDTO) throws BaseException{
        if(editPasswordDTO.getOldPassword().equals(user.getPassword())) {
//        if(editPasswordDTO.getOldPassword().equals(CryptWithMD5.crypt(user.getPassword()))) {
            validatePassword(editPasswordDTO.getNewPassword(), editPasswordDTO.getVerifyNewPassword());
            user.setPassword(editPasswordDTO.getNewPassword());
            userRepository.save(user);
        }
        else {
            throw new WrongPasswordInputException();
        }
        return new MessageDTO("Password changed successfully.");
    }

    public MessageDTO editEmail(User user, EditEmailDTO editEmailDTO) throws BaseException {
//      if(editEmailDTO.getPassword().equals(CryptWithMD5.crypt(user.getPassword()))){
        if(editEmailDTO.getPassword().equals(user.getPassword())){
            validateEmail(editEmailDTO.getNewEmail());
            user.setEmail(editEmailDTO.getNewEmail());
            userRepository.save(user);
        }
        else{
            throw new WrongPasswordInputException();
        }
        return new MessageDTO("Email changed successfully.");
    }

    public MessageDTO editFirstName(User user, EditFirstNameDTO editFirstNameDTO) throws RegistrationException{
        validateFirstName(editFirstNameDTO.getNewFirstName());
        user.setFirstName(editFirstNameDTO.getNewFirstName());
        userRepository.save(user);
        return new MessageDTO("First name changed successfully.");
    }

    public MessageDTO editLastName(User user, EditLastNameDTO editLastNameDTO) throws RegistrationException{
        validateLastName(editLastNameDTO.getNewLastName());
        user.setLastName(editLastNameDTO.getNewLastName());
        userRepository.save(user);
        return new MessageDTO("Last name changed successfully.");
    }

    public UserInformationDTO deleteUser(User user, DeleteUserProfileDTO deleteUserProfileDTO) throws BaseException{
//      if(deleteUserProfileDTO.getConfirmPassword().equals(CryptWithMD5.crypt(user.getPassword()))){
        if(deleteUserProfileDTO.getConfirmPassword().equals(user.getPassword())){
            for(ViewUserRelationsDTO user1 : getAllUserFollowing(user)) {
                unfollowUser(user, user1.getId());
            }
//            userLogout(session);
            userRepository.delete(user);
        }
        else{
            throw new WrongPasswordInputException();
        }
        return new UserInformationDTO(user.getId(),user.getUsername(),user.getFirstName(),
                user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
    }

    /* ************* Validations ************* */

    public void validateKey(String key, String verifyKey) throws BaseException{
        if(!key.equals(verifyKey)){
            throw new BaseException("Entered wrong code.");
        }
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
            logger.error(e.getMessage());
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

    public void validateUsernameAndPassword(String username, String password) throws InvalidLoginException {
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

    public void validateIfUserExist(long userId)throws UserExistException {
        if(!userRepository.existsById(userId)) {
            throw new UserExistException();
        }
    }

    public User getUserById(long id){
        return getUserById(id);
    }

}
