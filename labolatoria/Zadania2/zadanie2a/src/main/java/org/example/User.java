package org.example;

public class User {
    private String login;
    private String password;
    private String role; // user,admin
    private Vehicle rentedVehicle;

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicle = null;
    }

    public String getLogin() {
        return login;
    }

    public String getRole() {
        return role;
    }

    public boolean authenticate(String login, String password){
        return this.login.equals(login) && this.password.equals(password);
    }
}
