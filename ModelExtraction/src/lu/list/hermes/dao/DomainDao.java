package lu.list.hermes.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lu.list.hermes.util.*;
import lu.list.hermes.models.*;

public class DomainDao {

    public void addDomain(Domain Domain) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            session.save(Domain);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {
                trns.rollback();
            }
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }

    public void deleteDomain(int Domainid) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            Domain Domain = (Domain) session.load(Domain.class, new Integer(Domainid));
            session.delete(Domain);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {
                trns.rollback();
            }
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }

    public  List<Domain> getAllDomainsByRelid(int  relationid) {
        List<Domain> DomainsubClasses = new ArrayList<Domain>();
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = " Select d  from Domain d  where  d.IDr = :relationid";
            		
            Query query = session.createQuery(queryString);
            query.setParameter("relationid", relationid);

            DomainsubClasses = (List<Domain>) query.list();
            
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return DomainsubClasses;
    }
    public void updateDomain(Domain Domain) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            session.update(Domain);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {
                trns.rollback();
            }
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }

    public List<Domain> getAllDomain() {
        List<Domain> Domains = new ArrayList<Domain>();
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            Domains = session.createQuery("from Domain").list();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return Domains;
    }

    public Domain getDomainById(int Domainid) {
        Domain Domain = null;
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = "from Domain where id = :id";
            Query query = session.createQuery(queryString);
            query.setInteger("id", Domainid);
            Domain = (Domain) query.uniqueResult();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return Domain;
    }
}
