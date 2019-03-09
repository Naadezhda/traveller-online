package finalproject.javaee.service;

import finalproject.javaee.controller.BaseController;
import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.dto.userDTO.*;
import finalproject.javaee.dto.userDTO.editUserProfileDTO.*;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.util.Crypt;
import finalproject.javaee.util.MailUtil;
import finalproject.javaee.util.exceptions.BaseException;
import finalproject.javaee.util.exceptions.usersExceptions.ExistException;
import finalproject.javaee.util.exceptions.usersExceptions.InvalidInputException;
import finalproject.javaee.util.exceptions.usersExceptions.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional(rollbackOn = BaseException.class)
public class UserService {

    static Logger logger = Logger.getLogger(UserService.class.getName());

    @Autowired private UserRepository userRepository;
    @Autowired private PostService postService;

    public RegisterDTO register(User user) throws Exception{
        validateUsername(user.getUsername().trim());
        validatePassword(user.getPassword().trim(), user.getVerifyPassword().trim());
        user.setPassword(Crypt.hashPassword(user.getPassword().trim()));
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

        return new RegisterDTO(("Registration success.We have sent you email to " + user.getEmail() + "." +
                "Please click the link in that message to activate your account!"),user.getId(), user.getUsername(),
                user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoto(), user.getGender());
    }
    public MessageDTO complete(User user, String secureCode) throws BaseException {
        if(user.isCompleted()) {
            throw new InvalidInputException("Account is already activated.");
        }
            validateKey(user.getSecureCode(), secureCode);
            user.setCompleted(true);
            userRepository.save(user);
        return new MessageDTO("Account has been successfully verified!");
    }

    public MessageDTO followUser(User user, long id) throws BaseException {
        User followingUser = userRepository.findById(id);
        if (userRepository.existsById(id)) {
            if (!user.getFollowing().contains(followingUser)) {
                followingUser.getFollower().add(user);
                user.getFollowing().add(followingUser);
                userRepository.save(user);
            } else {
                throw new UserRelationException("Already followed.");
            }
        } else {
            throw new ExistException("User does not exist.");
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
                throw new UserRelationException("User is not followed.");
            }
        } else {
            throw new ExistException("User does not exist.");
        }
        return new MessageDTO(user.getUsername() + " unfollow " + unfollowingUser.getUsername() + ".");
    }

    public List<ViewUserRelationsDTO> getAllUserFollowers(User user) {
        List<User> follower = userRepository.findAllByFollowingId(user.getId());
        List<ViewUserRelationsDTO> userFollowerDTO = new ArrayList<>();
        for (User u : follower) {
            userFollowerDTO.add(new ViewUserRelationsDTO(u.getId(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getPhoto()));
        }
        return userFollowerDTO;
    }

    public List<ViewUserRelationsDTO> getAllUserFollowing(User user) {
        List<User> following = userRepository.findAllByFollowerId(user.getId());
        List<ViewUserRelationsDTO> userFollowingDTO = new ArrayList<>();
        for (User user1 : following) {
            userFollowingDTO.add(new ViewUserRelationsDTO(user1.getId(), user1.getUsername(), user1.getFirstName(), user1.getLastName(), user1.getPhoto()));
        }
        return userFollowingDTO;
    }

    public ViewUserProfileDTO viewProfile(User user) throws BaseException {
        return new ViewUserProfileDTO(user.getUsername(), user.getPhoto(),
                getAllUserFollowing(user),
                getAllUserFollowers(user),
                postService.getAllUserPosts(user.getId()));
    }

    public MessageDTO editPassword(User user, EditPasswordDTO editPasswordDTO) throws BaseException{
        if(Crypt.checkPassword(editPasswordDTO.getOldPassword().trim(),user.getPassword().trim())){
            validatePassword(editPasswordDTO.getNewPassword().trim(), editPasswordDTO.getVerifyNewPassword().trim());
            user.setPassword(Crypt.hashPassword(editPasswordDTO.getNewPassword()).trim());
            userRepository.save(user);
        }else {
            throw new InvalidInputException("Invalid password input.");
        }
        return new MessageDTO("Password changed successfully.");
    }

    public MessageDTO editEmail(User user, EditEmailDTO editEmailDTO) throws BaseException {
        if(Crypt.checkPassword(editEmailDTO.getPassword().trim(),user.getPassword().trim())){
            validateEmail(editEmailDTO.getNewEmail());
            user.setEmail(editEmailDTO.getNewEmail());
            userRepository.save(user);
        }else{
            throw new InvalidInputException("Invalid password input.");
        }
        return new MessageDTO("Email changed successfully.");
    }

    public MessageDTO editFirstName(User user, EditFirstNameDTO editFirstNameDTO) throws BaseException{
        validateFirstName(editFirstNameDTO.getNewFirstName());
        user.setFirstName(editFirstNameDTO.getNewFirstName());
        userRepository.save(user);
        return new MessageDTO("First name changed successfully.");
    }

    public MessageDTO editLastName(User user, EditLastNameDTO editLastNameDTO) throws BaseException{
        validateLastName(editLastNameDTO.getNewLastName());
        user.setLastName(editLastNameDTO.getNewLastName());
        userRepository.save(user);
        return new MessageDTO("Last name changed successfully.");
    }

    public UserInformationDTO deleteUser(User user, DeleteUserProfileDTO deleteUserProfileDTO) throws BaseException{
        if(Crypt.checkPassword(deleteUserProfileDTO.getConfirmPassword().trim(),user.getPassword().trim())){
            for (ViewUserRelationsDTO user1 : getAllUserFollowing(user)) {
                unfollowUser(user, user1.getId());
            }
            userRepository.delete(user);
        } else{
            throw new InvalidInputException("Invalid password input.");
        }
        return new UserInformationDTO(user.getId(),user.getUsername(),user.getFirstName(),
                user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
    }

    /* ************* Validations ************* */

    public void validateKey(String key, String verifyKey) throws BaseException{
        if(!key.equals(verifyKey)){
            throw new InvalidInputException("Entered wrong code.");
        }
    }

    private void validateUsername(String username)throws BaseException {
        if(username == null || username.isEmpty() || username.contains(" ")){
            throw new InvalidInputException("Invalid username input.");
        }
        if(userRepository.findByUsername(username) != null){
            throw new ExistException("Username already exist.");
        }
    }

    private void validatePassword(String password, String verifyPassword) throws BaseException {
        if((password == null || verifyPassword == null)||(password.isEmpty() || verifyPassword.isEmpty())){
            throw new InvalidInputException("Password can not be empty.");
        }
        if(password.contains(" ")||verifyPassword.contains(" ")){
            throw new InvalidInputException("Password can not contains space.");
        }
        if(!password.equals(verifyPassword)){
            throw new InvalidInputException("Passwords do not match.");
        }
        formatPassword(password);
    }

    private void formatPassword(String password) throws InvalidInputException {
        Pattern p = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*!?-]).{6,})");
        if(!p.matcher(password).find()){
            throw new InvalidInputException("The password must be at least six characters," +
                    "one upper case and one lower case letter,one number and one special character.");
        }
    }

    private void validateFirstName(String firstName) throws BaseException {
        if(firstName == null || firstName.isEmpty() || firstName.contains(" ")){
            throw new InvalidInputException("Invalid first name input.");
        }
    }
    private void validateLastName(String lastName) throws BaseException {
        if(lastName == null || lastName.isEmpty() || lastName.contains(" ")){
            throw new InvalidInputException("Invalid last name input.");
        }
    }

    private void validateEmail(String email) throws BaseException {
        try {
            if (email != null) {
                InternetAddress internetAddress = new InternetAddress(email);
                internetAddress.validate();
            } else {
                throw new InvalidInputException("Invalid email input.");
            }
        } catch (AddressException e) {
            logger.error(e.getMessage());
            throw new InvalidInputException("Invalid email input.");
        }
        if(userRepository.findByEmail(email) != null){
            throw new ExistException("Email already exist.");
        }
    }

    private void validateGender(String gender) throws BaseException{
        if(gender == null ||!(gender.equalsIgnoreCase(("M")) || gender.equalsIgnoreCase(("F")))){
            throw new InvalidInputException("Invalid gender input.");
        }
    }

    public void validateUsernameAndPassword(String username, String password) throws InvalidLoginException {
        if ((username == null || password == null) || (username.isEmpty() || password.isEmpty())) {
            throw new InvalidLoginException();
        } else {
            User user = userRepository.findByUsername(username);
            if (user == null || !Crypt.checkPassword(password,user.getPassword().trim())) {
                throw new InvalidLoginException();
            }
        }
    }

    public void validateIfUserExist(long userId)throws BaseException {
        if(!userRepository.existsById(userId)) {
            throw new ExistException("User does not exist.");
        }
    }
}
