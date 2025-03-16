package org.example;

public class Authentication {
    private final UserRepository userRepository;

    public Authentication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String login, String password) {
        User user = userRepository.getUser(login);

        if (user == null) {
            System.out.println("Nie ma takiego użytkowanika w bazie");
            return false;
        }
        if (user.getPassword().equals(password)) {
            System.out.println("Witaj, " + login + "!");
            return true;
        } else {
            System.out.println("Niepoprawne hasło");
            return false;
        }
    }
}
