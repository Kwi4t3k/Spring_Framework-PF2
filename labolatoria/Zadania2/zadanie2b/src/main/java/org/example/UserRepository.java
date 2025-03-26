package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {
    private final List<User> users;
    private final String pathToFile = "src/main/resources/users.txt";

    public UserRepository() {
        this.users = new ArrayList<>();
        load();
    }

    @Override
    public User getUser(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
            for (User user : users) {
                writer.write(user.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Błąd przy zapisie pliku: " + e.getMessage());
        }
    }

    private void load() {
        File file = new File(pathToFile);
        if (!file.exists()) {
            return;
        }

        users.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    String login = parts[0];
                    String password = parts[1];
                    String role = parts[2];

                    String rentedVehicleId;
                    if (parts[3].equals("null")) {
                        rentedVehicleId = null;
                    } else {
                        rentedVehicleId = parts[3];
                    }

                    User user = new User(login, password, role, rentedVehicleId);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd przy odczycie pliku: " + e.getMessage());
        }
    }
}
