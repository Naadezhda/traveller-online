package finalproject.javaee.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public abstract class BaseDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private Connection connection;

    protected synchronized Connection getConnection() throws SQLException {
        if(connection == null){
            connection = jdbcTemplate.getDataSource().getConnection();
        }
        return connection;
    }

}
