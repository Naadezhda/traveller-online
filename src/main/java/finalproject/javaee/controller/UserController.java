package finalproject.javaee.controller;

import finalproject.javaee.model.pojo.User;
import finalproject.javaee.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/registration")
    public User userRegistration(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }

    @PostMapping(value = "/login")
    public void login(){

    }
}
