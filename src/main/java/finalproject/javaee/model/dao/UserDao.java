package finalproject.javaee.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends BaseDAO {


    @Autowired
    private JdbcTemplate jdbcTemplate;
}
