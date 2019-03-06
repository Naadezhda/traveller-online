package finalproject.javaee.service;

import finalproject.javaee.dto.userDTO.*;
import finalproject.javaee.dto.userDTO.editUserProfileDTO.*;
import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import finalproject.javaee.model.util.MailUtil;
import finalproject.javaee.model.util.exceptions.BaseException;
import finalproject.javaee.model.util.exceptions.usersExceptions.*;
import finalproject.javaee.model.util.exceptions.usersRegistrationExcepions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    PostService postService;

    public UserInformationDTO register(User user) throws BaseException, MessagingException {
        validateUsername(user.getUsername());
        validatePassword(user.getPassword(),user.getVerifyPassword());

//        validatePassword(CryptWithMD5.crypt(user.getPassword()),CryptWithMD5.crypt(user.getVerifyPassword()));
//        user.setPassword(CryptWithMD5.crypt(user.getPassword()));
//        user.setVerifyPassword(CryptWithMD5.crypt(user.getVerifyPassword()));

        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
        validateEmail(user.getEmail());
        validateGender(user.getGender());

        long code = System.currentTimeMillis();
        user.setSecureCode(code);
        MailUtil.sendMail("nadejdab29@gmail.bg",user.getEmail(),"Confirm registration by email.",
                "To complete your registration, enter the following code  " + code + " ");

        userRepository.save(user);
        return new UserInformationDTO(user.getId(),user.getUsername(),user.getFirstName(),
                user.getLastName(),user.getEmail(),user.getPhoto(),user.getGender());
    }

    public void followUser(User user, long id) throws BaseException {
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
    }

    public UserDTO unfollowUser(User user, long id) throws BaseException {
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
        return unfollowingUser.userToUserDTO();
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

    public void editPassword(User user, EditPasswordDTO editPasswordDTO) throws BaseException{
        if(editPasswordDTO.getOldPassword().equals(user.getPassword())) {
//        if(editPasswordDTO.getOldPassword().equals(CryptWithMD5.crypt(user.getPassword()))) {
            validatePassword(editPasswordDTO.getNewPassword(), editPasswordDTO.getVerifyNewPassword());
            user.setPassword(editPasswordDTO.getNewPassword());
            userRepository.save(user);
        }
        else {
            throw new WrongPasswordInputException();
        }
    }

    public void editEmail(User user, EditEmailDTO editEmailDTO) throws BaseException {
//      if(editEmailDTO.getPassword().equals(CryptWithMD5.crypt(user.getPassword()))){
        if(editEmailDTO.getPassword().equals(user.getPassword())){
            validateEmail(editEmailDTO.getNewEmail());
            user.setEmail(editEmailDTO.getNewEmail());
            userRepository.save(user);
        }
        else{
            throw new WrongPasswordInputException();
        }
    }

    public void editFirstName(User user, EditFirstNameDTO editFirstNameDTO) throws RegistrationException{
        validateFirstName(editFirstNameDTO.getNewFirstName());
        user.setFirstName(editFirstNameDTO.getNewFirstName());
        userRepository.save(user);
    }

    public void editLastName(User user, EditLastNameDTO editLastNameDTO) throws RegistrationException{
        validateLastName(editLastNameDTO.getNewLastName());
        user.setLastName(editLastNameDTO.getNewLastName());
        userRepository.save(user);
    }

    public DeleteUserProfileDTO deleteUser(User user, DeleteUserProfileDTO deleteUserProfileDTO) throws BaseException{
//      if(deleteUserProfileDTO.getConfirmPassword().equals(CryptWithMD5.crypt(user.getPassword()))){
        if(deleteUserProfileDTO.getConfirmPassword().equals(user.getPassword())){
            for(ViewUserRelationsDTO user1 : getAllUserFollowing(user)) {
                unfollowUser(user, user1.getId());
            }
            userRepository.delete(user);
        }
        else{
            throw new WrongPasswordInputException();
        }
        return deleteUserProfileDTO;
    }

    /* ************* Validations ************* */

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

    protected void validateIfUserExist(long userId)throws UserExistException {
        if(!userRepository.existsById(userId)) {
            throw new UserExistException();
        }
    }


    public User getUserById(long id){
        return getUserById(id);
    }

}
