package com.spriton.therapypi.database;

import com.spriton.therapypi.Config;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class DataAccess {

    private static Logger log = Logger.getLogger(DataAccess.class);
    private static String DEFAULT_DATABASE_URL = "jdbc:h2:file:/data/h2";
    private static SessionFactory sessionFactory;

    public static void init() {
        try {
            ensureDbTables();
            setupHibernate();
        } catch (Exception ex) {
            log.error("Error starting up H2 database and hibernate connection.", ex);
        }
    }

    public static void ensureDbTables() throws Exception {
        Flyway flyway = new Flyway();
        flyway.setDataSource(Config.values.getString("DATABASE_URL", DEFAULT_DATABASE_URL), null, null);
        flyway.migrate();
    }

    public static SessionFactory getSessionFactory() {
        if(sessionFactory == null) {
            Configuration config = new Configuration().configure();
            config.addPackage("com.spriton.therapypi.database");
            config.addAnnotatedClass(Patient.class);
            config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            config.setProperty("hibernate.connection.url", Config.values.getString("DATABASE_URL", DEFAULT_DATABASE_URL));
            config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            config.setProperty("hibernate.archive.autodetection", "class");
            config.setProperty("show_sql", "true");

            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
            sessionFactory = config.buildSessionFactory(builder.build());
        }
        return sessionFactory;
    }

    public static void setupHibernate() throws Exception {
        try(Session session = getSessionFactory().openSession()) {
            List<Patient> patients = session.createQuery("FROM Patient").list();
            log.info("Database Patient Count: " + patients.size());
        }
    }


    public static void shutdown() {
        getSessionFactory().close();
    }
}
