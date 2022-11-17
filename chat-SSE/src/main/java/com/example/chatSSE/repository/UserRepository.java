package com.example.chatSSE.repository;

import com.example.chatSSE.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * From here we get access to the database
 */
@Repository
public class UserRepository implements UserDAO {

    /**
     * An Object that helps us to run queries
     */
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public UserRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    /**
     * Sql query from which we insert users in the database
     */
    @Override
    public User registration(User user) {

        String sql = "INSERT INTO users (username, password,isOnline) "
                + "   VALUES (:username, :password,:isOnline)";

        namedParameterJdbcOperations.update(sql,
                new MapSqlParameterSource("username", user.getUserName())
                        .addValue("password", user.getPassword())
                        .addValue("isOnline", 0));
        return user;
    }

    /**
     * Sql query to set the status of the user
     */
    @Override
    public void setUserStatus(String username) {
        String sql;
        if (findByUsername(username).isPresent()) {
            if (findByUsername(username).get().getIsOnline() == 1) {

                sql = "UPDATE users "
                        + " SET isOnline = 0 "
                        + " WHERE userName = :username";
            } else {

                sql = "UPDATE users "
                        + " SET isOnline = 1 "
                        + " WHERE userName = :username";
            }
            namedParameterJdbcOperations.update(sql, new MapSqlParameterSource("username", username));
        }
    }

    /**
     * Query to help us find a user by his username
     */
    @Override
    public Optional<User> findByUsername(String username) {

        String sql = "SELECT * "
                + "   FROM users"
                + "   WHERE username = :username";

        try {

            return Optional.ofNullable(namedParameterJdbcOperations.queryForObject(sql,
                    new MapSqlParameterSource("username", username),
                    UserRepository::mapRow));
        } catch (EmptyResultDataAccessException e) {

            return Optional.empty();
        }
    }

    /**
     * Query to help us find a user by his id
     */
    @Override
    public Optional<User> findUserById(Integer id) {
        String sql = "SELECT * "
                + "   FROM users "
                + "   WHERE user_id = :user_id";

        try {

            return Optional.ofNullable(namedParameterJdbcOperations.queryForObject(sql,
                    new MapSqlParameterSource("user_id", id),
                    UserRepository::mapRow));

        } catch (EmptyResultDataAccessException e) {

            return Optional.empty();
        }
    }

    /**
     * Query to help us delete a user by his id
     */
    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE "
                + "   FROM users "
                + "   WHERE user_id = :user_id";

        return namedParameterJdbcOperations.update(sql,
                new MapSqlParameterSource("user_id", id));
    }

    /**
     * Query to help us find all online users
     */
    @Override
    public List<User> readAllOnlineUsers() {
        String sql = "SELECT *"
                + "   FROM users "
                + "   WHERE isOnline = 1";

        return namedParameterJdbcOperations.query(sql, new MapSqlParameterSource(),
                UserRepository::mapRow);
    }

    /**
     * Query to help us find all  users
     */
    public List<User> readAllUsers() {
        String sql = "SELECT *"
                + "       FROM users ";

        return namedParameterJdbcOperations.query(sql, new MapSqlParameterSource(),
                UserRepository::mapRow);
    }

    /**
     * Row Mapper method to extract data from the database
     */
    private static User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User currentUser = new User();

        currentUser.setUserId(rs.getInt("user_id"));
        currentUser.setUserName(rs.getString("userName"));
        currentUser.setPassword(rs.getString("password"));
        currentUser.setIsOnline(rs.getInt("isOnline"));

        return currentUser;
    }
}