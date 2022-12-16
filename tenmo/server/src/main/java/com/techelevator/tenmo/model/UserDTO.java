package com.techelevator.tenmo.model;

public class UserDTO {
    private int id;
    private String username;

    public UserDTO(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public UserDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}

