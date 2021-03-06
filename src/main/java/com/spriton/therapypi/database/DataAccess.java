package com.spriton.therapypi.database;

import com.spriton.therapypi.Config;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.*;

public class DataAccess {

    private static Logger log = Logger.getLogger(DataAccess.class);
    private static String DEFAULT_DATABASE_URL = "jdbc:h2:file:/data/h2";
    private static String DEFAULT_DATABASE_FILE = "/data/h2/h2.mv.db";
    private static SessionFactory sessionFactory;

    public static void init() {
        try {
            ensureDbTables();
            setupHibernate();
        } catch (Exception ex) {
            log.error("Error starting up H2 database and hibernate connection.", ex);
        }
    }

    public static void clearDatabase() throws Exception {
        log.info("Clearing database...");
        int patientCount = 0;
        // Mark all sessions and patients as deleted
        try(Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Patient> patients = getAllPatients();
            for(Patient patient : patients) {
                patient.setDeleted(new Date());
                session.update(patient);
                patientCount++;
            }
            session.flush();
            transaction.commit();
        }
        log.info("All patients marked as deleted. count=" + patientCount);
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
            Collections.sort(patients, new PatientComparator());
            return patients;
        }
    }

    public static List<PatientSession> getAllPatientSessions() {
        try(Session session = getSessionFactory().openSession()) {
            List<PatientSession> sessions = session.createQuery("FROM PatientSession WHERE deleted IS NULL").list();
            return sessions;
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
                    "AND s.startTime > :date ORDER BY p.lastName, p.firstName ")
                    .setDate("date", cal.getTime())
                    .list();

            Collections.sort(patients, new PatientComparator());

//            for(Patient patient : patients) {
//                patient.setSessions(getPatientSessions(patient.getId()));
//            }

//            Collections.sort(patients, new Comparator<Patient>() {
//                @Override
//                public int compare(Patient patient, Patient patient2) {
//                    if(patient.getSessions() != null && !patient.getSessions().isEmpty() && patient.getSessions().get(0).getStartTime() != null) {
//                        if(patient2.getSessions() != null && !patient2.getSessions().isEmpty() && patient2.getSessions().get(0).getStartTime() != null) {
//                            patient.getSessions().get(0).getStartTime().compareTo(patient2.getSessions().get(0).getStartTime());
//                        }
//                    }
//                    return 0;
//                }
//            });

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

    public static Patient getPatient(String firstName, String lastName) {
        try(Session session = getSessionFactory().openSession()) {
            List<Patient> patients = session.createQuery(
                    "SELECT p FROM Patient p " +
                    "WHERE p.firstName = :firstName " +
                    "AND p.lastName = :lastName " +
                    "AND p.deleted IS NULL ")
                    .setString("firstName", firstName)
                    .setString("lastName", lastName)
                    .list();
            if(patients != null && patients.size() > 0) {
                return patients.get(0);
            }
            return null;
        }
    }

    public static Patient createPatient(Patient patient) {
        try(Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            patient.setCreated(new Date());
            session.save(patient);
            session.flush();
            transaction.commit();
            return patient;
        }
    }

    public static Patient updatePatient(Patient patient) {
        try(Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            patient.setUpdated(new Date());
            session.update(patient);
            session.flush();
            transaction.commit();
            return patient;
        }
    }

    public static void deletePatient(int id) {
        Patient patient = getPatient(id);
        if(patient != null) {
            patient.setDeleted(new Date());
            try(Session session = getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                session.update(patient);
                session.flush();
                transaction.commit();
            }
        }
    }

    public static PatientSession getSession(int id) {
        try(Session session = getSessionFactory().openSession()) {
            return session.get(PatientSession.class, id);
        }
    }

    public static void deleteSession(int id) {
        PatientSession patientSession = getSession(id);
        if(patientSession != null) {
            patientSession.setDeleted(new Date());
            try(Session session = getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                session.update(patientSession);
                session.flush();
                transaction.commit();
            }
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
            Transaction transaction = session.beginTransaction();
            value.setUpdated(new Date());
            session.update(value);
            session.flush();
            transaction.commit();
            log.info("Updated ConfigValue. " + value.getConfigKey() + ": " + value.getConfigValue() + " - " + value.getUpdated());
            return value;
        }
    }

    public static ConfigValue saveConfigValue(ConfigValue value) {
        try(Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            value.setCreated(new Date());
            session.save(value);
            session.flush();
            transaction.commit();
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
            Transaction transaction = session.beginTransaction();
            if(patientSession.getId() == null) {
                session.save(patientSession);
            } else {
                session.update(patientSession);
            }
            session.flush();
            transaction.commit();
            return patientSession;
        }
    }

    public static void shutdown() {
        getSessionFactory().close();
        sessionFactory = null;
    }

    private static class PatientComparator implements Comparator<Patient> {
        @Override
        public int compare(Patient o1, Patient o2) {
            if(o1 != null && o2 != null) {
                if(o1 != null && o2 == null) {
                    return 1;
                }
                if(o2 != null && o1 == null) {
                    return -1;
                }
                if(o1.getLastName() != null && o2.getLastName() != null && o1.getFirstName() != null && o2.getFirstName() != null) {
                    return new CompareToBuilder()
                            .append(o1.getLastName().toLowerCase(), o2.getLastName().toLowerCase())
                            .append(o1.getFirstName().toLowerCase(), o2.getFirstName().toLowerCase())
                            .toComparison();
                } else if(o1.getLastName() != null && o2.getLastName() != null) {
                    return o1.getLastName().compareToIgnoreCase(o2.getLastName());
                }
            }
            return 0;
        }
    }

}
