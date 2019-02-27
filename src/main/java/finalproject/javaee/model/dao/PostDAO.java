package finalproject.javaee.model.dao;

import finalproject.javaee.model.pojo.Post;
import finalproject.javaee.model.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class PostDAO extends BaseDAO{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    public List<Post> getPostsByCategory(int category_id) throws SQLException{ //TODO
        PreparedStatement ps = getConnection().prepareStatement("SELECT id, description, location_id, categories_id, date FROM posts as p WHERE p.categories_id = ?");
        //List<Post> posts = jdbcTemplate.query("SELECT description, location_id, categories_id, date FROM posts as p WHERE p.categories_id = category_id", (resultSet, i) -> toPost(resultSet));
        //return posts;
        ps.setInt(1, category_id);
        ResultSet rs =  ps.executeQuery();
        List<Post> posts = new ArrayList<>();
        while (rs.next()) {
            Post p = new Post(rs.getString("description"));
            posts.add(p);
        }
        return posts;
    }

    private Post toPost(ResultSet resultSet) throws SQLException {
        Post post = new Post(resultSet.getString("description"));
        return post;
    }


    public List<Post> getPostsByUser(long user_id) throws SQLException{
        List<Post> postsByUser = new ArrayList<>();
        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery("SELECT p.id, p.description FROM posts p JOIN users u ON p.user_id = u.id" );
        while(rs.next()){
            postsByUser.add(new Post(rs.getInt(1), rs.getString(2)));
        }
        return postsByUser;
    }
}
