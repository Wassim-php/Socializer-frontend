package com.example.demo.model;

public class UserUpdate {

    private String username;
    private String currentPassword;
    private String password;
    private String avatarColorHex;

    public UserUpdate(){

    }

    public UserUpdate(String username, String currentPassword, String newPassword, String avatarColorHex) {
        this.username = username;
        this.currentPassword = currentPassword;
        this.password = newPassword;
        this.avatarColorHex = avatarColorHex;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setAvatarColorHex(String avatarColorHex) {
        this.avatarColorHex = avatarColorHex;
    }

    public String getAvatarColorHex() {
        return avatarColorHex;
    }

    public String getPassword() {
        return password;
    }
}
