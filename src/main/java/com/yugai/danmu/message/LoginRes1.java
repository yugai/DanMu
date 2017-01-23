package com.yugai.danmu.message;

/**
 * Created by yugai on 2016-04-05.
 */
public class LoginRes1 extends Message {
    private String userName;

    public LoginRes1(Message message) {
        super(message);
        userName = list.get("username");
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "LoginRes{" +
               "userName='" + userName + '\'' +
               '}';
    }
}
