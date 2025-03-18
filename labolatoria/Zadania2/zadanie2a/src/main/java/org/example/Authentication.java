package org.example;

public class Authentication {
    private final IUserRepository userRepository;

    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String login, String password) {
        User user = userRepository.getUser(login);

        if (user == null) {
            System.out.println("Nie ma takiego użytkowanika w bazie");
            return null;
        }
        if (user.getPassword().equals(password)) {
            System.out.println("Witaj, " + login + "!");
            return user;
        } else {
            System.out.println("Niepoprawne hasło");
            return null;
        }
    }
}