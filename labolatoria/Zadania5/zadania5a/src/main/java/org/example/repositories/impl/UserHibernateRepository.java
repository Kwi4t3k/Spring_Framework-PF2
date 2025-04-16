package org.example.repositories.impl;

import lombok.Setter;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Setter
public class UserHibernateRepository implements UserRepository {
    private Session session;

    public UserHibernateRepository(Session session) {
        this.session = session;
    }

    @Override
    public List<User> findAll() {
        return session.createQuery("FROM User", User.class).list();
    }

    @Override
    public Optional<User> findById(String id) { // tak można bo id jest kluczem głównym
        return Optional.ofNullable(session.get(User.class, id));
    }

    @Override
    public Optional<User> findByLogin(String login) {
//        return Optional.ofNullable(session.get(User.class, login)); to nie jest dobrze bo login nie jest kluczem głównym

        Query<User> query = session.createQuery("from User where login = :login", User.class);
        query.setParameter("login", login);
        return query.uniqueResultOptional();
    }

    @Override
    public User save(User user) {
        return session.merge(user);
    }

    @Override
    public void deleteById(String id) {
        User user = session.get(User.class, id);

        if (user != null) {
            session.remove(user);
        }
    }
}