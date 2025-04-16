package org.example.app;

import org.example.config.HibernateConfig;
import org.hibernate.Session;

public class Main {
    public static void main(String[] args) {
        Session session = HibernateConfig.getSessionFactory().openSession();


    }
}
