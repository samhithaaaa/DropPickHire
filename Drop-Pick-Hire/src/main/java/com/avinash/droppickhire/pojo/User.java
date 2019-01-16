package com.avinash.droppickhire.pojo;

import java.io.Serializable;

public class User implements Serializable {

    private String username;

    private String password;

    private boolean isRecruiter;

    private String id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsRecruiter() {
        return isRecruiter;
    }

    public void setIsRecruiter(boolean isRecruiter) {
        this.isRecruiter = isRecruiter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
