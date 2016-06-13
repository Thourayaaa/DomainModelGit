package lu.list.hermes.util;

import java.io.File;

import lu.list.hermes.controllers.ConnectionAnnotRelation;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();
	final static Logger logger = Logger.getLogger(HibernateUtil.class);


    private static SessionFactory buildSessionFactory() {
        try {
        	File f = new File("resources/ hibernate.cfg.xml");
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure(f).buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            logger.error("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}