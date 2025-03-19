package org.example;

import org.apache.commons.codec.digest.DigestUtils;

public class User {
    private String login;
    private String password;
    private String role; // admin albo user
    private String rentedVehicleId;

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = null; // domyślnie brak wypożyczonego pojazdu
    }

    public User(String login, String password, String role, String rentedVehicleId) { // do wczytywania z pliku
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getRentedVehicleId() {
        return rentedVehicleId;
    }

    public void rentVehicle(String vehicleId) {
        this.rentedVehicleId = vehicleId;
    }

    public void returnVehicle() {
        this.rentedVehicleId = null;
    }

    @Override
    public String toString() {
        String vehicleInfo;
        if (rentedVehicleId != null) {
            vehicleInfo = rentedVehicleId;
        } else {
            vehicleInfo = "brak";
        }

        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", rentedVehicle=" + vehicleInfo +
                '}';
    }

    public String toCSV() {
        String rentedId;
        if (rentedVehicleId != null) {
            rentedId = rentedVehicleId;
        } else {
            rentedId = "null";
        }
        return login + ";" + password + ";" + role + ";" + rentedId;
    }
}
