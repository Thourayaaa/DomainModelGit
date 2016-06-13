package lu.list.hermes.dao;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lu.list.hermes.util.*;
import lu.list.hermes.models.*;
/**
 * This class is Annotation data access object that connects the class Annotation
 * to the Database
 * @author thourayabouzidi
 *
 */
public class AnnotationDao {

    public void addAnnotation(Annotation Annotation) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            session.save(Annotation);
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

    public void deleteAnnotation(int Annotationid) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            Annotation Annotation = (Annotation) session.load(Annotation.class, new Integer(Annotationid));
            session.delete(Annotation);
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

    public void updateAnnotation(Annotation Annotation) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            session.update(Annotation);
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

    public List<Annotation> getAllAnnotation() {
        List<Annotation> Annotations = new ArrayList<Annotation>();
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            Annotations = session.createQuery("from Annotation").list();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return Annotations;
    }
    public  List<Annotation> getAllAnnotationByCorpus(String  CorpusName) {
        List<Annotation> Annotations = new ArrayList<Annotation>();
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = " Select a  from Annotation a ,Document d, Corpus c  where a.document = d"
            		+ " and d.corpus = c and c.CorpusName = :CorpusName" ;
            Query query = session.createQuery(queryString);
            query.setParameter("CorpusName", CorpusName);

            Annotations = (List<Annotation>) query.list();
            
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return Annotations;
    }

    public Annotation getAnnotationById(int Annotationid) {
        Annotation Annotation = null;
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = "from Annotation where id = :id";
            Query query = session.createQuery(queryString);
            query.setInteger("id", Annotationid);
            Annotation = (Annotation) query.uniqueResult();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return Annotation;
    }
    
    
    
    
    
    
    
    
    
    
    
}
