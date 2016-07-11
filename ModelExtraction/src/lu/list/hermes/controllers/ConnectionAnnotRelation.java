package lu.list.hermes.controllers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import lu.list.hermes.dao.AnnotationDao;
import lu.list.hermes.dao.EntityRelDao;
import lu.list.hermes.dao.RelationDao;
import lu.list.hermes.models.Annotation;
import lu.list.hermes.models.EntityRel;
import lu.list.hermes.models.Relation;
import lu.list.hermes.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;


/** this class connects the outputs of the Annotator and the relation extractor 
 * in order to find relations between annotations
 * @author thourayabouzidi
 *
 */
public class ConnectionAnnotRelation {
	final static Logger logger = Logger.getLogger(ConnectionAnnotRelation.class);
  
	
	String entete = "@prefix owl:  <http://www.w3.org/2002/07/owl#>.\n"
			+"@prefix kr:  <http://www.list.lu/kr#>. \n \n"; // for .n3 file koda and ollie
	
	String entete1 = "@prefix kr:  <http://www.list.lu/kr#>. \n \n";  //for .n3 only koda
	
    
	
	/** this function gives an annotation list and removes duplications
	 * @return List<Annotation>
	 */
	public  List<Annotation> allAnnotationOnce (String corpusName)
     {
		AnnotationDao adao = new AnnotationDao();
		List<Annotation> annot = adao.getAllAnnotationByCorpus("corpusdomain"); 
		List<Annotation> annotintermediare = adao.getAllAnnotationByCorpus("corpusdomain");
		for (Annotation a : annotintermediare)
		{
			//test if there is another similar annotation
			int id = (int) a.getidAnn();
			String annotation = a.getAnnotation();
			for (int i = id ; i<annot.size();i++)
			{
				if (annotation.equals(annot.get(i).getAnnotation()))
				{
					annot.remove(annot.get(i));
					
				}
			}
			
        }

		
		
		return annot;
    	 
     }
	/** this functions searches for a match between annotations and subjects
	 * @param ido
	 * @return 
	 */
	
	public List<Integer> searchKodaSubject (Annotation a)
	{
		logger.info("Search for annotation in subjects : that is why i had to add a label to the entity table");

		SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    EntityRelDao entdao = new EntityRelDao();
	    List<EntityRel> Listsubj = entdao.getAllEntityRelBylabel("Subject");
	    List<Integer> ListSubjKoda = new ArrayList<Integer>(); // koda annotations id 
	    for (EntityRel entity : Listsubj)
	    {
	    	if (entity.getEntitytext().equals(a.getAnnotation()))
	    	{
	    		 ListSubjKoda.add((int)entity.getiDe());
	    		 
	    	}
	    	
	    }
	    
	    
	    session.getTransaction().commit();
        session.close();
		
		return ListSubjKoda;
		
	}
	
	
	
	
	/**if a match between annotations and subjects is found, then look if there is a match between annotation and objects
	 * @param ido
	 * @return
	 */
	public List<Integer> searchObjAnnotator (int ido)
	{
		logger.info("Search for objects in annotations : that is why i had to add a label to the entity table");

		 EntityRelDao edao =  new EntityRelDao();
		 EntityRel obj = edao.getEntityRelById(ido);
		 SessionFactory sf = HibernateUtil.getSessionFactory();
         Session session = sf.openSession();
         session.beginTransaction();
         AnnotationDao adao = new AnnotationDao();
         List <Annotation> Listann = adao.getAllAnnotation();
         List<Integer> Listobj = new ArrayList<Integer>();
         
         for (Annotation a : Listann)
	
         {    if (!(obj.getEntitytext() == null))
         {
             if (a.getAnnotation().equals(obj.getEntitytext()))
             {
            	 Listobj.add((int) a.getidAnn());
             }
        	 
         }}
         
 	    session.getTransaction().commit();
         session.close();
 		
 	
		return Listobj;
	}
	
	/** Write the match using owl:SameAs between annotations and the relation extractor outputs(sujbects and objects)
	 * @param idrel
	 * @param a1
	 * @param a2
	 * @param iddo
	 * @return
	 */
	public String writeRelation (int idrel, Annotation a1,Annotation a2, int iddo) 
	
	{
		RelationDao rdao = new RelationDao();
		String relationKoda = "<http://www.list.lu/kr/document"+a1.getDocument().getCorpus().getIDc()+"#koda/"
	+a1.getidAnn()+"> \t owl:sameAs \t <http://www.list.lu/kr/document"+iddo+"#ollie/Subject_"+idrel+">. \n"
				+ "<http://www.list.lu/kr/document"+a2.getDocument().getCorpus().getIDc()+"#koda/"
	+a2.getidAnn()+"> \t owl:sameAs \t <http://www.list.lu/kr/document"+iddo+"#ollie/Object_"+idrel+">. \n"
						+"<"+a1.getdb() +">\t"+ "kr:"+rdao.getRelationById(idrel).getrelationNL().replaceAll("\\s", "_")+"\t <"+a2.getdb()+">. \n \n";
				
		return relationKoda;
	}
	
	public String writedbRelation (Annotation a1, Annotation a2, int idrel)
	{
		RelationDao  rdao = new RelationDao();
		String relationdb = "<"+a1.getdb() +">\t"+ "kr:"+rdao.getRelationById(idrel).getrelationNL().replaceAll("\\s", "_")+"\t <"+a2.getdb()+">.\n";
	    return relationdb;
	}
	

	
	/** This class tests if the match is found is the database then write relations between annotations
	 * in an output files .n3 into the input folders 
	 * @param idcc
	 * @param pathrelKodaoll
	 * @param pathOnlykoda
	 * @throws FileNotFoundException
	 */
	public void writeRelationfile (int idcc, String pathrelKodaoll, String pathOnlykoda, String corpusname) throws FileNotFoundException
	{
		 SessionFactory sf = HibernateUtil.getSessionFactory();
         Session session = sf.openSession();
         session.beginTransaction();
         AnnotationDao adao = new AnnotationDao();
         List <Annotation> Listann = allAnnotationOnce(corpusname) ;
           	     StringBuilder sbkoda = new StringBuilder();
	     
   		 StringBuilder sbolliekoda = new StringBuilder();
   		 sbolliekoda.append(entete);
   		 sbkoda.append(entete1);
   		 
         for (Annotation a : Listann) //this is for the subject research
	
         { StringBuilder sb = new StringBuilder();
         
           StringBuilder sb1 = new StringBuilder();
        

	        String RelKo ="";
	        String Reldb = "";

        	 List <Integer> LsubjKoda = searchKodaSubject(a);
        	 if (!(LsubjKoda.isEmpty()))
        	 {
        		 //then look for the object in koda
        		 for (int id : LsubjKoda) // those are spo ids !
        		 {
        			 
        			 List<Integer> LobjKoda  = searchObjAnnotator (id+1); //returns the list of annotations like objects 
        			if (LobjKoda.isEmpty() == false)
        			{
        				
        				//now we are sure we have a relation detected

        				for (int idko: LobjKoda)
        				{   
        					logger.info("the match is found: write relations between annotations");
        					Annotation a1 = adao.getAnnotationById(idko);
        					RelationDao  rdao = new RelationDao();
        					EntityRelDao entdao = new EntityRelDao();
        					Relation rel = entdao.getEntityRelById(id).getRelation();
        					int idrelation = (int) rel.getIDr();
        					int iddco = (int)rel.getDocument().getIdDoc();
        					 RelKo = writeRelation ( idrelation, a,a1, iddco) ;
        					 Reldb=writedbRelation(a,a1, idrelation);
          					 logger.info("********************************************************"+RelKo);

        					 sb.append(RelKo);
        					 sb1.append(Reldb);
        				}
        			}
        		
        			sbolliekoda.append(sb.toString());
    				sbkoda.append(sb1.toString());
    				sb.delete(0, sb.length());
    				sb1.delete(0, sb1.length());

    				

        		 }
        		 
        	 }
        	 
         }
         logger.info("Write the output files");
         PrintWriter out = new PrintWriter(pathrelKodaoll+"/RelationKodaOllie.txt");
         out.println(sbolliekoda.toString());
         out.close();
         
         PrintWriter out1 = new PrintWriter(pathOnlykoda+"/JustKodaRel.txt");
	     out1.println(sbkoda.toString());
	     out1.close();
         logger.info(" done Writing the output files");


         session.getTransaction().commit();
         session.close();
 		
		
	}
	
	}

	

