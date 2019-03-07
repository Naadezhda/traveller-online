package finalproject.javaee.controller;

import finalproject.javaee.dto.MessageDTO;
import finalproject.javaee.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class TagController extends BaseController {

    @Autowired private TagService tagService;

    @GetMapping(value = "/tags/posts/{postId}/users/{userId}")
    public MessageDTO addTagUser(@PathVariable("postId") long postId,
                                 @PathVariable("userId") long userId, HttpSession session) throws Exception {
        validateisLoggedIn(session);
        return tagService.tagUser(userId,postId);
    }

    @DeleteMapping(value = "/tags/posts/{postId}/users/{userId}")
    public MessageDTO removeTagUser(@PathVariable("postId") long postId,
                                    @PathVariable("userId") long userId, HttpSession session) throws Exception {
        validateisLoggedIn(session);
        return tagService.deleteTagUser(userId, postId);
    }
}
