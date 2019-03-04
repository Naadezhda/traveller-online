package finalproject.javaee.dto.userDTO;

import finalproject.javaee.dto.PostWithUserAndMediaDTO;

import java.util.Comparator;

public class PostsByDateComparator implements Comparator<PostWithUserAndMediaDTO> {

    @Override
    public int compare(PostWithUserAndMediaDTO o1, PostWithUserAndMediaDTO o2) { //DESC ORDER
        return o2.getDate().compareTo(o1.getDate());
    }
}
