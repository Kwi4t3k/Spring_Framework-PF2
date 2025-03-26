package org.example.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.UserRepository;
import org.example.utils.JsonFileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserJsonRepository implements UserRepository {
    private final JsonFileStorage<User> storage = new JsonFileStorage<>("src/main/resources/vehicles.json", new TypeToken<List<Vehicle>>(){}.getType());

    private final List<User> users;

    public UserJsonRepository(List<User> users) {
        this.users = new ArrayList<>(storage.load());
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        } else {
            deleteById(user.getId());
        }
        users.add(user);
        storage.save(users);
        return user;
    }

    @Override
    public void deleteById(String id) {
        users.removeIf(v -> v.getId().equals(id));
        storage.save(users);
    }
}
