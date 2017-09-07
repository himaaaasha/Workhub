package com.example.himasha.workhub;

/**
 * Created by Himasha on 9/5/2017.
 */

public class User {
    private String UserName;
    private String UserEmail;
    private String UserTelephone;

    public User() {
    }

    public User(String userName, String userEmail, String userTelephone) {
        UserName = userName;
        UserEmail = userEmail;
        UserTelephone = userTelephone;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserTelephone() {
        return UserTelephone;
    }

    public void setUserTelephone(String userTelephone) {
        UserTelephone = userTelephone;
    }
}
