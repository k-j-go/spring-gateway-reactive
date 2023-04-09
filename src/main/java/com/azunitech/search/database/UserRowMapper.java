package com.azunitech.search.database;

import com.azunitech.search.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        return new User(id, title, author);
    }
}
