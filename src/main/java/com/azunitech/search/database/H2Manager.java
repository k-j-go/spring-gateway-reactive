package com.azunitech.search.database;

import com.azunitech.search.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;

public class H2Manager {
    private JdbcTemplate jdbcTemplate;

    private void init() {
        jdbcTemplate.execute("create table users (id bigint not null, title varchar(255), author varchar(255), primary key (id))");
    }

    public void insert(User user) {
        jdbcTemplate.update("INSERT INTO USERS (id, title, author) VALUES (?, ?, ?)", user.getId(), user.getTitle(), user.getAuthor());

    }

    public List<User> query() {
        return jdbcTemplate.query("SELECT * FROM USERS", new UserRowMapper());
    }

    public static H2ManagerBuilder builder() {
        return new H2ManagerBuilder();
    }

    public static class H2ManagerBuilder {
        private String url = "jdbc:h2:mem:testdb";
        private String userName = "sa";
        private String password = "";

        public H2ManagerBuilder url(String url) {
            this.url = url;
            return this;
        }

        public H2ManagerBuilder username(String userName) {
            this.userName = userName;
            return this;
        }

        public H2ManagerBuilder password(String password) {
            this.password = password;
            return this;
        }

        public H2Manager build() {
            H2Manager mg = new H2Manager();
            DataSource dataSource = new DriverManagerDataSource(this.url, this.userName, this.password);
            mg.jdbcTemplate = new JdbcTemplate(dataSource);
            mg.init();
            return mg;
        }
    }
}
