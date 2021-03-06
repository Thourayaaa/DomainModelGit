package lu.list.hermes.controllers;


import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import lu.list.hermes.dao.CorpusDao;
import lu.list.hermes.dao.DocumentDao;
import lu.list.hermes.dao.EntityRelDao;
import lu.list.hermes.dao.RelationDao;
import lu.list.hermes.models.Corpus;
import lu.list.hermes.models.Document;
import lu.list.hermes.models.EntityRel;
import lu.list.hermes.models.Relation;
import lu.list.hermes.util.HibernateUtil;
import edu.knowitall.ollie.Ollie;
import edu.knowitall.ollie.OllieExtraction;
import edu.knowitall.ollie.OllieExtractionInstance;
import edu.knowitall.tool.parse.MaltParser;
import edu.knowitall.tool.parse.graph.DependencyGraph;

/** this class implements Ollie functions that extracts spo from input documents
 * saved into the database
 * @author thourayabouzidi
 *
 */
public class OllieExtractor implements RelationExtractor {
	final static Logger logger = Logger.getLogger(OllieExtractor.class);

    
	// the extractor itself
    private Ollie ollie;
    public String language = "english";
    

    // the parser--a step required before the extractor
    private MaltParser maltParser;
    private static final String MALT_PARSER_FILENAME = "engmalt.linear-1.7.mco";


    // the path of the malt parser model file
    
    public OllieExtractor()   {
        // initialize MaltParser

    	 scala.Option<File> nullOption = scala.Option.apply(null);
         maltParser = new MaltParser(new File(MALT_PARSER_FILENAME));
        // initialize Ollie
        ollie = new Ollie();
    }

    /**
     * returns Ollie extractions from a single sentence.
     * @param sentence
     * @return the set of ollie extractions
     */
    public  Iterable<OllieExtractionInstance> extract(String sentence) {
        // parse the sentence
        DependencyGraph graph = maltParser.dependencyGraph(sentence);
       
        // run Ollie over the sentence and convert to a Java collection
        Iterable<OllieExtractionInstance> extrs = scala.collection.JavaConversions.asJavaIterable(ollie.extract(graph));
          
        return extrs;
    }
    
       
    
    /** Ollie adds white spaces to the subject and object outputs, this function will remove it in order to find the 
     * correct index in the input text
     * @param input
     * @return
     */
    public String cleanollie (String input)
    {input=input.replaceAll("\\s+,", ",");
	  input=input.replaceAll("\\s+:", ":");
	  input=input.replaceAll("\\s+\\.", ".");
	  input=input.replaceAll("\\(\\s+", "\\(");
	  input=input.replaceAll("\n", "\\s");
	  input=input.replaceAll("\t", "");

    	return input;
    }
    /** find the real relation in the text based on the OllieExtraction : ollie outputs 
     * @param extr
     * @return
     */
    public String findRelationNL( OllieExtraction extr)
    {
    	String RelationLN="";
    	
    			 Iterable<edu.knowitall.tool.parse.graph.DependencyNode>  depnod =  
    	         scala.collection.JavaConversions.asJavaIterable( extr.rel().nodes());
    	         edu.knowitall.tool.parse.graph.DependencyNode m = null;
    	         Iterator<edu.knowitall.tool.parse.graph.DependencyNode> it = depnod.iterator();
    	            while (it.hasNext())
    	            { 
    	            	//i need position of start and ending for each node
                		 edu.knowitall.tool.parse.graph.DependencyNode n1 = it.next();
                         RelationLN = RelationLN+" "+n1.text();
    	            	
    	            	
    	            }
                
    	return RelationLN;
    }

	@Override
	public  ArrayList<Relation>  generateSPOFromDocument(Document doc, String language) {
		// TODO Auto-generated method stub
		
		ArrayList<Relation> relations = new ArrayList<Relation>();
		OllieExtractor oll = new OllieExtractor();
		Iterable<OllieExtractionInstance> extrs = oll.extract(doc.getDocText());
        for (OllieExtractionInstance inst : extrs) {
        	 logger.info("Find subject, object and relation in the ollie outputs");
        	 OllieExtraction extr = inst.extr();
        	 String subj = cleanollie(extr.arg1().text());
        	 String obj = cleanollie(extr.arg2().text());
        	 String relnl = findRelationNL( extr);
        	 String rel = extr.rel().text().replaceAll("^\\s+|\\s+$", "");
        	 Relation relation = new Relation();
        	 RelationDao rdao = new RelationDao();
        	 EntityRelDao edao = new EntityRelDao();
        	 //here i am supposed to save set of relations
        	 relation.setrelation(rel);
        	 relation.setrelationNL(relnl);
        	 relation.setDocument(doc);
         	 rdao.addRelation(relation);
         	 
        	 logger.info("inset the subject,object and relation into the database");

        	 EntityRel entityobj = new EntityRel();    //save object as an entity
        	 entityobj.setEntitytext(obj); entityobj.setindexe(doc.getDocText().indexOf(obj));entityobj.setlongent(obj.length());
        	 entityobj.seturient("http://www.list.lu/"+obj.replaceAll("\\s", "_")); entityobj.setlabel("Object");
             entityobj.setRelation(relation);
        	 EntityRel entitysubj = new EntityRel(); //save subject as an entity
        	 entitysubj.setEntitytext(subj); entitysubj.setindexe(doc.getDocText().indexOf(subj));entitysubj.setlongent(subj.length());
        	 entitysubj.seturient("http://www.list.lu/"+subj.replaceAll("\\s", "_")); entitysubj.setlabel("Subject");
             entitysubj.setRelation(relation);

            edao.addEntityRel(entitysubj);
        	edao.addEntityRel(entityobj);
        	 
        }
		return relations;
	}

	@Override
	public void extractRelationsCorpus(int idcorpus) {
		// TODO Auto-generated method stub
		
		 logger.info("Extract relations from the whole corpus");
		 SessionFactory sf = HibernateUtil.getSessionFactory();
		 Session session = sf.openSession();
		 session.beginTransaction();
		 CorpusDao cdao = new CorpusDao();
		 Corpus c = cdao.getCorpusById(idcorpus);
		 
		 Set<lu.list.hermes.models.Document> dc = c.getDocuments();
		 
		 
		 for (lu.list.hermes.models.Document doo :dc)
		 {
			 logger.info("extract relations from the document"+doo.getIdDoc());
			 extractRelationsDocument(doo); //save SPO into database	 
		 }
		
		session.close();
	}

	@Override
	public void extractRelationsDocument( Document doc) {
		// TODO Auto-generated method stub
		ArrayList<Relation> relations = new ArrayList<Relation>();
		Iterable<OllieExtractionInstance> extrs = this.extract(doc.getDocText());
        for (OllieExtractionInstance inst : extrs) {
        	 logger.info("find subject, object and relation in the ollie outputs");
        	 OllieExtraction extr = inst.extr();
        	 String subj = cleanollie(extr.arg1().text());
        	 String obj = cleanollie(extr.arg2().text());
        	 String relnl = findRelationNL( extr);
        	 String rel = extr.rel().text();
        	 Relation relation = new Relation();
        	 RelationDao rdao = new RelationDao();
        	 EntityRelDao edao = new EntityRelDao();
        	 //here i am supposed to save set of relations
        	 relation.setrelation(rel);
        	 relation.setrelationNL(relnl);
        	 relation.setDocument(doc);
        	 logger.info("insert subject object relation into the database");
        	 EntityRel entityobj = new EntityRel();    //save object as an entity
        	 entityobj.setEntitytext(obj); entityobj.setindexe(doc.getDocText().indexOf(obj));entityobj.setlongent(obj.length());
        	 entityobj.seturient("http://www.list.lu/"+obj.replaceAll("\\s", "_")); entityobj.setlabel("Object");
        	 entityobj.setRelation(relation);
             
        	 EntityRel entitysubj = new EntityRel(); //save subject as an entity
        	 entitysubj.setEntitytext(subj); entitysubj.setindexe(doc.getDocText().indexOf(subj));entitysubj.setlongent(subj.length());
        	 entitysubj.seturient("http://www.list.lu/"+subj.replaceAll("\\s", "_")); entitysubj.setlabel("Subject");
        	 entitysubj.setRelation(relation);
        	
        	//save intoDataBase 
        	rdao.addRelation(relation);
        	edao.addEntityRel(entitysubj);
        	edao.addEntityRel(entityobj);
        	 relations.add(relation);

		
        }
	}

    

}
