package com.example.chatSSE.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * An Object that represents our users
 */
public class User {

    /**
     * The unique identification number of the user
     */
    private int userId;

    /**
     * The unique username of the user
     */
    @Size(min = 2,
            max = 30,
            message = "The length of the username should be between {min} and {max}")
    private String userName;

    /**
     * The password of the user
     */
    @Size(min = 5,
            max = 15,
            message = "The length of the password should be between {min} and {max}")
    private String password;

    /**
     * Integer Value to represent the status of the user(1->online, 0->offline)
     */
    @Min(0)
    @Max(1)
    private Integer isOnline = 0;

    public User(int userId, String userName, String password, Integer isOnline) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.isOnline = isOnline;
    }

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }
}