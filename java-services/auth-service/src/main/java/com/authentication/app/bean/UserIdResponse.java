package com.authentication.app.bean;

public class UserIdResponse {

    private final String userId;

    public UserIdResponse(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
