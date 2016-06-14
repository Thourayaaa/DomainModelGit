package lu.list.hermes.controllers;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lu.list.hermes.dao.CorpusDao;
import lu.list.hermes.dao.DocumentDao;
import lu.list.hermes.dao.EntityRelDao;
import lu.list.hermes.dao.RelationDao;
import lu.list.hermes.models.Annotation;
import lu.list.hermes.models.Corpus;
import lu.list.hermes.models.Document;
import lu.list.hermes.models.EntityRel;
import lu.list.hermes.models.Relation;
import lu.list.hermes.util.HibernateUtil;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


/** this class implements functions that write the outputs of the Annotator 
 * and the extractor and save it into .rdf files in the input folders
 * @author thourayabouzidi
 *
 */
/**
 * @author thourayabouzidi
 *
 */

public class NifFilesGenerator {
	final static Logger logger = Logger.getLogger(NifFilesGenerator.class);

	
	
	static public String entete = "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>." +"\n"
			+ "@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> ."+"\n"
			+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> ."+ "\n"
	+"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."+"\n";
	
	
	
	public  String createNIFDoc (Document dc) 	 { 
		    SessionFactory sf = HibernateUtil.getSessionFactory();
		    Session session = sf.openSession();
		    session.beginTransaction();
		
		     String NIFdoc = "<http:/www.list.lu/document"+dc.getIdDoc()+"#char=0,"+dc.getDocText().length()+">\n"
	         +"\t"+"a"+"\t"+ "nif:RFC5147String , nif:Phrase , nif:Context ;"+"\n"+"\t"+
	         "nif:beginIndex  \"0\"^^xsd:nonNegativeInteger;"+"\n \t"
	         +"nif:endIndex \t\""+dc.getDocText().length()+"\"^^xsd:nonNegativeInteger s;"+"\n \t"
	         + "itsrdf:taIdentRef \t <"+dc.geturid()+">;"+"\n \t"
	         +"nif:isString \t\""+dc.getDocText()+"\"^^xsd:string ."+"\n \n";
	        
		     
		     session.getTransaction().commit();
	         session.close();
			 
		 return NIFdoc;
	 }

	
	
	/**Write Nif file for the whole corpus
	 * @param idc
	 * @param path
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public void nifRelationCorpus (int idc,String path, String filename) throws FileNotFoundException  // for the whole corpus
	{
		  SessionFactory sf = HibernateUtil.getSessionFactory();
	      Session session = sf.openSession();
	      session.beginTransaction();
	      CorpusDao cdao = new CorpusDao();
	      Corpus c = cdao.getCorpusById(idc);
	      Set <Document> docs = c.getDocuments();
	      StringBuilder sb = new StringBuilder();
          sb.append(entete);
		  String Docnif = "";
	    for (Document docc : docs)
	    {  
	    	// For each document i create a NIF file and save it into the input path
    	  //starting with creating the doc entete
          String docentet =  createNIFDoc (docc);
          sb.append(docentet);

         String Ann = makeRelationObjSubj ( (int)docc.getIdDoc()); // write in NIF format all Ollie entities
       
        sb.append(Ann);
         Docnif = sb.toString();
        //save output into a file
        
       }
	      PrintWriter out = new PrintWriter(path+"/"+filename+".rdf");
         out.println(Docnif);
         out.close();
         sb.delete(0, sb.length());
     

	     session.getTransaction().commit();
        session.close();
		
	}
	

	/** generate Nif file for one document based on the relation extractor outputs
	 * @param iddocument
	 * @param path
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public void nifRelationDocument (int iddocument,String path, String filename) throws FileNotFoundException  // for the whole corpus
	{
		  SessionFactory sf = HibernateUtil.getSessionFactory();
	      Session session = sf.openSession();
	      session.beginTransaction();
	      DocumentDao cdao = new DocumentDao();
	      Document docc = cdao.getDocumentById(iddocument);
	      StringBuilder sb = new StringBuilder();
          sb.append(entete);
		  String Docnif = "";
	     
	    	// For each document i create a NIF file and save it into the input path
    	  //starting with creating the doc entete
          String docentet =  createNIFDoc (docc);
          sb.append(docentet);

         String Ann = makeRelationObjSubj ( (int)docc.getIdDoc()); // write in NIF format all Ollie entities
       
         sb.append(Ann);
         Docnif = sb.toString();
        //save output into a file
        
       
	      PrintWriter out = new PrintWriter(path+"/"+filename+".rdf");
         out.println(Docnif);
         System.out.println(Docnif);
         out.close();
         sb.delete(0, sb.length());
     

	     session.getTransaction().commit();
        session.close();
		
	}
	
	
	 public String nifAnnotation (Annotation a, Document d, String tool) // this method is for koda output annotations (tool = koda)
	 {   
		    SessionFactory sf = HibernateUtil.getSessionFactory();
		    Session session = sf.openSession();
		    session.beginTransaction();
		
	    	int end = a.getindexa()+ a.getlonga();
		 
		   String NIFAnn ="<http://www.list.lu/document"+d.getIdDoc()+"#"+tool+"/"+a.getidAnn()+">\n \t"
				 +"a nif:Phrase;"+"\n\t"
				 +"nif:referenceContext <http://www.list.lu/"+tool+"/document"+d.getIdDoc()+"#char=0,"+d.getDocText().length()+">; \n \t"
				 +"nif:anchorOf \t \""+a.getAnnotation()+"\"^^xsd:string ;\n \t"
				 +"nif:beginIndex \t \""+a.getindexa()+"\"^^xsd:nonNegativeInteger ; \n \t"+
				 "nif:endIndex \t \""+end+"\"^^xsd:nonNegativeInteger ; \n \t"
				 +"itsrdf:taIdentRef \t <"+a.getdb()+">."+"\n \n";
		 
		     session.getTransaction().commit();
	         session.close();
			
		 return NIFAnn ;
		 
		}
	
	 public String nifEntityObject (EntityRel ent, Document d, String tool, String uri)  // for Ollie Entities
	 {   
		    SessionFactory sf = HibernateUtil.getSessionFactory();
		    Session session = sf.openSession();
		    session.beginTransaction();
		
	    	int end = ent.getindexe()+ ent.getlongent();
		 
		   String NIFAnn ="<http://www.list.lu/document"+d.getIdDoc()+"#"+tool+"/"+ent.getlabel()+"_"+ent.getiDe()+">\n \t"
				 +"a nif:Phrase;"+"\n\t"
				 +"nif:referenceContext <http://www.list.lu/"+tool+"/document"+d.getIdDoc()+"#char=0,"+d.getDocText().length()+">; \n \t"
				 +"nif:anchorOf \t \""+ent.getindexe()+"\"^^xsd:string ;\n \t"
				 +"nif:beginIndex \t \""+ent.getindexe()+"\"^^xsd:nonNegativeInteger ; \n \t"+
				 "nif:endIndex \t \""+end+"\"^^xsd:nonNegativeInteger ; \n \t"
				 +"itsrdf:taIdentRef \t <"+uri+">."+"\n \n";
		 
		     session.getTransaction().commit();
	         session.close();
			
		 return NIFAnn ;
		 
		}
	 public String nifEntitySubject (EntityRel entsubj, Document d, String tool, String r, int idobj, String uri)
	 
	 {
		 
		  int end = entsubj.getindexe() + entsubj.getlongent();
		 
		  String NIFAnn ="<http://www.list.lu/document"+d.getIdDoc()+"#"+tool+"/Subject_"+entsubj.getiDe()+">\n \t"
				 +"a nif:Phrase;"+"\n\t"
				 +"nif:referenceContext <http://www.list.lu/"+tool+"/document"+d.getIdDoc()+"#char=0,"+d.getDocText().length()+">; \n \t"
				 +"nif:anchorOf \t \""+entsubj.getEntitytext()+"\"^^xsd:string ;\n \t"
				 +"nif:beginIndex \t \""+entsubj.getindexe()+"\"^^xsd:nonNegativeInteger ; \n \t"+
				 "nif:endIndex \t \""+end+"\"^^xsd:nonNegativeInteger ; \n \t"
				 +"itsrdf:taIdentRef \t <"+uri+">;"+"\n \t"
				 +"kr: "+r.replaceAll("\\s", "_") + " <http://www.list.lu/document"+d.getIdDoc()+"#"+tool+"/Object_"+idobj+">. \n\n";
		 
		 
		 return NIFAnn ;
		
		 
	 }
	
	 
	 public String makeNifFileAnnotator (Document d, String tool) //this makes the nif format the set of annotations( single document)
	 {
		    SessionFactory sf = HibernateUtil.getSessionFactory();
		    Session session = sf.openSession();
		    session.beginTransaction();
		    Set <Annotation> annots = d.getAnnotations();
		     String Niftext = "";
			 StringBuilder sb = new StringBuilder();
		     
		 for (Annotation a : annots)
		 {
			    // Annotation a, Document d, String tool
	    	     Niftext = nifAnnotation (a,d,tool);
         //concatener les r�sultats
	            sb.append(Niftext);
	     
		 }
	     
	     String S = sb.toString();
	     session.getTransaction().commit();
         session.close();
					 
		 return S;
	 }
	 
	 /**this function is to make sure that we don't add the relation twice    		 

	 * @param rel
	 * @return
	 */
	public static String checkRelationExist(Relation rel) 
	 {  
		  SessionFactory sf = HibernateUtil.getSessionFactory();
	      Session session = sf.openSession();
	      session.beginTransaction();
	     
		 int id =(int)rel.getIDr()-1;
		 RelationDao  rdao = new RelationDao();
		 
		 List <Relation> rels =  rdao.getAllRelation();
		
		 Iterator<Relation> iter = rels.iterator();
		 String relat = "";
		 while ((iter.hasNext()) && (iter.next().getIDr() < id)) {		  
	    	if (iter.next().getrelation().equals(rel.getrelation()))
	    	{
	    		relat = iter.next().getrelation();
	    	}
	      }
	     session.getTransaction().commit();
         session.close();
		 return relat ;
	 }
	

	 
	 
	 public String makeRelationObjSubj ( int iddoc) 
		{

			 String NiftextO = "";
			 String NiftextS ="";
			 
			  SessionFactory sf = HibernateUtil.getSessionFactory();
		      Session session = sf.openSession();
		      session.beginTransaction();
		      StringBuilder sb = new StringBuilder("");
		      //Document : relation , subject , object 
		      DocumentDao ddao = new DocumentDao();
		      Document Dc = ddao.getDocumentById(iddoc);
            
		      Set <Relation> Rels = Dc.getRelations();
		      if (!(Rels.isEmpty()))
		      {
		      for (Relation re : Rels)
		      {
	             

		    	  EntityRelDao edao = new EntityRelDao();
		    	  EntityRel subject =  edao.getEntityRelByLabel("Subject", re);
		    	  EntityRel object	= edao.getEntityRelByLabel("Object", re) ;
		    	 if (!(object == null) && !(subject == null))
		    	 {  String uri1 = subject.geturient()+"_"+subject.getiDe();
		    	  String uri2 = object.geturient()+"_"+object.getiDe();
		    	
		     
		     //Relation should be here i guess 
		       String rexist = checkRelationExist(re);
		       if (rexist.equals(""))
		       { 
		    	    NiftextS= nifEntitySubject(subject,Dc, "Ollie", re.getrelation(),(int)object.getiDe(), uri1);//Subject we should add something here
			       
		            String Relation = "kr:"+ re.getrelationNL().replaceAll("\\s", "_")+"\n \t"
		    		 +"a rdf:Property;"+"\n \t"
		    		 +"rdfs:label \""+re.getrelationNL()+"\". \n";
		     
		     NiftextO = nifEntityObject (object,Dc,"Ollie", uri2);			      //concatener les r�sultats
		     sb.append(Relation);
		     sb.append(NiftextS);
		     sb.append(NiftextO);
		       }
		       else 
		       {
		    	   NiftextS= nifEntitySubject(subject,Dc, "Ollie", re.getrelation(),(int)object.getiDe(), uri1);//Subject we should add something here
		    	   NiftextO = nifEntityObject (object,Dc,"Ollie",uri2);//object
			   
		    	     sb.append(NiftextS);
				     sb.append(NiftextO);
				       
		       }
               
		     
		      }}
		      }
			     String S = sb.toString();

			     session.getTransaction().commit();
		         session.close();
				
		      
			    return S;
			
			
		}
		

	 /** Generate Nif file for the whole koda corpus
	 * @param idc
	 * @param path
	 * @param filename
	 * @param tool
	 * @throws FileNotFoundException
	 */
	public void generateNifCorpusAnnotator (int idc,String path, String filename, String tool) throws FileNotFoundException 
		{     
		      SessionFactory sf = HibernateUtil.getSessionFactory();
		      Session session = sf.openSession();
		      session.beginTransaction();
		      CorpusDao cdao = new CorpusDao();
		      Corpus c = cdao.getCorpusById(idc);
		      Set <Document> docs = c.getDocuments();
	          StringBuilder sb = new StringBuilder();
	          sb.append(entete);
	           String Docnif ="";

		    for (Document docc : docs)
		    {

			    String docentet =  createNIFDoc(docc); //entete du document
	           sb.append(docentet);
	           String Ann = makeNifFileAnnotator (docc, tool) ; // set of annotations
	           sb.append(Ann);
	            Docnif = sb.toString();
	          //save output into a file
	         		          }
		       PrintWriter out = new PrintWriter(path+"/"+filename+".rdf");
	           out.println(Docnif);

	           out.close();
	           sb.delete(0, sb.length());

		    
		     session.getTransaction().commit();
	         session.close();
			
	          
		}
	 

	 
	 
	/** Generate NIF file for one document based on the annotator outputs
	 * @param iddocument
	 * @param path
	 * @param filename
	 * @param tool
	 * @throws FileNotFoundException
	 */
	public void generateNifDocAnnotator (int iddocument,String path, String filename, String tool) throws FileNotFoundException 
	{     
	      SessionFactory sf = HibernateUtil.getSessionFactory();
	      Session session = sf.openSession();
	      session.beginTransaction();
	      DocumentDao cdao = new DocumentDao();
	      Document docc = cdao.getDocumentById(iddocument);
          StringBuilder sb = new StringBuilder();
          sb.append(entete);
           String Docnif ="";

		   String docentet =  createNIFDoc(docc); //entete du document
           sb.append(docentet);
           String Ann = makeNifFileAnnotator (docc, tool) ; // set of annotations
           sb.append(Ann);
            Docnif = sb.toString();
          //save output into a file
         		          
	       PrintWriter out = new PrintWriter(path+"/"+filename+".rdf");
           out.println(Docnif);

           out.close();
           sb.delete(0, sb.length());

	    
	     session.getTransaction().commit();
         session.close();
		
          
}
 

	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	

  
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
