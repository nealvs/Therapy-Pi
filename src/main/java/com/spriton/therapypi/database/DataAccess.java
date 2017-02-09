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
import java.util.*;

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
            config.addAnnotatedClass(Patient.class);
            config.addAnnotatedClass(PatientSession.class);
            config.addAnnotatedClass(ConfigValue.class);
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

    public static List<Patient> getAllPatients() {
        try(Session session = getSessionFactory().openSession()) {
            List<Patient> patients = session.createQuery("FROM Patient WHERE deleted IS NULL ORDER BY lastName, firstName").list();
            return patients;
        }
    }

    public static List<Patient> getRecentPatients() {
        try(Session session = getSessionFactory().openSession()) {
            Date today = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(today);
            cal.add(Calendar.DAY_OF_MONTH, -30);
            List<Patient> patients = session.createQuery(
                    "SELECT DISTINCT p FROM Patient p, PatientSession s " +
                    "WHERE s.patientId = p.id AND p.deleted IS NULL AND s.deleted IS NULL " +
                    "AND s.startTime > :date ")
                    .setDate("date", cal.getTime())
                    .list();

            for(Patient patient : patients) {
                patient.setSessions(getPatientSessions(patient.getId()));
            }

            Collections.sort(patients, new Comparator<Patient>() {
                @Override
                public int compare(Patient patient, Patient patient2) {
                    if(patient.getSessions() != null && !patient.getSessions().isEmpty() && patient.getSessions().get(0).getStartTime() != null) {
                        if(patient2.getSessions() != null && !patient2.getSessions().isEmpty() && patient2.getSessions().get(0).getStartTime() != null) {
                            patient.getSessions().get(0).getStartTime().compareTo(patient2.getSessions().get(0).getStartTime());
                        }
                    }
                    return 0;
                }
            });

            return patients;
        }
    }

    public static Patient getPatient(int id) {
        try(Session session = getSessionFactory().openSession()) {
            Patient patient = session.get(Patient.class, id);
            if(patient != null) {
                patient.setSessions(getPatientSessions(patient.getId()));
            }
            return patient;
        }
    }

    public static Patient createPatient(Patient patient) {
        try(Session session = getSessionFactory().openSession()) {
            patient.setCreated(new Date());
            session.save(patient);
            session.flush();
            return patient;
        }
    }

    public static Patient updatePatient(Patient patient) {
        try(Session session = getSessionFactory().openSession()) {
            patient.setUpdated(new Date());
            session.update(patient);
            session.flush();
            return patient;
        }
    }

    public static void deletePatient(int id) {
        Patient patient = getPatient(id);
        if(patient != null) {
            patient.setDeleted(new Date());
            try(Session session = getSessionFactory().openSession()) {
                session.update(patient);
                session.flush();
            }
        }
    }

    public static PatientSession getSession(int id) {
        try(Session session = getSessionFactory().openSession()) {
            return session.get(PatientSession.class, id);
        }
    }

    public static ConfigValue getConfigValue(String key) {
        try(Session session = getSessionFactory().openSession()) {
            ConfigValue value = session.get(ConfigValue.class, key);
            if(value != null) {
                log.info("Loaded ConfigValue. " + value.getConfigKey() + ": " + value.getConfigValue() + " - " + value.getUpdated());
            }
            return value;
        } catch(Exception ex) {
            log.error("Unable to get config from db: " + key, ex);
        }
        return null;
    }

    public static ConfigValue updateConfigValue(ConfigValue value) {
        try(Session session = getSessionFactory().openSession()) {
            value.setUpdated(new Date());
            session.update(value);
            session.flush();
            log.info("Updated ConfigValue. " + value.getConfigKey() + ": " + value.getConfigValue() + " - " + value.getUpdated());
            return value;
        }
    }

    public static ConfigValue saveConfigValue(ConfigValue value) {
        try(Session session = getSessionFactory().openSession()) {
            value.setCreated(new Date());
            session.save(value);
            session.flush();
            log.info("Saved ConfigValue. " + value.getConfigKey() + ": " + value.getConfigValue());
            return value;
        }
    }

    public static Double getDoubleConfig(String key) {
        try(Session session = getSessionFactory().openSession()) {
            ConfigValue value = session.get(ConfigValue.class, key);
            if(value != null && value.getConfigValue() != null) {
                log.info("Double Config from database. " + key + ": " + value.getConfigValue() + " - " + value.getUpdated());
                return Double.parseDouble(value.getConfigValue());
            }
        } catch(Exception ex) {
            log.error("Unable to get config from db: " + key, ex);
        }
        return null;
    }

    public static List<PatientSession> getPatientSessions(int id) {
        try(Session session = getSessionFactory().openSession()) {
            List<PatientSession> sessions = session
                    .createQuery("FROM PatientSession WHERE patientId = :id AND deleted IS NULL ORDER BY startTime DESC")
                    .setInteger("id", id)
                    .list();
            return sessions;
        }
    }

    public static PatientSession createOrUpdateSession(PatientSession patientSession) {
        try(Session session = getSessionFactory().openSession()) {
            if(patientSession.getId() == null) {
                session.save(patientSession);
            } else {
                session.update(patientSession);
            }
            session.flush();
            return patientSession;
        }
    }

    public static void shutdown() {
        getSessionFactory().close();
    }

}
