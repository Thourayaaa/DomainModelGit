package lu.list.hermes.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import lu.list.hermes.util.*;
import lu.list.hermes.models.*;

public class EntityRelDao {

    public void addEntityRel(EntityRel entit) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            session.save(entit);
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

    public void deleteEntityRel(int ident) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            EntityRel entityrel = (EntityRel) session.load(EntityRel.class, new Integer(ident));
            session.delete(entityrel);
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

    public void updateEntityRel(EntityRel entityrel) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            session.update(entityrel);
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

    public List<EntityRel> getAllEntityRel() {
        List<EntityRel> entities = new ArrayList<EntityRel>();
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            entities = session.createQuery("from EntityRel").list();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return entities;
    }
    
    public  List<EntityRel> getAllEntitiesByCorpus(String  CorpusName) {
        List<EntityRel> entities = new ArrayList<EntityRel>();
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = " Select e  from EntityRel e ,Document d, Corpus c, Relation r  where  e.relation = r"
            		+ " and r.document = d"
            		+ " and d.corpus = c and c.CorpusName = :CorpusName" ;
            Query query = session.createQuery(queryString);
            query.setParameter("CorpusName", CorpusName);

          entities = (List<EntityRel>) query.list();
            
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return entities;
    }

    
    public List<EntityRel> getAllEntityRelBylabel(String label) {
        List<EntityRel> entities = new ArrayList<EntityRel>();
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = "from EntityRel where label = :label";
            Query query = session.createQuery(queryString);
            query.setParameter("label", label);

            entities = (List<EntityRel>) query.list();

            
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return entities;
    }
   
    
    public EntityRel getEntityRelByLabel(String label, Relation relation) {
    	EntityRel entityrel = null;
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = "from EntityRel where label = :label and relation = :relation";
            Query query = session.createQuery(queryString);
            query.setParameter("label", label);
            query.setParameter("relation", relation);

            entityrel = (EntityRel) query.uniqueResult();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return entityrel;
        }

    public EntityRel getEntityRelById(int ident) {
    	EntityRel entityrel = null;
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = "from EntityRel where id = :id";
            Query query = session.createQuery(queryString);
            query.setInteger("id", ident);
            entityrel = (EntityRel) query.uniqueResult();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return entityrel;
    }
}
